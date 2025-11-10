package ut.cs.ee.phonedev25

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.Preview
import ut.cs.ee.phonedev25.ui.theme.QrViewModel

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage


class Join_Game : AppCompatActivity() {

    private val qrViewModel: QrViewModel by viewModels()
    private var activeDialog: AlertDialog? = null
    private var scanInfoText: TextView? = null

    private var lastDetectedValue: String? = null
    private var lastDetectionTime = 0L

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) showQrScanPopup()
            else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<ImageView>(R.id.imageView3).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.startGame).setOnClickListener {
            startActivity(Intent(this, gameArena::class.java))
        }

        // --- QR Code Feature ---
        val makeCodeText = findViewById<TextView>(R.id.makeCode_text)
        val makeCodeImage = findViewById<ImageView>(R.id.imageView5)

        val generateAction = {
            // generate a new random code *each time*
            val qrData = "JoinCode:${(1000..9999).random()}"
            Toast.makeText(this, "Generating QR for $qrData", Toast.LENGTH_SHORT).show()
            qrViewModel.generateQr(qrData)
            qrViewModel.qrBitmap.observe(this) { bitmap: Bitmap? ->
                if (bitmap != null) {
                    showQrPopup(qrData, bitmap)
                }
            }
        }

        makeCodeText.setOnClickListener { generateAction() }
        makeCodeImage.setOnClickListener { generateAction() }

        // --- Scan QR ---
        val scanText = findViewById<TextView>(R.id.ScanCode_text)
        val scanImage = findViewById<ImageView>(R.id.imageView6)

        val scanAction = {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                showQrScanPopup()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        scanText.setOnClickListener { scanAction() }
        scanImage.setOnClickListener { scanAction() }

        qrViewModel.error.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

    }
    fun showQrPopup(code: String, bitmap: Bitmap) {
        // Dismiss previous popup if one is open
        activeDialog?.dismiss()

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_qr_popup, null)
        val qrImage = dialogView.findViewById<ImageView>(R.id.qrCodeImage)
        val codeText = dialogView.findViewById<TextView>(R.id.qrCodeText)
        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)

        qrImage.setImageBitmap(bitmap)
        codeText.text = code

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        closeButton.setOnClickListener { dialog.dismiss() }

        dialog.show()
        activeDialog = dialog
    }
    fun showQrScanPopup() {
        activeDialog?.dismiss()
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_qr_scan_popup, null)
        scanInfoText = dialogView.findViewById(R.id.scanInfo)
        val closeButton = dialogView.findViewById<Button>(R.id.closeScanButton)
        val previewView = dialogView.findViewById<PreviewView>(R.id.cameraPreview)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        closeButton.setOnClickListener { dialog.dismiss() }

        // Start camera preview
        startCamera(previewView)

        dialog.show()
        activeDialog = dialog
    }

    private fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val barcodeScanner = BarcodeScanning.getClient()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy: ImageProxy ->
                        processImageProxy(barcodeScanner, imageProxy)
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                Toast.makeText(this, "Camera error: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(scanner: com.google.mlkit.vision.barcode.BarcodeScanner, imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { value ->
                            val currentTime = System.currentTimeMillis()
                            if (value != lastDetectedValue || currentTime - lastDetectionTime > 2000) {
                                scanInfoText?.text = "Detected: $value"
                                lastDetectedValue = value
                                lastDetectionTime = currentTime
                            }

                        }
                    }
                }
                .addOnFailureListener { e ->
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
