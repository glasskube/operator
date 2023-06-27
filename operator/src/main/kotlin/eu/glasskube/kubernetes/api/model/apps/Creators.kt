package eu.glasskube.kubernetes.api.model.apps

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import eu.glasskube.kubernetes.api.model.labelSelector
import io.fabric8.kubernetes.api.model.IntOrString
import io.fabric8.kubernetes.api.model.LabelSelector
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec
import io.fabric8.kubernetes.api.model.apps.DeploymentStrategy
import io.fabric8.kubernetes.api.model.apps.RollingUpdateDeployment

inline fun deployment(block: (@KubernetesDslMarker Deployment).() -> Unit): Deployment =
    Deployment().apply(block)

inline fun Deployment.spec(block: (@KubernetesDslMarker DeploymentSpec).() -> Unit) {
    spec = DeploymentSpec().apply(block)
}

inline fun DeploymentSpec.selector(block: (@KubernetesDslMarker LabelSelector).() -> Unit) {
    selector = labelSelector(block)
}

inline fun DeploymentSpec.template(block: (@KubernetesDslMarker PodTemplateSpec).() -> Unit) {
    template = PodTemplateSpec().apply(block)
}

fun DeploymentSpec.strategyRecreate() {
    strategy = DeploymentStrategy(null, "Recreate")
}

fun DeploymentSpec.strategyRollingUpdate(
    block: (@KubernetesDslMarker RollingUpdateDeployment).() -> Unit = {
        maxSurge = IntOrString("25%")
        maxUnavailable = IntOrString("25%")
    }
) {
    strategy = DeploymentStrategy(RollingUpdateDeployment().apply(block), "RollingUpdate")
}
