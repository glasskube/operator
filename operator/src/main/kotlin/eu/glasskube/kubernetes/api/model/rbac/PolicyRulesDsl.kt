package eu.glasskube.kubernetes.api.model.rbac

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.rbac.PolicyRule

@KubernetesDslMarker
class PolicyRulesDsl {
    private val rules = mutableListOf<PolicyRule>()

    fun rule(block: PolicyRuleDsl.() -> Unit) {
        rules += PolicyRuleDsl().apply(block).build()
    }

    fun build(): List<PolicyRule> = rules
}
