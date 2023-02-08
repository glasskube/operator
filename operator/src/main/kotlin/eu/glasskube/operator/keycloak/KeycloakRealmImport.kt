package eu.glasskube.operator.keycloak

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("k8s.keycloak.org")
@Version("v2alpha1")
class KeycloakRealmImport : CustomResource<KeycloakRealmImportSpec, KeycloakRealmImportStatus>(), Namespaced
