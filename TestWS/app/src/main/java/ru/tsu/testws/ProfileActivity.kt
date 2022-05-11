package ru.tsu.testws

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.annotations.Contract
import retrofit2.HttpException
import ru.tsu.testws.databinding.ActivityMainBinding
import ru.tsu.testws.databinding.ActivityProfileBinding
import ru.tsu.testws.network.ApiRepo
import ru.tsu.testws.network.Network
import ru.tsu.testws.network.auth.ErrorResponse
import java.io.File
import java.util.*
import java.util.jar.Manifest

class ProfileActivity : AppCompatActivity(R.layout.activity_profile) {

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            sendProfileImage(uri)
        }
    }

    private val requestMediaPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if(it.all { it.value }) {
            selectImageFromGalleryResult.launch("image/*")
        } else {
            AlertDialog.Builder(this)
                .setTitle("Что-то пошло не так")
                .setPositiveButton("Ясно") {_,_ ->}
                .create()
                .show()
        }
    }

    private val mediaPermissions = listOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val viewBinding: ActivityProfileBinding by viewBinding()
    private val api by lazy { ApiRepo(Network.retrofit) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.profileImage.setOnClickListener {
            if (mediaPermissions.all { checkPermissions(it) }) {
                selectImageFromGalleryResult.launch("image/*")
            } else {
                requestMediaPermissions.launch(mediaPermissions.toTypedArray())
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val profile = api.getProfile()
                    with(viewBinding) {
                        profileName.text = profile.name
                        profileAbout.text = profile.aboutMyself
                        profile.avatar?.let {
                            Glide.with(root).load(it).into(profileImage)
                        }
                    }
            } catch (e: HttpException) {
                val code = e.code()
                val message = e.message()
                val response = Gson().fromJson(e.response()!!.errorBody()!!.string(), ErrorResponse::class.java)
                Toast.makeText(this@ProfileActivity, "$code $message $response", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendProfileImage(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val mimeType = contentResolver.getType(uri) ?: ""
        val file = File.createTempFile(UUID.randomUUID().toString(), ".jpeg", cacheDir)
        org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, file)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                api.uploadAvatar(
                    MultipartBody.Part.createFormData(
                        name = "avatar",
                        filename = file.name,
                        body = file.asRequestBody(contentType = mimeType.toMediaType()))
                )
                file.delete()
                val profile = api.getProfile()
                with(viewBinding) {
                    profileName.text = profile.name
                    profileAbout.text = profile.aboutMyself
                    profile.avatar?.let {
                        Glide.with(root).load(it).into(profileImage)
                    }
                }
            } catch(e: HttpException) {
                val code = e.code()
                val message = e.message()
                val response = Gson().fromJson(e.response()!!.errorBody()!!.string(), ErrorResponse::class.java)
                Toast.makeText(this@ProfileActivity, "$code $message $response}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkPermissions(permission: String) =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
}