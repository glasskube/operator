package eu.glasskube.operator.resource

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.extensions.Ingress
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.client.utils.Serialization
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent


data class HttpEchoSpec(
    var text: String? = null
)

data class HttpEchoStatus(
    var result: String? = null
)

@Group("glasskube.eu")
@Version("v1alpha1")
class HttpEcho : CustomResource<HttpEchoSpec, HttpEchoStatus>(), Namespaced


@KubernetesDependent(labelSelector = "echo")
class HttpEchoDeployment : CRUDKubernetesDependentResource<Deployment, HttpEcho> {
    constructor() : super(Deployment::class.java)

    override fun desired(primary: HttpEcho?, context: Context<HttpEcho>?): Deployment {
        val inputStream = this.javaClass.classLoader.getResourceAsStream("deployment.yaml")
        return Serialization.unmarshal(inputStream, Deployment::class.java)
    }
}

@KubernetesDependent(labelSelector = "echo")
class HttpEchoService : CRUDKubernetesDependentResource<Service, HttpEcho> {
    constructor() : super(Service::class.java)

    override fun desired(primary: HttpEcho?, context: Context<HttpEcho>?): Service {
        val inputStream = this.javaClass.classLoader.getResourceAsStream("service.yaml")
        return Serialization.unmarshal(inputStream, Service::class.java)
    }
}

@KubernetesDependent(labelSelector = "echo")
class HttpEchoIngress : CRUDKubernetesDependentResource<Ingress, HttpEcho> {
    constructor() : super(Ingress::class.java)

    override fun desired(primary: HttpEcho?, context: Context<HttpEcho>?): Ingress {
        val inputStream = this.javaClass.classLoader.getResourceAsStream("ingress.yaml")
        return Serialization.unmarshal(inputStream, Ingress::class.java)
    }
}
