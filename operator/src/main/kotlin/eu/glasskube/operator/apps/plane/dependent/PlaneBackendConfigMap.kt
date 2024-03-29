package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.api.reconciler.getSecondaryResource
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.Plane.Redis.redisName
import eu.glasskube.operator.apps.plane.backendResourceName
import eu.glasskube.operator.apps.plane.genericResourceLabels
import eu.glasskube.utils.logger
import io.fabric8.kubernetes.api.model.ConfigMap
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneBackendConfigMap.Discriminator::class)
class PlaneBackendConfigMap : CRUDKubernetesDependentResource<ConfigMap, Plane>(ConfigMap::class.java) {

    internal class Discriminator :
        ResourceIDMatcherDiscriminator<ConfigMap, Plane>({ ResourceID(it.backendResourceName, it.namespace) })

    override fun desired(primary: Plane, context: Context<Plane>) = configMap {
        metadata {
            name(primary.backendResourceName)
            namespace(primary.namespace)
            labels(primary.genericResourceLabels)
        }
        data = primary.run { commonData + smtpData + s3Data }
    }

    override fun onUpdated(primary: Plane, updated: ConfigMap, actual: ConfigMap, context: Context<Plane>) {
        super.onUpdated(primary, updated, actual, context)
        with(context) {
            getSecondaryResource(PlaneApiDeployment.Discriminator()).ifPresent {
                log.info("Restarting api Deployment after ConfigMap changed")
                context.client.apps().deployments().resource(it).rolling().restart()
            }
            getSecondaryResource(PlaneWorkerDeployment.Discriminator()).ifPresent {
                log.info("Restarting worker Deployment after ConfigMap changed")
                context.client.apps().deployments().resource(it).rolling().restart()
            }
            getSecondaryResource(PlaneBeatWorkerDeployment.Discriminator()).ifPresent {
                log.info("Restarting beat worker Deployment after ConfigMap changed")
                context.client.apps().deployments().resource(it).rolling().restart()
            }
        }
    }

    private val Plane.commonData
        get() = mapOf(
            "DJANGO_SETTINGS_MODULE" to "plane.settings.production",
            "REDIS_URL" to "redis://$redisName:6379/",
            "DOCKERIZED" to "1",
            "DEBUG" to "0",
            "USE_MINIO" to "0",
            "DISABLE_COLLECTSTATIC" to "1",
            "FILE_SIZE_LIMIT" to "5242880",
            "WEB_URL" to "https://${spec.host}/",
            "DEFAULT_EMAIL" to spec.defaultUser.email,
            "DEFAULT_PASSWORD" to spec.defaultUser.password,
            "ENABLE_SIGNUP" to if (spec.registrationEnabled) "1" else "0",
            "ENABLE_EMAIL_PASSWORD" to "1"
        )

    private val Plane.smtpData
        get() = spec.smtp?.run {
            mapOf(
                "EMAIL_HOST" to host,
                "EMAIL_PORT" to port.toString(),
                "EMAIL_FROM" to fromAddress,
                "EMAIL_USE_TLS" to if (tlsEnabled) "1" else "0",
                "EMAIL_USE_SSL" to if (sslEnabled) "1" else "0"
            )
        }.orEmpty()

    private val Plane.s3Data
        get() = spec.s3?.run {
            listOfNotNull(
                "AWS_REGION" to region,
                "AWS_S3_BUCKET_NAME" to bucket,
                endpoint?.let { "AWS_S3_ENDPOINT_URL" to it }
            ).toMap()
        }.orEmpty()

    companion object {
        private val log = logger()
    }
}
