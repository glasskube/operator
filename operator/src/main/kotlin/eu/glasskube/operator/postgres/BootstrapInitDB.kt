package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.LocalObjectReference

// TODO: Add properties once needed:
//  - import
//  - postInitApplicationSQLRefs
data class BootstrapInitDB(
    var database: String,
    var owner: String,
    var secret: LocalObjectReference? = null,
    var options: List<String>? = null,
    var dataChecksums: Boolean? = null,
    var encoding: String? = null,
    var localeCollate: String? = null,
    var localeCType: String? = null,
    var walSegmentSize: Int? = null,
    var postInitSQL: List<String>? = null,
    var postInitApplicationSQL: List<String>? = null,
    var postInitTemplateSQL: List<String>? = null,
)
