package ru.tsu.testws

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.tsu.testws.bottomnav.BottomNavActivity
import ru.tsu.testws.capture.CaptureActivity
import ru.tsu.testws.combinedview.CombinedViewActivity
import ru.tsu.testws.databinding.ActivityMainBinding
import ru.tsu.testws.firebase.FirebaseActivity
import ru.tsu.testws.network.ApiRepo
import ru.tsu.testws.network.Network
import ru.tsu.testws.network.auth.ErrorResponse
import ru.tsu.testws.network.auth.AuthForm
import ru.tsu.testws.pager.PagerActivity
import ru.tsu.testws.tabs.TabsActivity
import ru.tsu.testws.webview.WebActivity

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewBinding: ActivityMainBinding by viewBinding()
    private val api by lazy { ApiRepo(Network.retrofit) }

    private val messageClient by lazy { Wearable.getMessageClient(this) }

    private var nodeId: String? = null

    private fun sendMessage() {
        val capabilityInfo = Tasks.await(
            Wearable.getCapabilityClient(this)
                .getAllCapabilities(CapabilityClient.FILTER_REACHABLE)
        )

        nodeId = capabilityInfo.flatMap { it.value.nodes }.firstOrNull { it.isNearby }?.id
    }

    private fun requestData() {
        nodeId?.let {
            messageClient
                .sendMessage(it, EXAMPLE_CAPABILITY_PATH, "token".toByteArray())
                .apply {
                    addOnSuccessListener {
                        Log.i("Mobile", "Token sent")
                    }
                    addOnFailureListener {
                        Log.i("Mobile", "Error sent token")
                    }
                }
        }
    }

    private companion object {
        const val EXAMPLE_CAPABILITY_PATH = "/example"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalScope.launch {
            delay(1000)
            sendMessage()
            requestData()
        }




        viewBinding.registerButton.setOnClickListener {

                GlobalScope.launch(Dispatchers.Main) {
                        try {
                            val token = api.login(
                                AuthForm(
                                    email = viewBinding.emailEditText.text.toString(),
                                    password = viewBinding.passwordEditText.text.toString()
                                )
                            )
                            Network.token = token
                            Toast.makeText(this@MainActivity, "Success!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                        } catch (e: HttpException) {
                            val code = e.code()
                            val message = e.message()
                            val response = Gson().fromJson(e.response()!!.errorBody()!!.string(), ErrorResponse::class.java)
                            Toast.makeText(this@MainActivity, "$code $message ${response.message}", Toast.LENGTH_LONG).show()
                        }

                }
        }

        viewBinding.mapButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        viewBinding.mediaButton.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        viewBinding.bottomNavButton.setOnClickListener {
            startActivity(Intent(this, BottomNavActivity::class.java))
        }

        viewBinding.pagerButton.setOnClickListener {
            startActivity(Intent(this, PagerActivity::class.java))
        }

        viewBinding.tabsButton.setOnClickListener {
            startActivity(Intent(this, TabsActivity::class.java))
        }

        viewBinding.combineActivityButton.setOnClickListener {
            startActivity(Intent(this, CombinedViewActivity::class.java))
        }

        viewBinding.webButton.setOnClickListener {
            startActivity(Intent(this, WebActivity::class.java))
        }

        viewBinding.firebaseButton.setOnClickListener {
            startActivity(Intent(this, FirebaseActivity::class.java))
        }

        viewBinding.takePhotoButton.setOnClickListener {
            startActivity(Intent(this, CaptureActivity::class.java))
        }
    }
}