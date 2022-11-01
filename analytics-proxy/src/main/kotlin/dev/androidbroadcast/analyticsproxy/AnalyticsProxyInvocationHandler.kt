package dev.androidbroadcast.analyticsproxy

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

interface AnalyticsTracker {
    fun trackEvent(eventName: String, param: Map<String, Any>? = null)
}

class AnalyticsProxyInvocationHandler(
    private val tracker: AnalyticsTracker
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        checkAnalyticsMethod(method)

        val eventName = method.annotations.firstNotNullOf { it as EventName }
        val analyticsEventName = eventName.value
        if (method.parameterCount == 0) {
            tracker.trackEvent(analyticsEventName)
        } else {
            checkNotNull(args)
            val annotations = method.parameterAnnotations
            val analyticsParameterNames = annotations.map { paramAnnotations ->
                paramAnnotations.firstNotNullOf { it as? Param }.value
            }
            val analyticsParam: Map<String, Any> = buildMap {
                repeat(method.parameterCount) { index ->
                    put(analyticsParameterNames[index], args[index])
                }
            }
            tracker.trackEvent(analyticsEventName, analyticsParam)
        }
        return Unit
    }

    private fun checkAnalyticsMethod(method: Method) {
        check(method.annotations.any { it is EventName }) {
            "Analytics function has no EventName annotation"
        }
        method.parameterAnnotations.forEach { paramAnnotation ->
            check(paramAnnotation.any { it is Param }) {
                "Analytics function parameters has no Param annotation"
            }
        }
    }
}
