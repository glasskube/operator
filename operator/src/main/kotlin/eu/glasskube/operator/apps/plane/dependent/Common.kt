package eu.glasskube.operator.apps.plane.dependent

import eu.glasskube.kubernetes.api.model.createEnv
import eu.glasskube.kubernetes.api.model.envVar
import eu.glasskube.kubernetes.api.model.secretKeyRef
import eu.glasskube.operator.apps.plane.Plane
import eu.glasskube.operator.apps.plane.Plane.Postgres.postgresDatabaseName
import eu.glasskube.operator.apps.plane.Plane.Postgres.postgresHostName
import eu.glasskube.operator.apps.plane.Plane.Postgres.postgresSecretName

val Plane.commonBackendEnv
    get() = createEnv {
        envVar("DBUSER") { secretKeyRef(postgresSecretName, "username") }
        envVar("DBPASS") { secretKeyRef(postgresSecretName, "password") }
        envVar("DATABASE_URL", "postgres://\$(DBUSER):\$(DBPASS)@$postgresHostName:5432/$postgresDatabaseName")
        spec.smtp?.apply {
            envVar("EMAIL_HOST_USER") { secretKeyRef(authSecret.name, "username") }
            envVar("EMAIL_HOST_PASSWORD") { secretKeyRef(authSecret.name, "password") }
        }
    }
