package eu.glasskube.kubernetes.api.model.extensions

import io.fabric8.kubernetes.api.model.networking.v1.*


inline fun ingress(block: Ingress.() -> Unit) = Ingress().apply(block)
inline fun ingressSpec(block: IngressSpec.() -> Unit) = IngressSpec().apply(block)
inline fun ingressRule(block: IngressRule.() -> Unit) = IngressRule().apply(block)
fun ingressRuleValue(vararg paths: HTTPIngressPath) = HTTPIngressRuleValue(paths.asList())

fun ingressPath(path: String, pathType: String? = null, backend: IngressBackend) =
    HTTPIngressPath(backend, path, pathType)

fun ingressBackend(serviceName: String, servicePort: String) =
    IngressBackend(null, IngressServiceBackend(serviceName, ServiceBackendPort(servicePort, null)))

fun ingressBackend(serviceName: String, servicePort: Int) =
    IngressBackend(null, IngressServiceBackend(serviceName, ServiceBackendPort(null, servicePort)))
