package eu.glasskube.operator.apps.keycloak.dependent

import eu.glasskube.operator.apps.keycloak.Keycloak
import eu.glasskube.operator.generic.dependent.backups.BackupSpecNotNullCondition
import eu.glasskube.operator.generic.dependent.backups.DependentVeleroSecret
import io.fabric8.kubernetes.api.model.Secret
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent

@KubernetesDependent
class KeycloakVeleroSecret : DependentVeleroSecret<Keycloak>() {
    internal class ReconcilePrecondition : BackupSpecNotNullCondition<Secret, Keycloak>()
}
