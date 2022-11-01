package dev.androidbroadcast.analyticsproxy

import java.lang.reflect.Proxy
import kotlin.properties.Delegates

inline fun <reified T: Any> AnalyticsProxy.create(): T {
    return this.create(T::class.java)
}

class AnalyticsProxy private constructor(private val analyticsTracker: AnalyticsTracker) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(clazz: Class<T>): T {
        return Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf(clazz),
            AnalyticsProxyInvocationHandler(analyticsTracker)
        ) as T
    }

    class Builder {
        private var analyticsTracker by Delegates.notNull<AnalyticsTracker>()

        fun analyticsTracker(tracker: AnalyticsTracker): Builder {
            this.analyticsTracker = tracker
            return this
        }

        fun build(): AnalyticsProxy = AnalyticsProxy(analyticsTracker)
    }
}