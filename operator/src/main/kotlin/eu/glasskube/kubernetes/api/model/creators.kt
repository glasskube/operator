package eu.glasskube.kubernetes.api.model

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.ContainerPort
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.LabelSelector
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.PodSpec
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.ServicePort
import io.fabric8.kubernetes.api.model.ServiceSpec

inline fun objectMeta(block: (@KubernetesDslMarker ObjectMeta).() -> Unit) =
    ObjectMeta().apply(block)

inline fun HasMetadata.metadata(block: (@KubernetesDslMarker ObjectMeta).() -> Unit) {
    metadata = objectMeta(block)
}

inline fun labelSelector(block: (@KubernetesDslMarker LabelSelector).() -> Unit) =
    LabelSelector().apply(block)

inline fun PodTemplateSpec.metadata(block: (@KubernetesDslMarker ObjectMeta).() -> Unit) {
    metadata = objectMeta(block)
}

inline fun PodTemplateSpec.spec(block: (@KubernetesDslMarker PodSpec).() -> Unit) {
    spec = PodSpec().apply(block)
}

inline fun container(block: (@KubernetesDslMarker Container).() -> Unit) =
    Container().apply(block)

inline fun containerPort(block: (@KubernetesDslMarker ContainerPort).() -> Unit) =
    ContainerPort().apply(block)

inline fun service(block: (@KubernetesDslMarker Service).() -> Unit) =
    Service().apply(block)

inline fun Service.spec(block: (@KubernetesDslMarker ServiceSpec).() -> Unit) {
    spec = ServiceSpec().apply(block)
}

inline fun servicePort(block: (@KubernetesDslMarker ServicePort).() -> Unit) =
    ServicePort().apply(block)
