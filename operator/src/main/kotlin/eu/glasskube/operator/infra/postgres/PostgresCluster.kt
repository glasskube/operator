package eu.glasskube.operator.infra.postgres

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Kind
import io.fabric8.kubernetes.model.annotation.Version

@Kind("Cluster")
@Group("postgresql.cnpg.io")
@Version("v1")
class PostgresCluster : CustomResource<ClusterSpec, ClusterStatus>(), Namespaced {
    override fun setSpec(spec: ClusterSpec?) {
        super.setSpec(spec)
    }
}

inline fun postgresCluster(block: (PostgresCluster).() -> Unit) = PostgresCluster().apply(block)
