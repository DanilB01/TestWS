package com.example.wear

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.wear.databinding.ActivityMainBinding
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

class MainActivity : Activity(), MessageClient.OnMessageReceivedListener {

    private lateinit var binding: ActivityMainBinding
    private val messageClient by lazy { Wearable.getMessageClient(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageClient.addListener(this)
    }

    override fun onMessageReceived(event: MessageEvent) {
        println(event)
        if (event.path != EXAMPLE_CAPABILITY_PATH) return

        val token = String(event.data)
        println(token)
        binding.text.text = token
    }

    private companion object {
        const val EXAMPLE_CAPABILITY_PATH = "/example"
    }
}