package ru.tsu.testws.combinedview

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.tsu.testws.R
import ru.tsu.testws.bottomnav.FirstFragment
import ru.tsu.testws.bottomnav.SecondFragment
import ru.tsu.testws.bottomnav.ThirdFragment
import ru.tsu.testws.combinedview.ui.theme.TestWSTheme
import ru.tsu.testws.databinding.ActivityCombinedviewBinding

class CombinedViewActivity : AppCompatActivity(R.layout.activity_combinedview) {

    private val binding: ActivityCombinedviewBinding by viewBinding()

    private val dataset = listOf(
        Pair("Element 1", "Description of this element"),
        Pair("Element 2", "Description of this element"),
        Pair("Element 3", "Description of this element"),
        Pair("Element 4", "Description of this element"),
        Pair("Element 5", "Description of this element"),
        Pair("Element 6", "Description of this element"),
        Pair("Element 7", "Description of this element"),
        Pair("Element 8", "Description of this element"),
        Pair("Element 9", "Description of this element"),
        Pair("Element 10", "Description of this element"),
        Pair("Element 11", "Description of this element"),
        Pair("Element 12", "Description of this element")
    )

    private val sensorManager: SensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val sensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) }
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            val degrees = p0!!.values
            Log.e("Sensor", degrees[0].toString())
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.bottomNav.apply {
            setContent {
                var isSelected1 by remember { mutableStateOf(true) }
                var isSelected2 by remember { mutableStateOf(false) }
                var isSelected3 by remember { mutableStateOf(false) }

                BottomNavigation(
                    backgroundColor = Color.White
                ) {
                    BottomNavigationItem(
                        selected = isSelected1,
                        onClick = {
                            isSelected1 = true
                            isSelected2 = false
                            isSelected3 = false
                            openFragment(FirstFragment())
                        },
                        icon = {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(color = if(isSelected1) Color.Black else Color.Gray)
                                )
                                Text(text = "First", color = Color.Black)
                            }
                        }
                    )

                    BottomNavigationItem(
                        selected = isSelected2,
                        onClick = {
                            isSelected1 = false
                            isSelected2 = true
                            isSelected3 = false
                            openFragment(SecondFragment())
                        },
                        icon = {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(color = if(isSelected2) Color.Black else Color.Gray)
                                )
                                Text(text = "Second")
                            }
                        }
                    )

                    BottomNavigationItem(
                        selected = isSelected3,
                        onClick = {
                            isSelected1 = false
                            isSelected2 = false
                            isSelected3 = true
                            openFragment(ThirdFragment())
                        },
                        icon = {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(color = if(isSelected3) Color.Black else Color.Gray)
                                )
                                Text(text = "Third")
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.composeFragmentContainer, fragment, fragment.tag).commit()
    }
}