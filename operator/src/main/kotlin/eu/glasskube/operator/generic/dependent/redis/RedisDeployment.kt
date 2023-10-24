package eu.glasskube.operator.generic.dependent.redis

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.limits
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.requests
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.spec
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource

abstract class RedisDeployment<T : HasMetadata> :
    RedisDependentResource<T>, CRUDKubernetesDependentResource<Deployment, T>(Deployment::class.java) {

    override fun desired(primary: T, context: Context<T>) = deployment {
        metadata {
            name(redisNameMapper.getName(primary))
            namespace(primary.namespace)
            labels(redisNameMapper.getLabels(primary))
        }
        spec {
            selector {
                matchLabels = redisNameMapper.getLabelSelector(primary)
            }
            strategyRecreate()
            template {
                metadata {
                    labels(redisNameMapper.getLabels(primary))
                }
                spec {
                    containers = listOf(
                        container {
                            name = "redis"
                            image = "redis:${redisNameMapper.getVersion(primary)}-alpine"
                            resources {
                                limits(memory = Quantity("128", "Mi"))
                                requests(memory = Quantity("10", "Mi"))
                            }
                            ports = listOf(
                                containerPort { containerPort = 6379 }
                            )
                        }
                    )
                }
            }
        }
    }
}
