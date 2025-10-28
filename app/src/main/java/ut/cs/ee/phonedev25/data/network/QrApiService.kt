package ut.cs.ee.phonedev25.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QrApiService {
    @GET("create-qr-code/")
    suspend fun generateQr(
        @Query("data") data: String,
        @Query("size") size: String = "200x200"
    ): Response<okhttp3.ResponseBody>
}
