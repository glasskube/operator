package eu.glasskube.operator.boot

import eu.glasskube.operator.infra.minio.MinioBucket
import io.fabric8.kubernetes.api.model.FieldsV1
import io.fabric8.kubernetes.api.model.ManagedFieldsEntry
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.client.utils.KubernetesSerialization
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OperatorConfigTest {
    private val serialization = KubernetesSerialization(OperatorConfig().objectMapper(), true)

    @Test
    fun `clone keeps managedFields`() {
        val fieldsV1 = FieldsV1().apply {
            setAdditionalProperty("f:metadata", mapOf("f:labels" to mapOf("f:app" to emptyMap<String, Any>())))
        }
        val bucket = MinioBucket().apply {
            metadata = ObjectMeta().apply {
                name = "bucket"
                namespace = "default"
                managedFields = listOf(ManagedFieldsEntry().apply { this.fieldsV1 = fieldsV1 })
            }
        }

        val clone = serialization.clone(bucket)

        assertEquals(fieldsV1.additionalProperties, clone.metadata.managedFields.single().fieldsV1.additionalProperties)
    }
}
