package eu.glasskube.kubernetes.api.model.batch

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.batch.v1.CronJob
import io.fabric8.kubernetes.api.model.batch.v1.CronJobSpec
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec
import io.fabric8.kubernetes.api.model.batch.v1.JobTemplateSpec

inline fun CronJob.spec(block: (@KubernetesDslMarker CronJobSpec).() -> Unit) {
    spec = CronJobSpec().apply(block)
}

inline fun CronJobSpec.jobTemplate(block: (@KubernetesDslMarker JobTemplateSpec).() -> Unit) {
    jobTemplate = JobTemplateSpec().apply(block)
}

inline fun JobTemplateSpec.spec(block: (@KubernetesDslMarker JobSpec).() -> Unit) {
    spec = JobSpec().apply(block)
}

inline fun JobSpec.template(block: (@KubernetesDslMarker PodTemplateSpec).() -> Unit) {
    template = PodTemplateSpec().apply(block)
}
