package eu.glasskube.operator.httpecho.dependent

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.secret
import eu.glasskube.operator.config.ConfigKey
import eu.glasskube.operator.getConfig
import eu.glasskube.operator.httpecho.HttpEcho
import eu.glasskube.operator.httpecho.HttpEchoReconciler
import eu.glasskube.operator.httpecho.resourceLabels
import eu.glasskube.operator.postgres.*
import eu.glasskube.operator.secrets.SecretGenerator
import io.fabric8.kubernetes.api.model.LocalObjectReference
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.ResourceUpdatePreProcessor

private val HttpEcho.dbName
    get() = "${metadata.name}-db"

private val HttpEcho.dbSuperuserSecret
    get() = "$dbName-superuser"

private val HttpEcho.dbAppSecret
    get() = "$dbName-app"

@KubernetesDependent(labelSelector = HttpEchoReconciler.SELECTOR)
class HttpEchoPostgres : CRUDKubernetesDependentResource<Cluster, HttpEcho>(Cluster::class.java) {
    override fun desired(primary: HttpEcho, context: Context<HttpEcho>) = Cluster().apply {
        metadata {
            name = primary.dbName
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec = ClusterSpec(
            instances = 1,
            superuserSecret = LocalObjectReference(primary.dbSuperuserSecret),
            bootstrap = BootstrapConfiguration(
                initdb = BootstrapInitDB(
                    database = "app",
                    owner = "app",
                    secret = LocalObjectReference(primary.dbAppSecret)
                )
            ),
            storage = StorageConfiguration(
                storageClass = getConfig(client, ConfigKey.databaseStorageClassName),
                size = "1Gi"
            )
        )
    }
}

@KubernetesDependent(labelSelector = HttpEchoReconciler.SELECTOR + ",glasskube.eu/secret=superuser")
class HttpEchoPostgresSuperuserSecret :
    CRUDKubernetesDependentResource<Secret, HttpEcho>(Secret::class.java),
    ResourceUpdatePreProcessor<Secret> {
    override fun desired(primary: HttpEcho, context: Context<HttpEcho>) = secret {
        metadata {
            name = primary.dbSuperuserSecret
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels + ("glasskube.eu/secret" to "superuser") + SecretGenerator.LABEL
            annotations = mapOf(SecretGenerator.generateKeys("password"))
        }
        type = "kubernetes.io/basic-auth"
        stringData = mapOf(
            "username" to "postgres"
        )
    }

    override fun replaceSpecOnActual(actual: Secret, desired: Secret, context: Context<*>) = actual.apply {
        metadata.annotations.putAll(desired.metadata.annotations)
        metadata.labels.putAll(desired.metadata.labels)
        data.putAll(desired.data)
        stringData.putAll(desired.stringData)
    }
}

@KubernetesDependent(labelSelector = HttpEchoReconciler.SELECTOR + ",glasskube.eu/secret=app")
class HttpEchoPostgresAppSecret :
    CRUDKubernetesDependentResource<Secret, HttpEcho>(Secret::class.java),
    ResourceUpdatePreProcessor<Secret> {
    override fun desired(primary: HttpEcho, context: Context<HttpEcho>) = secret {
        metadata {
            name = primary.dbAppSecret
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels + ("glasskube.eu/secret" to "app") + SecretGenerator.LABEL
            annotations = mapOf(SecretGenerator.generateKeys("password"))
        }
        type = "kubernetes.io/basic-auth"
        stringData = mapOf(
            "username" to "app"
        )
    }

    override fun replaceSpecOnActual(actual: Secret, desired: Secret, context: Context<*>) = actual.apply {
        metadata.annotations.putAll(desired.metadata.annotations)
        metadata.labels.putAll(desired.metadata.labels)
        data.putAll(desired.data)
        stringData.putAll(desired.stringData)
    }
}
