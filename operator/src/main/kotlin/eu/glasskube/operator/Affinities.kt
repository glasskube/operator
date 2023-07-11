package eu.glasskube.operator

import io.fabric8.kubernetes.api.model.Affinity
import io.fabric8.kubernetes.api.model.LabelSelector
import io.fabric8.kubernetes.api.model.PodAffinity
import io.fabric8.kubernetes.api.model.PodAffinityTerm

object Affinities {
    private const val TOPOLOGY_KEY = "kubernetes.io/hostname"

    fun podAffinityFor(labels: Map<String, String>) =
        Affinity(
            null,
            PodAffinity(
                emptyList(),
                listOf(
                    PodAffinityTerm(
                        LabelSelector(
                            emptyList(),
                            labels
                        ),
                        null,
                        null,
                        TOPOLOGY_KEY
                    )
                )
            ),
            null
        )
}
