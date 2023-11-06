package eu.glasskube.operator.apps.gitlab.runner.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategyRecreate
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMap
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.emptyDir
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.limits
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.requests
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.secretRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.gitlab.runner.GitlabRunner
import eu.glasskube.operator.apps.gitlab.runner.GitlabRunnerReconciler
import eu.glasskube.operator.apps.gitlab.runner.configMapName
import eu.glasskube.operator.apps.gitlab.runner.genericResourceName
import eu.glasskube.operator.apps.gitlab.runner.resourceLabelSelector
import eu.glasskube.operator.apps.gitlab.runner.resourceLabels
import eu.glasskube.operator.apps.gitlab.runner.secretName
import io.fabric8.kubernetes.api.model.ExecAction
import io.fabric8.kubernetes.api.model.Lifecycle
import io.fabric8.kubernetes.api.model.LifecycleHandler
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.SecurityContext
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabRunnerReconciler.SELECTOR)
class GitlabRunnerDeployment : CRUDKubernetesDependentResource<Deployment, GitlabRunner>(Deployment::class.java) {
    override fun desired(primary: GitlabRunner, context: Context<GitlabRunner>) = deployment {
        metadata {
            name(primary.genericResourceName)
            namespace(primary.metadata.namespace)
            labels(primary.resourceLabels)
        }
        spec {
            selector {
                matchLabels = primary.resourceLabelSelector
            }
            replicas = 1
            strategyRecreate()
            template {
                metadata {
                    labels(primary.resourceLabels)
                }
                spec {
                    terminationGracePeriodSeconds = 1800
                    volumes = listOf(
                        volume(CONFIG_VOLUME) { emptyDir() },
                        volume(DOCKER_VOLUME) { emptyDir() },
                        volume(CONFIGMAP_VOLUME) { configMap(primary.configMapName) }
                    )
                    initContainers = listOf(
                        container {
                            name = "registration"
                            image = primary.image
                            envFrom {
                                secretRef(primary.secretName)
                            }
                            args = listOf(
                                "register",
                                "--template-config",
                                "$CONFIGMAP_VOLUME_PATH/$CONFIG_TEMPLATE_NAME",
                                "--non-interactive"
                            )
                            volumeMounts {
                                volumeMount {
                                    name = CONFIG_VOLUME
                                    mountPath = CONFIG_VOLUME_PATH
                                }
                                volumeMount {
                                    name = CONFIGMAP_VOLUME
                                    mountPath = CONFIGMAP_VOLUME_PATH
                                }
                            }
                        }
                    )
                    containers = listOf(
                        container {
                            name = GitlabRunner.APP_NAME
                            image = primary.image
                            lifecycle = Lifecycle().apply {
                                preStop = LifecycleHandler().apply {
                                    exec = ExecAction(
                                        listOf(
                                            "/entrypoint",
                                            "unregister",
                                            "--all-runners",
                                            "--non-interactive"
                                        )
                                    )
                                }
                            }
                            resources {
                                requests(cpu = Quantity("100", "m"), memory = Quantity("150", "Mi"))
                                limits(cpu = Quantity("500", "m"), memory = Quantity("500", "Mi"))
                            }
                            volumeMounts {
                                volumeMount {
                                    name = CONFIG_VOLUME
                                    mountPath = CONFIG_VOLUME_PATH
                                }
                                volumeMount {
                                    name = DOCKER_VOLUME
                                    mountPath = DOCKER_VOLUME_PATH
                                }
                            }
                        },
                        container {
                            name = GitlabRunner.DIND_NAME
                            image = "${GitlabRunner.DIND_IMAGE}:${GitlabRunner.DIND_VERSION}"
                            resources {
                                requests(
                                    cpu = Quantity((primary.spec.concurrency * 100).toString(), "m"),
                                    memory = Quantity((primary.spec.concurrency * 100).toString(), "Mi")
                                )
                                limits(
                                    cpu = Quantity(primary.spec.concurrency.toString()),
                                    memory = Quantity((1000 + (primary.spec.concurrency - 1) * 500).toString(), "Mi")
                                )
                            }
                            env {
                                envVar("DOCKER_DRIVER", "overlay2")
                            }
                            securityContext = SecurityContext().apply {
                                privileged = true
                            }
                            volumeMounts {
                                volumeMount {
                                    name = DOCKER_VOLUME
                                    mountPath = DOCKER_VOLUME_PATH
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private val GitlabRunner.image get() = "${GitlabRunner.APP_IMAGE}:v${spec.version}"

    companion object {
        private const val CONFIG_VOLUME = "config"
        private const val CONFIG_VOLUME_PATH = "/etc/gitlab-runner"
        private const val DOCKER_VOLUME = "docker"
        private const val DOCKER_VOLUME_PATH = "/var/run"
        private const val CONFIGMAP_VOLUME = "configmap"
        private const val CONFIGMAP_VOLUME_PATH = "/glasskube"
        const val CONFIG_TEMPLATE_NAME = "config.template.toml"
    }
}
