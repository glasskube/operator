package eu.glasskube.operator.webpage

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

data class WebPageSpec(
    var html: String? = null
)

data class WebPageStatus(
    var result: String? = null
)

@Group("glasskube.eu")
@Version("v1alpha1")
class WebPage : CustomResource<WebPageSpec, WebPageStatus>(), Namespaced
