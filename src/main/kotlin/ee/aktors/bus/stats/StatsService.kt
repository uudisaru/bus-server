package ee.aktors.bus.stats

import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StatsService {
    fun stats(): Uni<Stats> {
        return Uni.createFrom().item(Stats(0, 0, 0.0))
    }
}