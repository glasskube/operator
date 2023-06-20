package eu.glasskube.operator.apps.matomo

import com.fasterxml.jackson.annotation.JsonProperty

data class MatomoInstallConfig(
    @JsonProperty("PluginsInstalled")
    var pluginsInstalled: List<String>,
    @JsonProperty("Config")
    var config: MutableMap<String, MutableMap<String, Any>>
)
