package id.dreamfighter.android.compose.tojson.ui.model

import android.util.Log
import androidx.annotation.RestrictTo.Scope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynamicitemlazycolumn.R
import id.dreamfighter.android.compose.tojson.ui.model.parts.*
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.FontSize
import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import id.dreamfighter.android.compose.tojson.ui.model.utils.color
import id.dreamfighter.android.compose.tojson.ui.model.utils.createModifier

@Composable
fun ConstructPart(
    listItems: ListItems,
    modifier: Modifier = Modifier,
    content: @Composable (modifier: Modifier,map:Map<String,Any>) -> Modifier = {modifier,_->modifier}
) {
    when (listItems.type) {
        Type.TEXT -> {
            val textPart = listItems as Text
            var partModifier = modifier
            val text = textPart.message
            val textAlign = when (listItems.textAlign) {
                Align.START -> TextAlign.Start
                Align.END -> TextAlign.End
                Align.CENTER -> TextAlign.Center
                else -> null
            }
            val fontSize = when (listItems.textFont) {
                FontSize.TINY.name -> 8.sp
                FontSize.SMALL.name -> 12.sp
                FontSize.BIG.name -> 20.sp
                FontSize.HUGE.name -> 24.sp
                FontSize.DEFAULT.name -> 12.sp
                else -> listItems.textFont.toInt().sp
            }

            var color = Color.Black

            if(textPart.color!=null){
                color = textPart.color.color
            }
            textPart.props.forEach { (key, value) ->
                Log.d("PROPS","$key => $value")
                when(key){
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                    "padding" -> {
                        val padding = value as Map<String,Double>
                        Log.d("PADDING","${padding["start"]}")
                        padding["start"]?.let {
                            partModifier = partModifier.padding(start = it.dp)
                        }
                        padding["end"]?.let {
                            partModifier = partModifier.padding(end = it.dp)
                        }
                    }
                }
            }

            partModifier = content(partModifier,textPart.props)


            Text(
                maxLines = textPart.maxLines,
                text = text,
                color = color,
                modifier = partModifier,
                textAlign = textAlign,
                fontSize = fontSize
            )

        }
        Type.BUTTON -> {
            val text = (listItems as Button).message
            Button(onClick = { /*TODO*/ }, modifier = modifier) {
                Text(text = text)
            }
        }
        Type.IMAGE -> {
            var contentScale = ContentScale.Fit
            val imagePart = listItems as Image
            val imageAlign = when (imagePart.imageAlign) {
                Align.START -> Alignment.TopStart
                Align.END -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            var partModifier = modifier
            val image: Painter = painterResource(id = R.drawable.temp_masjid_img)

            if(imagePart.props?.get("contentScale") == "FillWidth"){

                contentScale = ContentScale.FillWidth
            }
            if(imagePart.props?.get("fillMaxWidth") == true){
                partModifier = partModifier.fillMaxWidth()
            }
            Image(image , "", modifier = partModifier, contentScale = contentScale)
        }

        Type.BOX -> {
            val box = listItems as Box
            val items = box.listItems
            var partModifier = modifier

            Log.d("PROPS","${box.props}")
            box.props.forEach { (key, value) ->
                Log.d("PROPS","$key => $value")
                when(key){
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                }
            }

            Box(
                modifier = partModifier
            ) {
                items?.let {
                    for (item in items) {
                        ConstructPart(item, modifier)
                    }
                }

            }
        }

        Type.COLUMN -> {
            val items = (listItems as Column).listItems
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
            ) {
                for (item in items) {
                    var partModifier = createModifier(item)
                    ConstructPart(item, partModifier){ modifier, props ->
                        props.forEach { (key, value) ->
                            Log.d("PROPS","$key => $value")
                            when(key){
                                "weight" -> partModifier = Modifier.weight(value as Float)
                            }
                        }
                        modifier
                    }
                }
            }
        }
        Type.ROW -> {
            val row = listItems as Row
            val items = row.listItems
            var partModifier = modifier

            row.props.forEach { (key, value) ->
                Log.d("PROPS","$key => $value")
                when(key){
                    "height" -> partModifier = partModifier.height((value as Double).dp)
                    "background" -> partModifier = partModifier.background(value.toString().color)
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = partModifier
            ) {
                for (item in items) {
                    ConstructPart(item, createModifier(item)){ modifier,props->
                        props.forEach { (key, value) ->
                            Log.d("PROPS","$key => $value")
                            when(key){
                                "weight" -> partModifier = Modifier.weight(value as Float)
                            }
                        }
                        modifier
                    }
                }
            }
        }
    }
}
