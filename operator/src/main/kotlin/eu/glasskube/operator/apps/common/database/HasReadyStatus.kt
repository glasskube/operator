package eu.glasskube.operator.apps.common.database

import com.fasterxml.jackson.annotation.JsonIgnore

interface HasReadyStatus {
    @get:JsonIgnore
    val isReady: Boolean
}
