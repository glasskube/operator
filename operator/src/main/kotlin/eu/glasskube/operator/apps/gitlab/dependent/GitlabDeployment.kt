package eu.glasskube.operator.apps.gitlab.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.strategy
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.persistentVolumeClaim
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.kubernetes.api.model.startupProbe
import eu.glasskube.kubernetes.api.model.volume
import eu.glasskube.kubernetes.api.model.volumeMount
import eu.glasskube.kubernetes.api.model.volumeMounts
import eu.glasskube.operator.apps.gitlab.Gitlab
import eu.glasskube.operator.apps.gitlab.GitlabReconciler
import eu.glasskube.operator.apps.gitlab.configMapName
import eu.glasskube.operator.apps.gitlab.databaseName
import eu.glasskube.operator.apps.gitlab.resourceLabelSelector
import eu.glasskube.operator.apps.gitlab.resourceLabels
import eu.glasskube.operator.apps.gitlab.volumeName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent(labelSelector = GitlabReconciler.SELECTOR)
class GitlabDeployment : CRUDKubernetesDependentResource<Deployment, Gitlab>(Deployment::class.java) {
    override fun desired(primary: Gitlab, context: Context<Gitlab>) = deployment {
        metadata {
            name = primary.metadata.name
            namespace = primary.metadata.namespace
            labels = primary.resourceLabels
        }
        spec {
            strategy("Recreate")
            selector {
                matchLabels = primary.resourceLabelSelector
            }
            template {
                metadata {
                    labels = primary.resourceLabels
                }
                spec {
                    volumes = listOf(
                        volume(VOLUME_NAME) { persistentVolumeClaim(primary.volumeName) }
                    )
                    containers = listOf(
                        container {
                            name = Gitlab.APP_NAME
                            image = "${Gitlab.APP_IMAGE}:${Gitlab.APP_VERSION}"
                            envFrom {
                                configMapRef(primary.configMapName)
                            }
                            env {
                                envVar("DB_PASSWORD") {
                                    secretKeyRef("${primary.databaseName}-app", "password")
                                }
                                when (val selector = primary.spec.initialRootPasswordSecret) {
                                    null ->
                                        envVar("INITIAL_ROOT_PASSWORD", "glasskube-operator")

                                    else ->
                                        envVar("INITIAL_ROOT_PASSWORD") {
                                            secretKeyRef(selector.name, selector.key)
                                        }
                                }
                                primary.spec.smtp?.also {
                                    envVar("SMTP_USERNAME") {
                                        secretKeyRef(it.authSecret.name, "username")
                                    }
                                    envVar("SMTP_PASSWORD") {
                                        secretKeyRef(it.authSecret.name, "password")
                                    }
                                }
                            }
                            volumeMounts {
                                volumeMount {
                                    mountPath = "/var/opt/gitlab"
                                    name = VOLUME_NAME
                                    subPath = "data"
                                }
                                volumeMount {
                                    mountPath = "/etc/gitlab"
                                    name = VOLUME_NAME
                                    subPath = "config"
                                }
                                volumeMount {
                                    mountPath = "/var/log/gitlab"
                                    name = VOLUME_NAME
                                    subPath = "logs"
                                }
                            }
                            ports = listOf(
                                containerPort {
                                    containerPort = 22
                                    name = "ssh"
                                },
                                containerPort {
                                    containerPort = 80
                                    name = "http"
                                },
                                containerPort {
                                    containerPort = 8082
                                    name = "sidekiq-exp"
                                },
                                containerPort {
                                    containerPort = 9168
                                    name = "gitlab-exp"
                                },
                                containerPort {
                                    containerPort = 9236
                                    name = "gitaly-exp"
                                }
                            )
                            startupProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                // If the container is still unresponsive after 10 minutes, it will be restarted
                                failureThreshold = 60
                                httpGet {
                                    path = "/"
                                    port = "http".intOrString()
                                }
                            }
                            livenessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                // If the container becomes unresponsive for 1 minute, it will be restarted
                                failureThreshold = 6
                                httpGet {
                                    path = "/"
                                    port = "http".intOrString()
                                }
                            }
                            readinessProbe {
                                periodSeconds = 10
                                successThreshold = 1
                                failureThreshold = 3
                                httpGet {
                                    path = "/"
                                    port = "http".intOrString()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    companion object {
        private const val VOLUME_NAME = "data"
    }
}
