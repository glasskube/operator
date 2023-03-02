package eu.glasskube.kubernetes.client

import io.fabric8.kubernetes.api.model.KubernetesResourceList
import io.fabric8.kubernetes.api.model.networking.v1.IngressClass
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.Resource
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl

fun KubernetesClient.ingressClasses(): MixedOperation<IngressClass, KubernetesResourceList<IngressClass>, Resource<IngressClass>> =
    resources(IngressClass::class.java)

/**
 * The default Ingress Class is the Ingress Class annotated with
 * `ingressclass.kubernetes.io/is-default-class: true`. If there are multiple
 * Ingress Class objects with this annotation, null is returned.
 *
 * @return the default Ingress Class
 */
fun KubernetesClient.getDefaultIngressClass(): IngressClass? =
    ingressClasses().withLabel("ingressclass.kubernetes.io/is-default-class", "true").list().items
        .singleOrNull()

fun <S, T : CustomResource<*, S>> T.patchOrUpdateStatus(desiredStatus: S): UpdateControl<T> =
    when (status) {
        null -> UpdateControl.updateStatus(apply { status = desiredStatus })
        desiredStatus -> UpdateControl.noUpdate()
        else -> UpdateControl.patchStatus(apply { status = desiredStatus })
    }

fun <S, T : CustomResource<*, S>> T.patchOrUpdateResourceAndStatus(desiredStatus: S): UpdateControl<T> =
    when (status) {
        null -> UpdateControl.updateResourceAndStatus(apply { status = desiredStatus })
        desiredStatus -> UpdateControl.updateResource(this)
        else -> UpdateControl.patchResourceAndStatus(apply { status = desiredStatus })
    }
