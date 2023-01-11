package eu.glasskube.operator

import java.security.SecureRandom
import java.util.Random
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class OperatorConfiguration {
    @Produces
    fun random(): Random = SecureRandom.getInstanceStrong()
}
