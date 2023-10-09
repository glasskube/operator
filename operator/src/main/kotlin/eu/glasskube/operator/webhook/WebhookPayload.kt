package eu.glasskube.operator.webhook

data class WebhookPayload(val status: Status) {
    enum class Status {
        READY, NOT_READY;

        companion object {
            fun from(isReady: Boolean) = if (isReady) READY else NOT_READY
        }
    }

    companion object {
        fun from(isReady: Boolean) = WebhookPayload(Status.from(isReady))
    }
}
