package eu.glasskube.operator

import eu.glasskube.kubernetes.api.model.affinity
import eu.glasskube.kubernetes.api.model.labelSelector
import eu.glasskube.kubernetes.api.model.podAffinity
import eu.glasskube.kubernetes.api.model.podAffinityTerm

object Affinities {
    private const val TOPOLOGY_KEY = "kubernetes.io/hostname"

    fun podAffinityFor(labels: Map<String, String>) =
        affinity {
            podAffinity {
                requiredDuringSchedulingIgnoredDuringExecution = listOf(
                    podAffinityTerm {
                        labelSelector {
                            matchLabels = labels
                        }
                        topologyKey = TOPOLOGY_KEY
                    }
                )
            }
        }
}
