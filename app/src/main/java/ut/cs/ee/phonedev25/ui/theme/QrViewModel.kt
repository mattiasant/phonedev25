package ut.cs.ee.phonedev25.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import ut.cs.ee.phonedev25.data.network.RetrofitInstance
import android.graphics.BitmapFactory
import android.graphics.Bitmap

class QrViewModel : ViewModel() {
    val qrBitmap = MutableLiveData<Bitmap?>()
    val error = MutableLiveData<String>()

    fun generateQr(text: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.qrApi.generateQr(text)
                if (response.isSuccessful) {
                    val bytes = response.body()?.bytes()
                    if (bytes != null) {
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        qrBitmap.postValue(bitmap)
                    } else {
                        error.postValue("Empty QR response.")
                    }
                } else {
                    error.postValue("Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                error.postValue("Error: ${e.localizedMessage}")
            }
        }
    }
}
