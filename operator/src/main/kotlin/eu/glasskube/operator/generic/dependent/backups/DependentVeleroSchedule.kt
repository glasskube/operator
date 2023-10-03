package eu.glasskube.operator.generic.dependent.backups

import eu.glasskube.kubernetes.api.model.metadata
import eu.glasskube.kubernetes.api.model.namespace
import eu.glasskube.operator.apps.common.backup.ResourceWithBackups
import eu.glasskube.operator.boot.VeleroProperties
import eu.glasskube.operator.infra.velero.VeleroBackupTemplate
import eu.glasskube.operator.infra.velero.VeleroSchedule
import eu.glasskube.operator.infra.velero.veleroSchedule
import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.LabelSelector
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDNoGCKubernetesDependentResource
import org.springframework.beans.factory.annotation.Autowired

abstract class DependentVeleroSchedule<P> :
    CRUDNoGCKubernetesDependentResource<VeleroSchedule, P>(VeleroSchedule::class.java), BackupDependentResource<P>
    where P : HasMetadata, P : ResourceWithBackups {

    @Autowired
    private lateinit var veleroProperties: VeleroProperties

    override fun desired(primary: P, context: Context<P>) = veleroSchedule {
        val backupsSpec = primary.getSpec().requireBackups()

        metadata {
            name(primary.velero.resourceNameWithNamespace)
            namespace(veleroProperties.namespace)
            labels(primary.velero.resourceLabels)
        }

        spec = VeleroSchedule.Spec(
            schedule = backupsSpec.schedule,
            template = VeleroBackupTemplate(
                storageLocation = primary.velero.resourceNameWithNamespace,
                ttl = backupsSpec.ttl,
                includedNamespaces = listOf(primary.namespace!!),
                orLabelSelectors = primary.velero.labelSelectors.map { LabelSelector(null, it) }
            )
        )
    }
}
