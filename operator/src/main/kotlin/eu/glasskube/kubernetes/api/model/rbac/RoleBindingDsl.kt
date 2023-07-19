package eu.glasskube.kubernetes.api.model.rbac

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import eu.glasskube.kubernetes.api.model.MetadataDsl
import eu.glasskube.kubernetes.api.model.MetadataDsl.Companion.build
import eu.glasskube.kubernetes.api.model.rbac.ClusterRoleRefDsl.Companion.build
import eu.glasskube.kubernetes.api.model.rbac.RoleRefDsl.Companion.build
import eu.glasskube.kubernetes.api.model.rbac.SubjectsDsl.Companion.build
import io.fabric8.kubernetes.api.model.rbac.RoleBinding
import io.fabric8.kubernetes.api.model.rbac.RoleBindingBuilder

inline fun roleBinding(block: RoleBindingDsl.() -> Unit) = RoleBindingDsl().apply(block).build()

@KubernetesDslMarker
class RoleBindingDsl {
    private val builder = RoleBindingBuilder(true)

    fun metadata(block: MetadataDsl.() -> Unit) {
        builder.withMetadata(block.build())
    }

    fun roleRef(block: RoleRefDsl.() -> Unit) {
        builder.withRoleRef(block.build())
    }

    fun clusterRoleRef(block: ClusterRoleRefDsl.() -> Unit) {
        builder.withRoleRef(block.build())
    }

    fun subjects(block: SubjectsDsl.() -> Unit) {
        builder.withSubjects(block.build())
    }

    fun build(): RoleBinding = builder.build()
}
