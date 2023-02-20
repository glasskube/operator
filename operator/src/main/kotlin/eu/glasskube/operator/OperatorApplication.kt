package eu.glasskube.operator

import eu.glasskube.operator.httpecho.dependent.HttpEchoDeployment
import eu.glasskube.operator.httpecho.dependent.HttpEchoIngress
import eu.glasskube.operator.httpecho.dependent.HttpEchoService
import eu.glasskube.operator.odoo.dependent.OdooDatabaseBackupSecret
import eu.glasskube.operator.odoo.dependent.OdooDatabaseSecret
import eu.glasskube.operator.odoo.dependent.OdooDatabaseSuperuserSecret
import io.fabric8.kubernetes.api.model.AuthInfo
import io.fabric8.kubernetes.api.model.Cluster
import io.fabric8.kubernetes.api.model.Config
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.Context
import io.fabric8.kubernetes.api.model.FieldsV1
import io.fabric8.kubernetes.api.model.ManagedFieldsEntry
import io.fabric8.kubernetes.api.model.NamedAuthInfo
import io.fabric8.kubernetes.api.model.NamedCluster
import io.fabric8.kubernetes.api.model.NamedContext
import io.fabric8.kubernetes.api.model.NamedExtension
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.Preferences
import io.fabric8.kubernetes.api.model.Secret
import io.fabric8.kubernetes.api.model.runtime.RawExtension
import io.fabric8.kubernetes.client.impl.KubernetesClientImpl
import io.fabric8.kubernetes.internal.KubernetesDeserializer
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependentConverter
import io.javaoperatorsdk.operator.processing.event.rate.LinearRateLimiter
import io.javaoperatorsdk.operator.processing.retry.GenericRetry
import org.reflections.Reflections
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.ImportRuntimeHints

fun main(args: Array<String>) {
    runApplication<OperatorApplication>(*args)
}

@ComponentScan(
    includeFilters = [
        ComponentScan.Filter(type = FilterType.ANNOTATION, value = [ControllerConfiguration::class])
    ]
)
@ImportRuntimeHints(OperatorApplication.RuntimeHints::class)
@RegisterReflectionForBinding(
    classes = [
        Config::class,
        NamedCluster::class,
        Cluster::class,
        NamedExtension::class,
        KubernetesDeserializer::class,
        RawExtension::class,
        NamedContext::class,
        Context::class,
        Preferences::class,
        NamedAuthInfo::class,
        AuthInfo::class,
        ConfigMap::class,
        ObjectMeta::class,
        ManagedFieldsEntry::class,
        FieldsV1::class,
        Secret::class,
        GenericRetry::class,
        LinearRateLimiter::class,
        KubernetesDependentConverter::class,
        HttpEchoService::class,
        HttpEchoIngress::class,
        HttpEchoDeployment::class,
        OdooDatabaseSecret.Discriminator::class,
        OdooDatabaseBackupSecret.Discriminator::class,
        OdooDatabaseSuperuserSecret.Discriminator::class
    ]
)
@SpringBootApplication
class OperatorApplication(releaseInfo: ReleaseInfo) {
    init {
        releaseInfo.print()
    }

    class RuntimeHints : RuntimeHintsRegistrar {
        override fun registerHints(hints: org.springframework.aot.hint.RuntimeHints, classLoader: ClassLoader?) {
            hints.reflection().registerTypes(
                Reflections("eu.glasskube.operator.odoo.dependent")
                    .getTypesAnnotatedWith(KubernetesDependent::class.java)
                    .map { TypeReference.of(it) }
            ) {
                it.withMembers(*MemberCategory.values())
            }
            hints.reflection().registerTypes(
                Reflections("eu.glasskube.operator.matomo.dependent")
                    .getTypesAnnotatedWith(KubernetesDependent::class.java)
                    .map { TypeReference.of(it) }
            ) {
                it.withMembers(*MemberCategory.values())
            }
            hints.reflection().registerType(
                TypeReference.of(KubernetesClientImpl::class.java),
                *MemberCategory.values()
            )
        }
    }
}
