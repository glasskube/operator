package eu.glasskube.operator.apps.common

interface ResourceWithUpdatesSpec {
    fun getSpec(): HasUpdatesSpec
}
