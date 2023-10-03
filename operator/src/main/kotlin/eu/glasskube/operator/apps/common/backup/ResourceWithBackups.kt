package eu.glasskube.operator.apps.common.backup

import eu.glasskube.operator.generic.dependent.backups.VeleroNameMapper

interface ResourceWithBackups {
    fun getSpec(): HasBackupSpec
    val velero: VeleroNameMapper
}
