package ee.aktors.bus.locations

import ee.aktors.bus.config.RedisConfiguration
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.redis.client.*
import io.vertx.redis.client.RedisOptions
import io.vertx.redis.client.ResponseType
import java.nio.charset.StandardCharsets
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class BusLocationService(val vertx: Vertx, final val redisConfiguration: RedisConfiguration) {
    private val log: Logger = LoggerFactory.getLogger(BusLocationService::class.java)
    private val maxReconnectRetries = 16

    private val options: RedisOptions = RedisOptions()
    private val stream: BroadcastProcessor<String>;

    private var client: RedisConnection? = null

    init {

        val host = redisConfiguration.host.orElse("localhost")
        val port = redisConfiguration.port.orElse(6379)
        val username = redisConfiguration.username.orElse("")
        var auth = "";
        redisConfiguration.password.ifPresent {
            auth = "${username ?: ""}:${it}@"
        }

        options.endpoints = listOf("redis://${auth}${host}:${port}")
        println(options.endpoints[0])

        createRedisClient(Handler {
            if (it.succeeded()) {
                log.info("Connected to Redis")
            } else if (it.failed()) {
                log.warn("Redis connection failed", it.cause())
            }
        })

        stream = BroadcastProcessor.create<String>();
    }

    fun locations(): Multi<String> {
        return stream.onOverflow().buffer(1000)
    }

    /**
     * Will create a redis client and setup a reconnect handler when there is
     * an exception in the connection.
     */
    private fun createRedisClient(handler: Handler<AsyncResult<RedisConnection>>) {
        Redis.createClient(vertx, options)
                .connect { onConnect: AsyncResult<RedisConnection> ->
                    if (onConnect.succeeded()) {
                        val channel = "bus::stream"
                        client = subscribe(onConnect, channel)
                    }
                    // allow further processing
                    handler.handle(onConnect)
                }
    }

    private fun onMessage(response: Response) {
        val msg = response.get(2);
        if (msg.type() == ResponseType.BULK) {
            val payload = msg.toString(StandardCharsets.UTF_8);
            stream.onNext(payload)
        }
    }

    private fun subscribe(onConnect: AsyncResult<RedisConnection>, channel: String): RedisConnection? {
        val c = onConnect.result()
        c.handler(this::onMessage)
        c.send(Request.cmd(Command.SUBSCRIBE).arg(channel)) {
            if (it.succeeded()) {
                log.info("Subscribed to {0}", channel)
            } else if (it.failed()) {
                log.warn("Cannot connect to {0}", channel, it.cause())
            }
        }
        // make sure the client is reconnected on error
        c.exceptionHandler { _ ->
            // attempt to reconnect
            attemptReconnect(0)
        }
        return c
    }

    /**
     * Attempt to reconnect up to MAX_RECONNECT_RETRIES
     */
    private fun attemptReconnect(retry: Int) {
        if (retry > maxReconnectRetries) {
            // we should stop now, as there's nothing we can do.
        } else {
            // retry with backoff up to 10240 ms
            val backoff = (Math.pow(2.0, Math.min(retry, 10).toDouble()) * 10).toLong()
            vertx.setTimer(backoff) {
                createRedisClient(Handler {
                    if (it.failed()) {
                        attemptReconnect(retry + 1)
                    }
                })
            }
        }
    }
}