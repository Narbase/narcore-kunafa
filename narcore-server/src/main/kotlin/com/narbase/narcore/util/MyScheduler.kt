package com.narbase.narcore.util


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/***
 * This is a simple attempt to isolate the code from the actual scheduler used so that it can be easily replaced later

 */
abstract class MyScheduler {
    abstract fun scheduleRepeated(initialDelay: Long, waitingPeriod: Long, action: () -> Unit)
    abstract fun cancel()
}

class MyCoroutineScheduler : MyScheduler() {
    private var job: Job? = null
    var didCancel: Boolean = false
        private set

    override fun scheduleRepeated(initialDelay: Long, waitingPeriod: Long, action: () -> Unit) {
        job = GlobalScope.launch {
            delay(initialDelay)
            while (didCancel.not()) {
                try {
                    action()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                delay(waitingPeriod)
            }
        }
    }

    override fun cancel() {
        job?.cancel()
        didCancel = true

    }
}

class MyJavaScheduler(val poolSize: Int = 10) : MyScheduler() {
    val poolExecutor by lazy {
        ScheduledThreadPoolExecutor(poolSize)
    }

    override fun scheduleRepeated(initialDelay: Long, waitingPeriod: Long, action: () -> Unit) {
        poolExecutor.scheduleAtFixedRate({
            try {
                action()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }, initialDelay, waitingPeriod, TimeUnit.MILLISECONDS)
    }

    override fun cancel() {
        poolExecutor.shutdownNow()
    }

}

enum class SchedulerType {
    Coroutine,
    JavaPoolExecutor
}

fun scheduleRepeated(
    initialDelay: Long,
    waitingPeriod: Long,
    schedulerType: SchedulerType = SchedulerType.Coroutine,
    action: () -> Unit
): MyScheduler {
    val scheduler = when (schedulerType) {
        SchedulerType.Coroutine -> MyCoroutineScheduler()
        SchedulerType.JavaPoolExecutor -> MyJavaScheduler()
    }
    scheduler.scheduleRepeated(initialDelay, waitingPeriod, action)
    return scheduler
}
