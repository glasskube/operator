package eu.glasskube.operator

object Labels {
    const val NAME = "app.kubernetes.io/name"
    const val INSTANCE = "app.kubernetes.io/instance"
    const val VERSION = "app.kubernetes.io/version"
    const val MANAGED_BY = "app.kubernetes.io/managed-by"
    const val COMPONENT = "app.kubernetes.io/component"
    const val PART_OF = "app.kubernetes.io/part-of"
    const val MANAGED_BY_GLASSKUBE = "$MANAGED_BY=glasskube-operator"

    fun resourceLabels(
        name: String,
        instance: String,
        partOf: String? = null,
        version: String? = null,
        component: String? = null
    ) = mutableMapOf(
        MANAGED_BY to "glasskube-operator",
        NAME to name,
        INSTANCE to instance
    ).also { labels ->
        if (version != null) labels += VERSION to version
        if (component != null) labels += COMPONENT to component
        if (partOf != null) labels += PART_OF to partOf
    }.toMap()

    fun resourceLabelSelector(
        name: String,
        instance: String,
        partOf: String? = null
    ) = mutableMapOf(
        NAME to name,
        INSTANCE to instance
    ).also {
        if (partOf != null) it += PART_OF to partOf
    }.toMap()
}
