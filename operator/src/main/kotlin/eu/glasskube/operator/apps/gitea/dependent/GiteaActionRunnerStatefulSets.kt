package eu.glasskube.operator.apps.gitea.dependent

import eu.glasskube.kubernetes.api.model.apps.statefulSet
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.emptyDir
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.limits
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.requests
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.securityContext
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.gitea.Gitea
import eu.glasskube.operator.apps.gitea.GiteaReconciler
import eu.glasskube.operator.apps.gitea.getRunnerName
import eu.glasskube.operator.apps.gitea.resourceLabelSelector
import eu.glasskube.operator.apps.gitea.resourceLabels
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.apps.StatefulSet
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.BulkDependentResource
import io.javaoperatorsdk.operator.processing.dependent.Matcher
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GiteaReconciler.SELECTOR)
class GiteaActionRunnerStatefulSets :
    CRUDKubernetesDependentResource<StatefulSet, Gitea>(StatefulSet::class.java), BulkDependentResource<StatefulSet, Gitea> {
    override fun desiredResources(primary: Gitea, context: Context<Gitea>) =
        primary.spec.actions.runners
            .map {
                statefulSet {
                    metadata {
                        name(primary.getRunnerName(it))
                        namespace(primary.namespace)
                        labels(primary.resourceLabels + it.resourceLabels)
                    }
                    spec {
                        selector { matchLabels = primary.resourceLabelSelector + it.resourceLabels }
                        replicas(1)
                        updateStrategyRollingUpdate {
                            maxUnavailable("100%")
                        }
                        volumeClaimTemplates {
                            volumeClaimTemplate {
                                metadata { name(RUNNER_DATA_VOLUME) }
                                spec {
                                    resources { requests = mapOf("storage" to Quantity("5", "Gi")) }
                                    accessModes = listOf("ReadWriteOnce")
                                }
                            }
                        }
                        template {
                            metadata { labels(primary.resourceLabels + it.resourceLabels) }
                            spec {
                                volumes = listOf(
                                    volume(DOCKER_CERT_VOLUME) { emptyDir() }
                                )
                                containers = listOf(
                                    container {
                                        name = Gitea.Runner.APP_NAME
                                        image = Gitea.Runner.APP_IMAGE
                                        command = listOf("sh")
                                        args = listOf(
                                            "-c",
                                            "while ! nc -z localhost 2376 </dev/null; do echo 'waiting for docker daemon...'; sleep 5; done; /sbin/tini -- /opt/act/run.sh"
                                        )
                                        env {
                                            envVar("DOCKER_HOST", DOCKER_HOST)
                                            envVar("DOCKER_CERT_PATH", DOCKER_CLIENT_CERT_PATH)
                                            envVar("DOCKER_TLS_VERIFY", "1")
                                            envVar("GITEA_INSTANCE_URL", "http://gitea-${primary.metadata.name}-http:3000")
                                            it.labels?.takeIf { it.isNotEmpty() }?.also {
                                                envVar("GITEA_RUNNER_LABELS", it.joinToString(","))
                                            }
                                        }
                                        envFrom { secretRef(primary.getRunnerName(it)) }
                                        volumeMounts {
                                            volumeMount {
                                                name = DOCKER_CERT_VOLUME
                                                mountPath = DOCKER_CERT_PATH
                                            }
                                            volumeMount {
                                                name = RUNNER_DATA_VOLUME
                                                mountPath = RUNNER_DATA_PATH
                                                subPath = "data"
                                            }
                                        }
                                        resources {
                                            requests(
                                                cpu = Quantity("200", "m"),
                                                memory = Quantity("250", "Mi")
                                            )
                                            limits(
                                                cpu = Quantity("200", "m")
                                            )
                                        }
                                    },
                                    container {
                                        name = "docker"
                                        image = Gitea.Runner.DOCKER_IMAGE
                                        env { envVar("DOCKER_TLS_CERTDIR", DOCKER_CERT_PATH) }
                                        volumeMounts {
                                            volumeMount {
                                                name = DOCKER_CERT_VOLUME
                                                mountPath = DOCKER_CERT_PATH
                                            }
                                            volumeMount {
                                                name = RUNNER_DATA_VOLUME
                                                mountPath = DOCKER_DATA_PATH
                                                subPath = "docker"
                                            }
                                        }
                                        securityContext { privileged = true }
                                        resources {
                                            requests(
                                                cpu = Quantity("1"),
                                                memory = Quantity("1", "Gi")
                                            )
                                            limits(
                                                cpu = Quantity("1")
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            .associateBy { it.metadata.name }

    override fun getSecondaryResources(primary: Gitea, context: Context<Gitea>) =
        context.getSecondaryResources(StatefulSet::class.java)
            .associateBy { it.metadata.name }
            .toMutableMap()

    override fun match(
        actualResource: StatefulSet,
        desired: StatefulSet,
        primary: Gitea,
        context: Context<Gitea>
    ): Matcher.Result<StatefulSet> =
        super<BulkDependentResource>.match(actualResource, desired, primary, context)

    companion object {
        private const val DOCKER_HOST = "tcp://localhost:2376"
        private const val DOCKER_CERT_PATH = "/certs"
        private const val DOCKER_CLIENT_CERT_PATH = "$DOCKER_CERT_PATH/client"
        private const val DOCKER_CERT_VOLUME = "docker-certs"
        private const val RUNNER_DATA_PATH = "/data"
        private const val RUNNER_DATA_VOLUME = "runner-data"
        private const val DOCKER_DATA_PATH = "/var/lib/docker"
    }
}
