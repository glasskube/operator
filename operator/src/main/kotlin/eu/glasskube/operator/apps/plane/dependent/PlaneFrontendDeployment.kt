package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.configMapRef
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.envFrom
import eu.glasskube.kubernetes.api.model.httpGet
import eu.glasskube.kubernetes.api.model.intOrString
import eu.glasskube.kubernetes.api.model.livenessProbe
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.readinessProbe
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.frontendImage
import eu.glasskube.operator.apps.plane.frontendResourceLabelSelector
import eu.glasskube.operator.apps.plane.frontendResourceLabels
import eu.glasskube.operator.apps.plane.frontendResourceName
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(resourceDiscriminator = PlaneFrontendDeployment.Discriminator::class)
class PlaneFrontendDeployment : CRUDKubernetesDependentResource<Deployment, Plane>(Deployment::class.java) {
    internal class Discriminator :
        ResourceIDMatcherDiscriminator<Deployment, Plane>({ ResourceID(it.frontendResourceName) })

    override fun desired(primary: Plane, context: Context<Plane>) = deployment {
        metadata {
            name = primary.frontendResourceName
            namespace = primary.namespace
            labels = primary.frontendResourceLabels
        }
        spec {
            selector {
                matchLabels = primary.frontendResourceLabelSelector
            }
            template {
                metadata {
                    labels = primary.frontendResourceLabelSelector
                }
                spec {
                    terminationGracePeriodSeconds = 10
                    containers = listOf(
                        container {
                            name = Plane.FRONTEND_NAME
                            image = primary.frontendImage
                            command = listOf("/usr/local/bin/start.sh", "web/server.js", "web")
                            envFrom {
                                configMapRef(primary.frontendResourceName)
                            }
                            ports = listOf(
                                containerPort { containerPort = 3000 }
                            )
                            resources = primary.spec.frontend.resources
                            readinessProbe {
                                initialDelaySeconds = 20
                                timeoutSeconds = 5
                                httpGet {
                                    path = "/"
                                    port = intOrString(3000)
                                }
                            }
                            livenessProbe {
                                initialDelaySeconds = 20
                                timeoutSeconds = 5
                                httpGet {
                                    path = "/"
                                    port = intOrString(3000)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
