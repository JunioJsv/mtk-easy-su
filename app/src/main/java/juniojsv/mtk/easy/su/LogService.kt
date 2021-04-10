package juniojsv.mtk.easy.su

import io.github.rybalkinsd.kohttp.dsl.async.httpPostAsync
import io.github.rybalkinsd.kohttp.ext.url
import org.json.JSONObject

data class LogModel(val body: String, val success: Boolean)

object LogService {
    private const val API = "https://api.jsonbin.io"

    suspend fun send(model: LogModel) = httpPostAsync {
        url("$API/b")
        header {
            "Content-Type" to "application/json"
            "secret-key" to BuildConfig.JSON_BIN_SECRET_KEY
            "collection-id" to BuildConfig.JSON_BIN_MTK_EASY_SU_COLLECTION_ID
        }
        body {
            string(
                JSONObject(
                    mutableMapOf(
                        "type" to "log",
                        "message" to model.body
                            .replace("\n", " ")
                            .replace("\t", " ").trim(),
                        "success" to model.success
                    ) as Map<*, *>
                ).toString()
            )
        }
    }.await()
}