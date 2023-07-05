package eu.glasskube.operator.generic.dependent.postgres

interface PostgresDependentResource<in P> {
    val postgresNameMapper: PostgresNameMapper<P>
}
