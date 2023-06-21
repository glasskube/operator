package eu.glasskube.operator.api.reconciler

interface HasRegistrationCondition {
    /**
     * Whether this controller should be registered by the operator.
     */
    val isRegistrationEnabled: Boolean

    /**
     * Optional string that is printed in logs in case the controller was not registered.
     *
     * This should be used to provide a hint to users what they can do in order to enable this controller.
     */
    val registrationConditionHint: String? get() = null
}
