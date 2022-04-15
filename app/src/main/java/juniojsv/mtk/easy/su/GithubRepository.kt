package juniojsv.mtk.easy.su

import retrofit2.Call
import retrofit2.http.GET

interface GithubRepository {
    @GET("/repos/juniojsv/mtk-easy-su/releases/latest")
    fun getLatestRelease(): Call<GithubRelease>
}