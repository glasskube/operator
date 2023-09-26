package eu.glasskube.operator.apps.common

interface HasUpdatesSpec {
    val updates: UpdatesSpec<*>?
}
