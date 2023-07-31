package eu.glasskube.kubernetes.api.model.rbac

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.rbac.Subject
import io.fabric8.kubernetes.api.model.rbac.SubjectBuilder

@KubernetesDslMarker
class SubjectDsl {
    private val builder = SubjectBuilder()

    fun apiGroup(apiGroup: String) {
        builder.withApiGroup(apiGroup)
    }

    fun kind(kind: String) {
        builder.withKind(kind)
    }

    fun name(name: String) {
        builder.withName(name)
    }

    fun namespace(namespace: String?) {
        builder.withNamespace(namespace)
    }

    fun build(): Subject = builder.build()
}
