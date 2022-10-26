package eu.glasskube.kubernetes.api.model.extensions

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressPath
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressRuleValue
import io.fabric8.kubernetes.api.model.networking.v1.Ingress
import io.fabric8.kubernetes.api.model.networking.v1.IngressBackend
import io.fabric8.kubernetes.api.model.networking.v1.IngressRule
import io.fabric8.kubernetes.api.model.networking.v1.IngressServiceBackend
import io.fabric8.kubernetes.api.model.networking.v1.IngressSpec
import io.fabric8.kubernetes.api.model.networking.v1.ServiceBackendPort

inline fun ingress(block: (@KubernetesDslMarker Ingress).() -> Unit) =
    Ingress().apply(block)

inline fun Ingress.spec(block: (@KubernetesDslMarker IngressSpec).() -> Unit) {
    spec = IngressSpec().apply(block)
}

inline fun ingressRule(block: (@KubernetesDslMarker IngressRule).() -> Unit) =
    IngressRule().apply(block)

fun ingressRuleValue(vararg paths: HTTPIngressPath) =
    HTTPIngressRuleValue(paths.asList())

fun ingressPath(path: String, pathType: String? = null, backend: IngressBackend) =
    HTTPIngressPath(backend, path, pathType)

fun ingressBackend(serviceName: String, servicePort: String) =
    IngressBackend(null, IngressServiceBackend(serviceName, ServiceBackendPort(servicePort, null)))

fun ingressBackend(serviceName: String, servicePort: Int) =
    IngressBackend(null, IngressServiceBackend(serviceName, ServiceBackendPort(null, servicePort)))
