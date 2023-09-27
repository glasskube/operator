package eu.glasskube.operator.generic.dependent.postgres.backup

fun interface DefaultRetentionPolicyProvider<P> {
    fun P.getDefaultRetentionPolicy(): String?
}
