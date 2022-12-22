package eu.glasskube.operator.postgres

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.fabric8.kubernetes.api.model.LocalObjectReference

// TODO: Add properties once needed:
//  - import
//  - postInitApplicationSQLRefs
data class BootstrapInitDB @JsonCreator constructor(
    @JsonProperty("database")
    val database: String,
    @JsonProperty("owner")
    val owner: String,
    @JsonProperty("secret")
    val secret: LocalObjectReference? = null,
    @JsonProperty("options")
    val options: List<String>? = null,
    @JsonProperty("dataChecksums")
    val dataChecksums: Boolean? = null,
    @JsonProperty("encoding")
    val encoding: String? = null,
    @JsonProperty("localeCollate")
    val localeCollate: String? = null,
    @JsonProperty("localeCType")
    val localeCType: String? = null,
    @JsonProperty("walSegmentSize")
    val walSegmentSize: Int? = null,
    @JsonProperty("postInitSQL")
    val postInitSQL: List<String>? = null,
    @JsonProperty("postInitApplicationSQL")
    val postInitApplicationSQL: List<String>? = null,
    @JsonProperty("postInitTemplateSQL")
    val postInitTemplateSQL: List<String>? = null,
)
