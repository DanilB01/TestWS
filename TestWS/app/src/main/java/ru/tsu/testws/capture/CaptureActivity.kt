package ru.tsu.testws.capture

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.tsu.testws.R
import ru.tsu.testws.databinding.ActivityCaptureBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CaptureActivity : AppCompatActivity(R.layout.activity_capture) {

    private val binding: ActivityCaptureBinding by viewBinding()

    val REQUEST_CODE = 200

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                capturePhoto()
            } else {
                AlertDialog.Builder(this)
                    .setMessage("Permissions restricted")
                    .setPositiveButton("OK") { _,_ -> }
                    .create()
                    .show()
            }
        }

    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
            binding.capturedImage.setImageBitmap(data.extras?.get("data") as Bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.capturedImage.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    capturePhoto()
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA)
                }
            }
        }
    }
}