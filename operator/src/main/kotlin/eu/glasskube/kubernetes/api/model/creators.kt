package eu.glasskube.kubernetes.api.model

import io.fabric8.kubernetes.api.model.*

inline fun objectMeta(block: ObjectMeta.() -> Unit) = ObjectMeta().apply(block)
inline fun labelSelector(block: LabelSelector.() -> Unit) = LabelSelector().apply(block)
inline fun podTemplateSpec(block: PodTemplateSpec.() -> Unit) = PodTemplateSpec().apply(block)
inline fun podSpec(block: PodSpec.() -> Unit) = PodSpec().apply(block)
inline fun container(block: Container.() -> Unit) = Container().apply(block)
inline fun containerPort(block: ContainerPort.() -> Unit) = ContainerPort().apply(block)
inline fun service(block: Service.() -> Unit) = Service().apply(block)
inline fun serviceSpec(block: ServiceSpec.() -> Unit) = ServiceSpec().apply(block)
inline fun servicePort(block: ServicePort.() -> Unit) = ServicePort().apply(block)
