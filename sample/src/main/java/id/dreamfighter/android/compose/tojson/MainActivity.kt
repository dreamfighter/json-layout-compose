package id.dreamfighter.android.compose.tojson

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.dreamfighter.android.compose.tojson.ui.model.ConstructPart
import id.dreamfighter.android.compose.tojson.ui.model.parts.ListItems

class Payload(val listItems: List<ListItems>)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadPayload(payloadMajidTv)?.let{
            setContent {
                Greeting(payload = it)

            }
        }
    }
}

@Composable
fun Greeting(payload: Payload) {
    val dynamicListItem = payload.listItems
    val modifier = Modifier
    for(item in dynamicListItem) {
        ConstructPart(
            item,
            modifier
        )
    }
}

