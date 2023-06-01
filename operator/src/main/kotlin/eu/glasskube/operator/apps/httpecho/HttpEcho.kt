package eu.glasskube.operator.apps.httpecho

import eu.glasskube.operator.resourceLabels
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Plural
import io.fabric8.kubernetes.model.annotation.Version

data class HttpEchoSpec(
    val text: String? = null,
    val host: String? = null
)

data class HttpEchoStatus(
    val result: String? = null
)

@Group("glasskube.eu")
@Version("v1alpha1")
@Plural("httpechos")
class HttpEcho : CustomResource<HttpEchoSpec, HttpEchoStatus>(), Namespaced

val HttpEcho.identifyingLabel
    get() = HttpEchoReconciler.LABEL to metadata.name

val HttpEcho.resourceLabels
    get() = resourceLabels(HttpEchoReconciler.APP_NAME, identifyingLabel)
