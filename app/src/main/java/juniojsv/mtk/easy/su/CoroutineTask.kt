package juniojsv.mtk.easy.su

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class CoroutineTask<T>(private val onPostExecute: (result: T) -> Unit) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    fun cancel() {
        job.cancel()
    }

    fun execute() {
        onPreExecute()
        launch {
            val result = doInBackground()
            launch(Dispatchers.Main) {
                onPostExecute(result)
            }
        }
    }

    abstract fun onPreExecute()
    abstract fun doInBackground(): T
}