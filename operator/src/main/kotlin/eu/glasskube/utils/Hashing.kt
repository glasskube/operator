package eu.glasskube.utils

val Any.resourceHash: String
    get() = "%08x".format(Int.MAX_VALUE + hashCode().toLong())
