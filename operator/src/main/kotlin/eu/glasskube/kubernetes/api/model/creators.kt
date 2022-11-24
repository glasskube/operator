package eu.glasskube.kubernetes.api.model

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.ConfigMapEnvSource
import io.fabric8.kubernetes.api.model.ConfigMapVolumeSource
import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.ContainerPort
import io.fabric8.kubernetes.api.model.EnvFromSource
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.LabelSelector
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.PodSpec
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.Secret
import io.fabric8.kubernetes.api.model.SecretEnvSource
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.ServicePort
import io.fabric8.kubernetes.api.model.ServiceSpec
import io.fabric8.kubernetes.api.model.Volume
import io.fabric8.kubernetes.api.model.VolumeMount

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

inline fun Container.envFrom(block: (@KubernetesDslMarker MutableList<EnvFromSource>).() -> Unit) {
    envFrom = mutableListOf<EnvFromSource>().apply(block)
}

fun MutableList<EnvFromSource>.secretRef(name: String, optional: Boolean? = null) {
    add(EnvFromSource().apply { secretRef = SecretEnvSource(name, optional) })
}

fun MutableList<EnvFromSource>.configMapRef(name: String, optional: Boolean? = null) {
    add(EnvFromSource().apply { configMapRef = ConfigMapEnvSource(name, optional) })
}

inline fun secret(block: (@KubernetesDslMarker Secret).() -> Unit) =
    Secret().apply(block)

inline fun configMap(block: (@KubernetesDslMarker ConfigMap).() -> Unit) =
    ConfigMap().apply(block)

inline fun volumeMount(block: (@KubernetesDslMarker VolumeMount).() -> Unit) =
    VolumeMount().apply(block)

inline fun volume(block: (@KubernetesDslMarker Volume).() -> Unit) =
    Volume().apply(block)

inline fun configMapVolumeSource(block: (@KubernetesDslMarker ConfigMapVolumeSource).() -> Unit) =
    ConfigMapVolumeSource().apply(block)
