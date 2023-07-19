package eu.glasskube.kubernetes.api.model.rbac

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.rbac.RoleRefBuilder

@KubernetesDslMarker
class RoleRefDsl private constructor() : AbstractRoleRefDsl() {
    override val builder: RoleRefBuilder =
        RoleRefBuilder(true)
            .withApiGroup("rbac.authorization.k8s.io")
            .withKind("Role")

    companion object {
        fun (RoleRefDsl.() -> Unit).build() = RoleRefDsl().apply(this).build()
    }
}
