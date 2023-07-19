package eu.glasskube.kubernetes.api.model.rbac

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.rbac.PolicyRule
import io.fabric8.kubernetes.api.model.rbac.PolicyRuleBuilder

@KubernetesDslMarker
class PolicyRuleDsl {
    private val builder: PolicyRuleBuilder = PolicyRuleBuilder(true)

    fun apiGroups(vararg apiGroups: String) {
        builder.withApiGroups(apiGroups.asList())
    }

    fun resources(vararg resources: String) {
        builder.withResources(resources.asList())
    }

    fun verbs(vararg verbs: String) {
        builder.withVerbs(verbs.asList())
    }

    fun resourceNames(resourceNames: List<String>) {
        builder.withResourceNames(resourceNames)
    }

    fun resourceNames(vararg resourceNames: String) {
        resourceNames(resourceNames.asList())
    }

    fun build(): PolicyRule = builder.build()
}
