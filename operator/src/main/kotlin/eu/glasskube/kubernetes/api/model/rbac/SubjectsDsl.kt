package eu.glasskube.kubernetes.api.model.rbac

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.rbac.Subject

@KubernetesDslMarker
class SubjectsDsl private constructor() {
    private val subjects = mutableListOf<Subject>()

    fun subject(block: SubjectDsl.() -> Unit) {
        subjects += SubjectDsl().apply(block).build()
    }

    fun build(): List<Subject> = subjects

    companion object {
        fun (SubjectsDsl.() -> Unit).build() = SubjectsDsl().apply(this).build()
    }
}
