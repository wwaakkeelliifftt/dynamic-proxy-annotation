package dev.androidbroadcast.analyticsproxy

import kotlin.system.measureNanoTime

fun main() {
    val analyticsProxy = AnalyticsProxy.Builder()
        .analyticsTracker(LogAnalyticsTracker())
        .build()

    val appAnalytics = analyticsProxy.create<AppAnalytics>()
    repeat(44) {
        println(measureNanoTime { appAnalytics.trackAppStarted() })
    }
    appAnalytics.trackClick(count = 7)

}

private class LogAnalyticsTracker: AnalyticsTracker {
    override fun trackEvent(eventName: String, params: Map<String, Any>?) {
        if (params.isNullOrEmpty()) {
            println(eventName)
        } else {
            println("$eventName($params)")
        }
    }
}
