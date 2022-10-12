package eu.glasskube.operator.httpecho

import eu.glasskube.kubernetes.api.model.*
import eu.glasskube.kubernetes.api.model.extensions.*
import eu.glasskube.operator.resourceLabels
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

data class HttpEchoSpec(
    var text: String? = null,
    var host: String? = null
)

data class HttpEchoStatus(
    var result: String? = null
)

@Group("glasskube.eu")
@Version("v1alpha1")
class HttpEcho : CustomResource<HttpEchoSpec, HttpEchoStatus>(), Namespaced

val HttpEcho.identifyingLabel
    get() = HttpEchoReconciler.LABEL to metadata.name

val HttpEcho.resourceLabels
    get() = resourceLabels(HttpEchoReconciler.APP_NAME, identifyingLabel)
