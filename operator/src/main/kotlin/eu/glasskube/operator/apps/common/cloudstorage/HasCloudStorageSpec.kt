package eu.glasskube.operator.apps.common.cloudstorage

import com.fasterxml.jackson.annotation.JsonIgnore

interface HasCloudStorageSpec {
    @get:JsonIgnore
    val cloudStorage: CloudStorageSpec?
}
