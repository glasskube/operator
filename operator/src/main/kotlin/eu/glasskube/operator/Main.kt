package eu.glasskube.operator

import eu.glasskube.kubernetes.client.getDefaultIngressClass
import eu.glasskube.kubernetes.client.ingressClasses
import eu.glasskube.operator.config.CloudProvider
import eu.glasskube.operator.config.ConfigGenerator
import eu.glasskube.operator.config.ConfigKey
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.Resource

fun getConfig(client: KubernetesClient): Resource<ConfigMap> =
    client.configMaps().inNamespace(Environment.NAMESPACE).withName(ConfigGenerator.NAME)

fun getConfig(client: KubernetesClient, key: ConfigKey): String = getConfig(client).get().data.getValue(key.name)

/**
 * The Ingress Class name is determined by evaluating the following and picking
 * the first available value:
 * 1. If the Ingress Class name is specified in the operator configuration, it is used
 * 2. If there is exactly one default Ingress Class, its name is used
 * 3. If there is exactly one Ingress Class, its name is used
 * 4. Otherwise, null is used, but it will likely fail!
 *
 * @return the Ingress Class Name to be used for Ingress objects.
 */
fun KubernetesClient.getIngressClassName(): String? =
    runCatching { getConfig(this, ConfigKey.ingressClassName) }.getOrNull()
        ?: getDefaultIngressClass()?.metadata?.name
        ?: ingressClasses().list().items.singleOrNull()?.metadata?.name

fun getCloudProvider(client: KubernetesClient): CloudProvider {
    fun detectCloudProvider(): CloudProvider {
        if (client.nodes().withLabel("eks.amazonaws.com/nodegroup").list().items.isNotEmpty()) {
            return CloudProvider.aws
        } else if (client.nodes().withLabel("csi.hetzner.cloud/location").list().items.isNotEmpty()) {
            return CloudProvider.hcloud
        }
        return CloudProvider.generic
    }

    return CloudProvider.valueOf(
        getConfig(client).get().data[ConfigKey.cloudProvider.name] ?: detectCloudProvider().name
    )
}
