package eu.glasskube.kubernetes.api.model.rbac

import io.fabric8.kubernetes.api.model.rbac.RoleRef
import io.fabric8.kubernetes.api.model.rbac.RoleRefBuilder

abstract class AbstractRoleRefDsl {
    protected abstract val builder: RoleRefBuilder

    fun name(name: String) {
        builder.withName(name)
    }

    fun build(): RoleRef = builder.build()
}
