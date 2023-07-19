package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudInstallConfig
import eu.glasskube.operator.apps.nextcloud.configName
import eu.glasskube.operator.apps.nextcloud.resourceLabels
import eu.glasskube.utils.logger
import eu.glasskube.utils.resourceAsString
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class NextcloudConfigMap : CRUDKubernetesDependentResource<ConfigMap, Nextcloud>(ConfigMap::class.java) {
    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = configMap {
        metadata {
            name = primary.configName
            namespace = primary.namespace
            labels = primary.resourceLabels
        }
        data = mapOf(
            NextcloudDeployment.CONFIG_FILE_NAME to primary.configFile,
            NextcloudDeployment.NGINX_CONFIG_FILE_NAME to nginxConfigFile
        )
    }

    override fun onUpdated(primary: Nextcloud, updated: ConfigMap, actual: ConfigMap, context: Context<Nextcloud>) {
        super.onUpdated(primary, updated, actual, context)
        context.getSecondaryResource(NextcloudDeployment.Discriminator()).ifPresent {
            log.info("Restarting deployment after config change")
            kubernetesClient.apps().deployments().resource(it).rolling().restart()
        }
    }

    private val nginxConfigFile: String
        get() = resourceAsString("nginx.conf")

    private val Nextcloud.configFile: String
        get() = kubernetesClient.kubernetesSerialization.asJson(
            NextcloudInstallConfig(
                listOfNotNull(
                    spec.defaultPhoneRegion?.let { "default_phone_region" to it },
                    "overwriteprotocol" to "https",
                    "trusted_proxies" to listOf(
                        "10.0.0.0/8",
                        "172.16.0.0/12",
                        "192.168.0.0/16"
                    )
                ).toMap(),
                listOfNotNull(
                    spec.apps.office?.let {
                        "richdocuments" to mapOf(
                            "wopi_url" to "https://${it.host}/",
                            "public_wopi_url" to "https://${it.host}"
                        )
                    }
                ).toMap().takeIf { it.isNotEmpty() }
            )
        )

    companion object {
        private val log = logger()
    }
}
