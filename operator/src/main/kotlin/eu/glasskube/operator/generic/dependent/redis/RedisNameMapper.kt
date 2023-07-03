package eu.glasskube.operator.generic.dependent.redis

abstract class RedisNameMapper<in T> {
    abstract fun getName(primary: T): String
    abstract fun getLabels(primary: T): Map<String, String>
    abstract fun getLabelSelector(primary: T): Map<String, String>
    abstract fun getVersion(primary: T): String
    val T.redisName get() = getName(this)
}
