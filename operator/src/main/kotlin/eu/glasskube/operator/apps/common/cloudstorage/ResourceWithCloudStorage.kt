package eu.glasskube.operator.apps.common.cloudstorage

import com.fasterxml.jackson.annotation.JsonIgnore

interface ResourceWithCloudStorage {
    @get:JsonIgnore
    val backupResourceName: String

    @get:JsonIgnore
    val backupResourceLabels: Map<String, String>

    fun getSpec(): HasCloudStorageSpec
}
