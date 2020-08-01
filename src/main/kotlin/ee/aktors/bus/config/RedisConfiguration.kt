package ee.aktors.bus.config

import io.quarkus.arc.config.ConfigProperties
import java.util.*

@ConfigProperties(prefix = "redis")
class RedisConfiguration {
    lateinit var host: Optional<String>
    lateinit var port: Optional<Int>
    lateinit var username: Optional<String>
    lateinit var password: Optional<String>
}
