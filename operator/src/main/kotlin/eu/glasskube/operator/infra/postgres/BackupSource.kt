package eu.glasskube.operator.infra.postgres

import io.fabric8.kubernetes.api.model.SecretKeySelector

data class BackupSource(
    val endpointCA: SecretKeySelector
)
