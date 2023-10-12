package eu.glasskube.operator.generic.dependent.postgres

import eu.glasskube.operator.apps.common.database.ResourceWithDatabaseSpec
import eu.glasskube.operator.apps.common.database.postgres.PostgresDatabaseSpec
import io.fabric8.kubernetes.api.model.HasMetadata
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition

open class PostgresWithoutS3BackupsSpecCondition<R, P> : Condition<R, P>
    where P : HasMetadata, P : ResourceWithDatabaseSpec<PostgresDatabaseSpec> {

    override fun isMet(dependentResource: DependentResource<R, P>, primary: P, context: Context<P>) =
        primary.getSpec().database.backups?.s3 == null
}
