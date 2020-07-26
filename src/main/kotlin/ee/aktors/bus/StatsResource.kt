package ee.aktors.bus

import ee.aktors.bus.stats.Stats
import ee.aktors.bus.stats.StatsService
import io.smallrye.mutiny.Uni
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/api/stats")
class StatsResource(val statsService: StatsService) {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ping")
    fun ping() = "OK"

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun stats(): Uni<Stats> = statsService.stats()
}