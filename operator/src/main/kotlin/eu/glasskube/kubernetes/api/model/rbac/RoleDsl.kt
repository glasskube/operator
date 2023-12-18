package eu.glasskube.kubernetes.api.model.rbac

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import eu.glasskube.kubernetes.api.model.MetadataDsl
import eu.glasskube.kubernetes.api.model.MetadataDsl.Companion.build
import io.fabric8.kubernetes.api.model.rbac.Role
import io.fabric8.kubernetes.api.model.rbac.RoleBuilder

inline fun role(block: RoleDsl.() -> Unit): Role = RoleDsl().apply(block).build()

@KubernetesDslMarker
class RoleDsl {
    private val builder: RoleBuilder = RoleBuilder()

    fun metadata(block: MetadataDsl.() -> Unit) {
        builder.withMetadata(block.build())
    }

    fun rules(block: PolicyRulesDsl.() -> Unit) {
        builder.withRules(PolicyRulesDsl().apply(block).build())
    }

    fun build(): Role = builder.build()
}
