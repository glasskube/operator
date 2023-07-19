package eu.glasskube.kubernetes.api.model

import eu.glasskube.kubernetes.api.annotation.KubernetesDslMarker
import io.fabric8.kubernetes.api.model.ExecAction
import io.fabric8.kubernetes.api.model.HTTPGetAction
import io.fabric8.kubernetes.api.model.LifecycleHandler
import io.fabric8.kubernetes.api.model.LifecycleHandlerBuilder
import io.fabric8.kubernetes.api.model.TCPSocketAction

@KubernetesDslMarker
class LifecycleHandlerDsl private constructor() {
    private val builder = LifecycleHandlerBuilder(true)

    fun exec(block: ExecAction.() -> Unit) {
        builder.withExec(ExecAction().apply(block))
    }

    fun httpGet(block: HTTPGetAction.() -> Unit) {
        builder.withHttpGet(HTTPGetAction().apply(block))
    }

    fun tcpSocket(block: TCPSocketAction.() -> Unit) {
        builder.withTcpSocket(TCPSocketAction().apply(block))
    }

    fun build(): LifecycleHandler = builder.build()

    companion object {
        fun (LifecycleHandlerDsl.() -> Unit).build() = LifecycleHandlerDsl().apply(this).build()
    }
}
