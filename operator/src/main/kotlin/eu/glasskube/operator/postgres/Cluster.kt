package eu.glasskube.operator.postgres

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version

@Group("postgresql.cnpg.io")
@Version("v1")
class Cluster : CustomResource<ClusterSpec, ClusterStatus>(), Namespaced {
    override fun setSpec(spec: ClusterSpec?) {
        super.setSpec(spec)
    }
}

inline fun postgresCluster(block: (Cluster).() -> Unit) = Cluster().apply(block)
