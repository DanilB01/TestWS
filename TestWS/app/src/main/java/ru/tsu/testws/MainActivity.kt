package ru.tsu.testws

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.tsu.testws.bottomnav.BottomNavActivity
import ru.tsu.testws.databinding.ActivityMainBinding
import ru.tsu.testws.network.ApiRepo
import ru.tsu.testws.network.Network
import ru.tsu.testws.network.auth.ErrorResponse
import ru.tsu.testws.network.auth.AuthForm

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewBinding: ActivityMainBinding by viewBinding()
    private val api by lazy { ApiRepo(Network.retrofit) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }
}