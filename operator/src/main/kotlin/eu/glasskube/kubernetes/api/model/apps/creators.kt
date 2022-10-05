package eu.glasskube.kubernetes.api.model.apps

import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec

inline fun deployment(block: Deployment.() -> Unit) = Deployment().apply(block)
inline fun deploymentSpec(block: DeploymentSpec.() -> Unit) = DeploymentSpec().apply(block)
