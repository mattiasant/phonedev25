package ut.cs.ee.phonedev25.data.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {
    val qrApi: QrApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.qrserver.com/v1/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(QrApiService::class.java)
    }
}
