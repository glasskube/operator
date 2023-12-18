package eu.glasskube.kubernetes.api.model.rbac

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.rbac.RoleRefBuilder

@KubernetesDslMarker
class ClusterRoleRefDsl private constructor() : AbstractRoleRefDsl() {
    override val builder: RoleRefBuilder =
        RoleRefBuilder()
            .withApiGroup("rbac.authorization.k8s.io")
            .withKind("ClusterRole")

    companion object {
        fun (ClusterRoleRefDsl.() -> Unit).build() = ClusterRoleRefDsl().apply(this).build()
    }
}
