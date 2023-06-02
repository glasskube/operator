package eu.glasskube.operator.infra.postgres

import com.fasterxml.jackson.annotation.JsonInclude
import io.fabric8.kubernetes.api.model.LocalObjectReference

// TODO: Add properties once needed:
//  - import
//  - postInitApplicationSQLRefs
@JsonInclude(JsonInclude.Include.NON_NULL)
data class BootstrapInitDB(
    val database: String,
    val owner: String? = null,
    val secret: LocalObjectReference? = null,
    val options: List<String>? = null,
    val dataChecksums: Boolean? = null,
    val encoding: String? = null,
    val localeCollate: String? = null,
    val localeCType: String? = null,
    val walSegmentSize: Int? = null,
    val postInitSQL: List<String>? = null,
    val postInitApplicationSQL: List<String>? = null,
    val postInitTemplateSQL: List<String>? = null
)
