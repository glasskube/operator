package eu.glasskube.operator.generic.dependent.postgres

abstract class PostgresNameMapper<in P> {
    abstract fun getName(primary: P): String
    abstract fun getLabels(primary: P): Map<String, String>
    abstract fun getDatabaseName(primary: P): String
    val P.postgresClusterName get() = getName(this)
    val P.postgresDatabaseName get() = getDatabaseName(this)
    val P.postgresHostName get() = "$postgresClusterName-rw"
    val P.postgresSecretName get() = "$postgresClusterName-app"
}
