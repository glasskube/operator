package eu.glasskube.operator.infra.minio

import eu.glasskube.kubernetes.api.model.metadata
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MinioBucketKtTest {
    @Test
    fun `defaultBucketName with length 63 should not be truncated`() {
        val name = "a".repeat(30)
        val namespace = "b".repeat(32)
        val bucket = minioBucket {
            metadata {
                name(name)
                namespace(namespace)
            }
        }
        assertEquals("$name-$namespace", bucket.defaultBucketName)
    }

    @Test
    fun `defaultBucketName with length 64 should be truncated`() {
        val name = "a".repeat(31)
        val namespace = "b".repeat(32)
        val bucket = minioBucket {
            metadata {
                name(name)
                namespace(namespace)
            }
        }
        assertEquals(63, bucket.defaultBucketName.length)
    }
}
