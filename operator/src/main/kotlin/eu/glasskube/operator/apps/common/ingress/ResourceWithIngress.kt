package eu.glasskube.operator.apps.common.ingress

interface ResourceWithIngress {
    fun getSpec(): HasIngressSpec
}
