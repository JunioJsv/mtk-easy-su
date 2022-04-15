package juniojsv.mtk.easy.su

import com.google.gson.annotations.SerializedName

data class GithubRelease(
    @SerializedName("html_url") val url: String,
    @SerializedName("tag_name") val tag: String
)