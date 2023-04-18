package id.dreamfighter.android.compose.tojson.ui.model

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.dynamicitemlazycolumn.R
import id.dreamfighter.android.compose.tojson.ui.model.parts.*
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.FontSize
import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import id.dreamfighter.android.compose.tojson.ui.model.utils.color
import id.dreamfighter.android.compose.tojson.ui.model.utils.createModifier
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun ConstructPart(
    listItems: ListItems,
    modifier: Modifier = Modifier,
    data: SnapshotStateMap<String,Any?> = SnapshotStateMap<String,Any?>(),
    event:(String,Any) -> Unit = {_,_ ->}
) {
    when (listItems.type) {
        Type.TEXT -> {
            val textPart = listItems as Text
            var partModifier = modifier
            Log.d("TEXT","${textPart.name}=>${data[textPart.name]}")
            val text = if(data[textPart.name]!=null){
                data[textPart.name] as String
            }else{
                textPart.message
            }
            val textAlign = when (listItems.textAlign) {
                Align.START -> TextAlign.Left
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

            var fontWeight = when(textPart.fontWeight){
                "BOLD" -> FontWeight.Bold
                "THIN" -> FontWeight.Thin
                "LIGHT" -> FontWeight.Light
                else -> FontWeight.Normal
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
                    "background" -> partModifier =
                        partModifier.background(value.toString().color)
                    "clip" ->{
                        val clip = value as Map<String,String>
                        when("${clip["type"]}"){
                            "ROUND" -> {
                                var topEnd = 0.dp
                                if(clip["topEnd"]!=null){
                                    topEnd = (clip["topEnd"] as Double).dp
                                }
                                partModifier =
                                    partModifier.clip(RoundedCornerShape(topEnd = topEnd))
                            }
                        }
                    }
                    "animated" -> {
                        val animated = value as Map<String,String>
                        Log.d("PADDING","${animated["type"]}")
                        when("${animated["type"]}"){
                            "scroll" -> {
                                val scrollState = rememberScrollState()
                                var shouldAnimated by remember {
                                    mutableStateOf(true)
                                }
                                LaunchedEffect(key1 = shouldAnimated){
                                    scrollState.animateScrollTo(
                                        scrollState.maxValue,
                                        animationSpec = tween(20000, 200, easing = CubicBezierEasing(0f,0f,0f,0f))
                                    )
                                    scrollState.scrollTo(0)
                                    shouldAnimated = !shouldAnimated
                                }
                                partModifier =
                                    partModifier.horizontalScroll(scrollState, false)
                            }
                        }

                    }
                }
            }

            Text(
                maxLines = textPart.maxLines,
                text = text,
                color = color,
                modifier = partModifier,
                textAlign = textAlign,
                fontSize = fontSize,
                fontWeight = fontWeight
            )

        }

        Type.ANIMATED_VISIBILITY -> {
            var visible by remember { mutableStateOf(false) }
            val animated = listItems as AnimatedVisibility
            val items = animated.listItems

            LaunchedEffect(Unit) {
                while(true) {
                    visible = !visible
                    delay(10000)
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth ->
                    // Offsets the content by 1/3 of its width to the left, and slide towards right
                    // Overwrites the default animation with tween for this slide animation.
                    -fullWidth
                } + fadeIn(
                    // Overwrites the default animation with tween
                    animationSpec = tween(durationMillis = 200)
                ),
                exit = slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) {fullWidth->
                    // Overwrites the ending position of the slide-out to 200 (pixels) to the right
                    -fullWidth
                } + fadeOut()
            ) {
                // Content that needs to appear/disappear goes here:
                items?.let {
                    for (item in items) {
                        item.props.forEach { (key, value) ->
                            Log.d("PROPS","$key => $value")

                        }
                        ConstructPart(item, modifier = modifier,data)
                    }
                }
            }
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

            if(imagePart.props["contentScale"] == "FillWidth"){

                contentScale = ContentScale.FillWidth
            }
            if(imagePart.props["fillMaxWidth"] == true){
                partModifier = partModifier.fillMaxWidth()
            }
            Image(image , "", modifier = partModifier, contentScale = contentScale)
        }

        Type.VIDEO -> {
            var contentScale = ContentScale.Fit
            val imagePart = listItems as Image
            val imageAlign = when (imagePart.imageAlign) {
                Align.START -> Alignment.TopStart
                Align.END -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            var partModifier = modifier
            val image: Painter = painterResource(id = R.drawable.temp_masjid_img)

            if(imagePart.props["contentScale"] == "FillWidth"){

                contentScale = ContentScale.FillWidth
            }
            if(imagePart.props["fillMaxWidth"] == true){
                partModifier = partModifier.fillMaxWidth()
            }
            Image(image , "", modifier = partModifier, contentScale = contentScale)
        }

        Type.GLIDE_IMAGE -> {
            var contentScale = ContentScale.Fit
            val imagePart = listItems as GlideImagePart
            val imageAlign = when (imagePart.imageAlign) {
                Align.START -> Alignment.TopStart
                Align.END -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            var partModifier = modifier
            val image: Painter = painterResource(id = R.drawable.temp_masjid_img)

            if(imagePart.props["contentScale"] == "FillWidth"){

                contentScale = ContentScale.FillWidth
            }
            if(imagePart.props["fillMaxWidth"] == true){
                partModifier = partModifier.fillMaxWidth()
            }

            if(imagePart.glideUrl!=null){
                GlideImage(
                    model = imagePart.glideUrl,
                    modifier = partModifier,
                    contentDescription = "", contentScale = contentScale
                )
            }else {
                Image(image , "", modifier = partModifier, contentScale = contentScale)
            }
            //Image(image , "", modifier = partModifier, contentScale = contentScale)
        }

        Type.BOX -> {
            val box = listItems as Box
            val items = box.listItems
            var partModifier = modifier

            Log.d("BOX_PROPS","${box.props}")
            box.props.forEach { (key, value) ->
                Log.d("BOX_PROPS","$key => $value")
                when(key){
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                    "background" -> partModifier = partModifier.background(value.toString().color)
                }
            }

            //partModifier = content(partModifier,box.props)

            Box(
                modifier = partModifier
            ) {
                items?.let {
                    for (item in items) {
                        val modifier = Modifier
                        item.props.forEach { (key, value) ->
                            Log.d("PROPS","$key => $value")
                        }
                        ConstructPart(item, modifier = modifier,data)
                    }
                }
            }
        }

        Type.SPACER -> {

        }

        Type.COLUMN -> {
            val column = listItems as Column
            val items = column.listItems
            val horizontalAlignment = when(column.horizontalAlignment){
                "START" -> Alignment.Start
                "END" -> Alignment.End
                else -> Alignment.CenterHorizontally
            }

            Column(
                horizontalAlignment = horizontalAlignment,
                modifier = modifier
            ) {
                for (item in items) {
                    var partModifier = createModifier(listItems = item)
                    item.props.forEach { (key, value) ->
                        Log.d("COLUMN_PROPS","$key => $value")
                        when(key){
                            "align" ->  partModifier = Modifier.align(alignment = when(value) {
                                "START" -> Alignment.Start
                                else -> Alignment.Start
                            })
                            "weight" -> partModifier = Modifier.weight((value as Double).toFloat())
                            "fillWeight" -> partModifier = Modifier.weight((value as Double).toFloat(),fill = true)
                        }
                    }
                    ConstructPart(item, partModifier,data)
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
                    var modifierItem = createModifier(item)
                    item.props.forEach { (key, value) ->
                        Log.d("PROPS","$key => $value")
                        when(key){
                            "weight" -> modifierItem = modifierItem.weight((value as Double).toFloat())
                            "fillWeight" -> modifierItem = modifierItem.weight((value as Double).toFloat(),fill = true)
                            "padding" -> {
                                val padding = value as Map<String,Double>
                                padding["start"]?.let {
                                    modifierItem = modifierItem.padding(start = it.dp)
                                }
                                padding["end"]?.let {
                                    modifierItem = modifierItem.padding(end = it.dp)
                                }
                            }
                            "background" -> modifierItem.background(value.toString().color)
                        }
                    }
                    ConstructPart(item, modifierItem,data)
                }
            }
        }
        Type.CARD -> {
            val card = listItems as CardPart
            val items = card.listItems
            var partModifier = modifier
            var color = Color.White
            var elevation = 10.dp

            if(card.cardBackgroundColor!=null){
                color = card.cardBackgroundColor.color
            }
            if(card.elevation!=null){
                elevation = card.elevation.dp
            }

            card.props.forEach { (key, value) ->
                Log.d("CARD_PROPS","$key => $value")
                when(key){
                    "height" -> partModifier = partModifier.height((value as Double).dp)
                    "background" -> partModifier = partModifier.background(value.toString().color)
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                }
            }
            Card(
                modifier = partModifier,
                elevation = elevation,
                backgroundColor = color
            ) {
                for (item in items) {
                    var modifierItem = Modifier.background("#00FFFFFF".color)
                    item.props.forEach { (key, value) ->
                        Log.d("CARD_PROPS","$key => $value")
                        when(key) {
                            "background" -> modifierItem =
                                modifierItem.background(value.toString().color)
                        }
                    }
                    ConstructPart(item, modifierItem,data)
                }
            }
        }
    }
}
