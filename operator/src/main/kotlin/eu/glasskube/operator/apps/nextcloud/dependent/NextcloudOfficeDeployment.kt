package eu.glasskube.operator.apps.nextcloud.dependent

import eu.glasskube.kubernetes.api.model.apps.deployment
import eu.glasskube.kubernetes.api.model.apps.selector
import eu.glasskube.kubernetes.api.model.apps.spec
import eu.glasskube.kubernetes.api.model.apps.template
import eu.glasskube.kubernetes.api.model.container
import eu.glasskube.kubernetes.api.model.containerPort
import eu.glasskube.kubernetes.api.model.env
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.limits
import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.kubernetes.api.model.resources
import eu.glasskube.kubernetes.api.model.spec
import eu.glasskube.operator.apps.nextcloud.Nextcloud
import eu.glasskube.operator.apps.nextcloud.NextcloudReconciler
import eu.glasskube.operator.apps.nextcloud.officeName
import eu.glasskube.operator.apps.nextcloud.officeResourceLabelSelector
import eu.glasskube.operator.apps.nextcloud.officeResourceLabels
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent
import io.javaoperatorsdk.operator.processing.event.ResourceID

@KubernetesDependent(
    labelSelector = NextcloudReconciler.OFFICE_SELECTOR,
    resourceDiscriminator = NextcloudOfficeDeployment.Discriminator::class
)
class NextcloudOfficeDeployment : CRUDKubernetesDependentResource<Deployment, Nextcloud>(Deployment::class.java) {

    class ReconcilePrecondition : IsOfficeEnabledPrecondition<Deployment>()

    internal class Discriminator : ResourceIDMatcherDiscriminator<Deployment, Nextcloud>({
        ResourceID(it.officeName, it.namespace)
    })

    override fun desired(primary: Nextcloud, context: Context<Nextcloud>) = deployment {
        metadata {
            name = primary.officeName
            namespace = primary.namespace
            labels = primary.officeResourceLabels
        }
        spec {
            selector {
                matchLabels = primary.officeResourceLabelSelector
            }
            template {
                metadata {
                    labels = primary.officeResourceLabels
                }
                spec {
                    containers = listOf(
                        container {
                            name = Nextcloud.OFFICE_NAME
                            image = Nextcloud.OFFICE_IMAGE
                            resources {
                                limits(memory = Quantity("500", "Mi"))
                            }
                            env {
                                envVar("extra_params", "--o:ssl.enable=false --o:ssl.termination=true")
                                envVar("aliasgroup1", "https://${primary.spec.host}:443")
                            }
                            ports = listOf(
                                containerPort {
                                    containerPort = 9980
                                    name = "http"
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
