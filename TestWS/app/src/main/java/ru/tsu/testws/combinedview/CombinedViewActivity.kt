package ru.tsu.testws.combinedview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.tsu.testws.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.composeView.apply {
            setContent {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(items = dataset) { item ->
                        Card(
                            backgroundColor = colorResource(id = R.color.purple_200),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    modifier = Modifier.size(96.dp)
                                )
                                Text(
                                    text = item.first,
                                    fontSize = 16.sp,
                                    color = colorResource(id = R.color.black),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Text(
                                    text = item.second,
                                    fontSize = 16.sp,
                                    color = colorResource(id = R.color.black),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}