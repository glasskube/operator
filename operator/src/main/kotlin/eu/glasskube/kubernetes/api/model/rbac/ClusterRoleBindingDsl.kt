package eu.glasskube.kubernetes.api.model.rbac

import eu.glasskube.kubernetes.api.model.MetadataDsl
import eu.glasskube.kubernetes.api.model.MetadataDsl.Companion.build
import eu.glasskube.kubernetes.api.model.rbac.ClusterRoleRefDsl.Companion.build
import eu.glasskube.kubernetes.api.model.rbac.SubjectsDsl.Companion.build
import io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding
import io.fabric8.kubernetes.api.model.rbac.ClusterRoleBindingBuilder

inline fun clusterRoleBinding(block: ClusterRoleBindingDsl.() -> Unit) = ClusterRoleBindingDsl().apply(block).build()

class ClusterRoleBindingDsl {
    private val builder = ClusterRoleBindingBuilder(true)

    fun metadata(block: MetadataDsl.() -> Unit) {
        builder.withMetadata(block.build())
    }

    fun clusterRoleRef(block: ClusterRoleRefDsl.() -> Unit) {
        builder.withRoleRef(block.build())
    }

    fun subjects(block: SubjectsDsl.() -> Unit) {
        builder.withSubjects(block.build())
    }

    fun build(): ClusterRoleBinding = builder.build()
}
