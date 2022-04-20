package ru.tsu.testws

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.annotations.Contract
import retrofit2.HttpException
import ru.tsu.testws.databinding.ActivityMainBinding
import ru.tsu.testws.databinding.ActivityProfileBinding
import ru.tsu.testws.network.ApiRepo
import ru.tsu.testws.network.Network
import ru.tsu.testws.network.auth.ErrorResponse
import java.io.File

class ProfileActivity : AppCompatActivity(R.layout.activity_profile) {

    private val viewBinding: ActivityProfileBinding by viewBinding()
    private val api by lazy { ApiRepo(Network.retrofit) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 200)
        }
        GlobalScope.launch(Dispatchers.Main) {
            try {
                //api.deleteAvatar()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 200) {
            if(resultCode == RESULT_OK) {
                val uri: Uri = data?.data ?: return


                val stream = contentResolver.openInputStream(uri)
                val file = File.createTempFile("newFile", ".jpeg", cacheDir)
                org.apache.commons.io.FileUtils.copyInputStreamToFile(stream, file)
                val requestBody = RequestBody.create(
                    contentResolver.getType(uri)!!.toMediaType(),
                    file
                )
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        api.uploadAvatar(
                            MultipartBody.Part.createFormData("avatar", file.name, requestBody)
                        )
                        file.delete()
                        api.getProfile()
                    } catch(e: HttpException) {
                        val code = e.code()
                        val message = e.message()
                        val response = Gson().fromJson(e.response()!!.errorBody()!!.string(), ErrorResponse::class.java)
                        Toast.makeText(this@ProfileActivity, "$code $message $response}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}