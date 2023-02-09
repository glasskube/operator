package eu.glasskube.operator

import io.fabric8.kubernetes.client.KubernetesClient
import io.minio.MinioClient
import io.minio.admin.MinioAdminClient
import io.minio.credentials.Credentials
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig {
    @Bean
    fun minioClient(credentials: Credentials): MinioClient =
        MinioClient.builder()
            .endpoint("http://${Environment.MINIO_HOST_NAME}:9000")
            .credentialsProvider { credentials }
            .build()

    @Bean
    fun getMinioAdminClient(credentials: Credentials): MinioAdminClient =
        MinioAdminClient.builder()
            .endpoint("http://${Environment.MINIO_HOST_NAME}:9000")
            .credentialsProvider { credentials }
            .build()

    @Bean
    fun getMinioCredentials(kubernetesClient: KubernetesClient): Credentials =
        kubernetesClient.secrets().inNamespace(Environment.NAMESPACE).withName(Environment.MINIO_SECRET_NAME).get()
            ?.let {
                Credentials(
                    it.data.getValue("rootUser").decodeBase64(),
                    it.data.getValue("rootPassword").decodeBase64(),
                    null,
                    null
                )
            }
            ?: throw IllegalStateException("Secret ${Environment.MINIO_SECRET_NAME} does not exist")
}
