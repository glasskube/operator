package eu.glasskube.operator.boot

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("velero")
class VeleroProperties(
    val namespace: String
) {
    init {
        INSTANCE = this
    }

    companion object {
        /**
         * **Do not use!** Please only use this field as a last resort when working in a context where dependency
         * injection is not available.
         *
         * Holds a reference to the instance of [VeleroProperties] that was created last.
         * Since Spring only creates instances for configuration properties once, it can be assumed that this instance
         * is the same one that would be available via dependency injection.
         */
        lateinit var INSTANCE: VeleroProperties
            private set
    }
}
