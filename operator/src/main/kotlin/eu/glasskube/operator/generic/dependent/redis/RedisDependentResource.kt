package eu.glasskube.operator.generic.dependent.redis

interface RedisDependentResource<in T> {
    val redisNameMapper: RedisNameMapper<T>
}
