package eu.glasskube.operator.minio

import eu.glasskube.operator.Labels
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("glasskube.eu")
@Version("v1alpha1")
class MinioBucket : CustomResource<MinioBucketSpec, MinioBucketStatus>(), Namespaced {
    companion object {
        internal const val APP_NAME = "bucket"
        internal const val USERNAME_KEY = "username"
        internal const val PASSWORD_KEY = "password"
    }
}

fun minioBucket(block: MinioBucket.() -> Unit) = MinioBucket().apply(block)
val MinioBucket.genericResourceName get() = "${metadata.name}-bucket"
val MinioBucket.resourceLabels get() = Labels.resourceLabels(MinioBucket.APP_NAME, metadata.name)
private val MinioBucket.defaultPolicy
    get(): String = """
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Action": [
                        "s3:*"
                    ],
                    "Resource": [
                        "arn:aws:s3:::$bucketName/*"
                    ]
                }
            ]
        }
    """.trimIndent()
val MinioBucket.policy get() = spec.policyOverride ?: defaultPolicy
private val MinioBucket.defaultBucketName get() = "${metadata.name}-${metadata.namespace}"
val MinioBucket.bucketName get() = spec.bucketNameOverride ?: defaultBucketName
val MinioBucket.policyName get() = bucketName
