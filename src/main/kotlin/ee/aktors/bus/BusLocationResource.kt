package ee.aktors.bus

import ee.aktors.bus.locations.BusLocationService
import io.smallrye.mutiny.Multi
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Path("/api/locations")
class BusLocationResource(val locationService: BusLocationService) {

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun stream(): Multi<String> = locationService.locations()
}