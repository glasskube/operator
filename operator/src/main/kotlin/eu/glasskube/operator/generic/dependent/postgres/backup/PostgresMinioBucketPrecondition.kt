package eu.glasskube.operator.generic.dependent.postgres.backup

import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import eu.glasskube.operator.generic.condition.AllCompositeCondition
import eu.glasskube.operator.generic.dependent.postgres.PostgresWithBackupsEnabledCondition
import eu.glasskube.operator.generic.dependent.postgres.PostgresWithoutS3BackupsSpecCondition
import io.fabric8.kubernetes.api.model.HasMetadata

abstract class PostgresMinioBucketPrecondition<R, P> : AllCompositeCondition<R, P>(
    PostgresWithBackupsEnabledCondition(),
    PostgresWithoutS3BackupsSpecCondition()
) where P : HasMetadata, P : ResourceWithDatabaseSpec<PostgresDatabaseSpec>
