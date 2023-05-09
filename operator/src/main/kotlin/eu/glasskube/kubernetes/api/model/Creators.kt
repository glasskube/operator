package eu.glasskube.kubernetes.api.model

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.ConfigMapEnvSource
import io.fabric8.kubernetes.api.model.ConfigMapKeySelector
import io.fabric8.kubernetes.api.model.ConfigMapVolumeSource
import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.ContainerPort
import io.fabric8.kubernetes.api.model.EnvFromSource
import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.EnvVarSource
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.KeyToPath
import io.fabric8.kubernetes.api.model.LabelSelector
import io.fabric8.kubernetes.api.model.ObjectFieldSelector
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpec
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource
import io.fabric8.kubernetes.api.model.PodSpec
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.ResourceClaim
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder
import io.fabric8.kubernetes.api.model.Secret
import io.fabric8.kubernetes.api.model.SecretEnvSource
import io.fabric8.kubernetes.api.model.SecretKeySelector
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

inline fun Container.env(block: (@KubernetesDslMarker MutableList<EnvVar>).() -> Unit) {
    env = mutableListOf<EnvVar>().apply(block)
}

fun MutableList<EnvVar>.envVar(name: String, value: String) {
    add(EnvVar(name, value, null))
}

inline fun MutableList<EnvVar>.envVar(name: String, block: (@KubernetesDslMarker EnvVarSource).() -> Unit) {
    add(EnvVar(name, null, EnvVarSource().apply(block)))
}

fun secretKeySelector(name: String, key: String, optional: Boolean = false) =
    SecretKeySelector(key, name, optional.takeIf { it })

fun EnvVarSource.secretKeyRef(name: String, key: String, optional: Boolean = false) {
    secretKeyRef = secretKeySelector(key = key, name = name, optional = optional)
}

fun configMapKeySelector(name: String, key: String, optional: Boolean = false) =
    ConfigMapKeySelector(key, name, optional.takeIf { it })

fun EnvVarSource.configMapRef(name: String, key: String, optional: Boolean = false) {
    configMapKeyRef = configMapKeySelector(key = key, name = name, optional = optional)
}

fun EnvVarSource.fieldRef(fieldPath: String, apiVersion: String? = null) {
    fieldRef = ObjectFieldSelector(apiVersion, fieldPath)
}

inline fun containerPort(block: (@KubernetesDslMarker ContainerPort).() -> Unit) =
    ContainerPort().apply(block)

inline fun service(block: (@KubernetesDslMarker Service).() -> Unit) =
    Service().apply(block)

inline fun Service.spec(block: (@KubernetesDslMarker ServiceSpec).() -> Unit) {
    spec = ServiceSpec().apply(block)
}

inline fun servicePort(block: (@KubernetesDslMarker ServicePort).() -> Unit) =
    ServicePort().apply(block)

inline fun Container.resources(block: (@KubernetesDslMarker ResourceRequirementsBuilder).() -> Unit) {
    resources = ResourceRequirementsBuilder().apply(block).build()
}

fun ResourceRequirementsBuilder.requests(cpu: Quantity? = null, memory: Quantity? = null) {
    if (cpu != null) {
        addToRequests("cpu", cpu)
    }
    if (memory != null) {
        addToRequests("memory", memory)
    }
}

fun ResourceRequirementsBuilder.limits(cpu: Quantity? = null, memory: Quantity? = null) {
    if (cpu != null) {
        addToLimits("cpu", cpu)
    }
    if (memory != null) {
        addToLimits("memory", memory)
    }
}

fun ResourceRequirementsBuilder.claims(vararg claims: String) {
    addAllToClaims(claims.map { ResourceClaim(it) })
}

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

inline fun volume(name: String, block: (@KubernetesDslMarker Volume).() -> Unit = {}) =
    Volume().apply { this.name = name }.apply(block)

inline fun Volume.configMap(name: String, block: (@KubernetesDslMarker ConfigMapVolumeSource).() -> Unit = {}) {
    configMap = ConfigMapVolumeSource().apply { this.name = name }.apply(block)
}

fun Volume.persistentVolumeClaim(name: String, readonly: Boolean? = null) {
    persistentVolumeClaim = PersistentVolumeClaimVolumeSource(
        name,
        // readonly == false would be removed by k8s, so we don't add it
        readonly?.takeIf { it }
    )
}

inline fun ConfigMapVolumeSource.items(block: (@KubernetesDslMarker MutableList<KeyToPath>).() -> Unit) {
    items = mutableListOf<KeyToPath>().apply(block)
}

fun MutableList<KeyToPath>.item(key: String, path: String, mode: Int? = null) {
    add(KeyToPath(key, mode, path))
}

inline fun Container.volumeMounts(block: (@KubernetesDslMarker MutableList<VolumeMount>).() -> Unit) {
    volumeMounts = mutableListOf<VolumeMount>().apply(block)
}

fun MutableList<VolumeMount>.volumeMount(block: (@KubernetesDslMarker VolumeMount).() -> Unit) {
    add(VolumeMount().apply(block))
}

inline fun persistentVolumeClaim(block: (@KubernetesDslMarker PersistentVolumeClaim).() -> Unit) =
    PersistentVolumeClaim().apply(block)

inline fun PersistentVolumeClaim.spec(block: (@KubernetesDslMarker PersistentVolumeClaimSpec).() -> Unit) {
    spec = PersistentVolumeClaimSpec().apply(block)
}

inline fun PersistentVolumeClaimSpec.resources(block: (@KubernetesDslMarker ResourceRequirements).() -> Unit) {
    resources = ResourceRequirements().apply(block)
}
