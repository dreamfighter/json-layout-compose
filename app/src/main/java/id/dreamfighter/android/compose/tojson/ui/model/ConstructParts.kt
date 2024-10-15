package id.dreamfighter.android.compose.tojson.ui.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.dynamicitemlazycolumn.R
import id.dreamfighter.android.compose.tojson.ui.model.parts.*
import id.dreamfighter.android.compose.tojson.ui.model.shape.CustomShape
import id.dreamfighter.android.compose.tojson.ui.model.shape.Parallelogram
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.FontSize
import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import id.dreamfighter.android.compose.tojson.ui.model.utils.asOrFail
import id.dreamfighter.android.compose.tojson.ui.model.utils.collectBoxProps
import id.dreamfighter.android.compose.tojson.ui.model.utils.collectRowScopeProps
import id.dreamfighter.android.compose.tojson.ui.model.utils.color
import id.dreamfighter.android.compose.tojson.ui.model.utils.createModifier
import id.dreamfighter.android.compose.tojson.ui.model.utils.gradientBackground
import id.dreamfighter.android.compose.tojson.ui.model.utils.toListOrEmpty
import id.dreamfighter.android.compose.tojson.ui.model.utils.toMapOrEmpty
import id.dreamfighter.android.compose.tojson.ui.model.view.AutoScrollingLazyRow
import id.dreamfighter.android.compose.tojson.ui.theme.DefaultFont
import kotlinx.coroutines.delay
import okhttp3.Headers
import java.io.File

@UnstableApi
class SimpleCacheBuilder private constructor() {
    companion object {

        @Volatile
        private var instance: SimpleCache? = null

        @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
        fun build(context:Context) =
            instance ?: synchronized(this) {
                val databaseProvider = StandaloneDatabaseProvider(context)
                val cacheDir = context.externalCacheDir
                //Log.d("CACHE_DIR","${cacheDir?.exists()} ${cacheDir?.absolutePath}")
                //if(cacheDir?.exists() !){
                //cacheDir?.mkdirs()
                //}
                //Log.d("CACHE_DIR","${it.absolutePath}")

                instance ?: SimpleCache(
                    cacheDir!!,
                    NoOpCacheEvictor(),
                    databaseProvider
                ).also { instance = it }
            }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalAnimationApi::class, ExperimentalGlideComposeApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ConstructPart(
    listItems: ListItems,
    modifier: Modifier = Modifier,
    data: MutableMap<String,Any?> = mutableMapOf(),
    event:(Map<String,Any>) -> Unit = {_ ->}
) {
    when (listItems.type) {
        Type.TEXT -> {
            val textPart = listItems as Text
            var partModifier = modifier
            var fontfamily = DefaultFont
            //var texts = mutableStateListOf<String>()
            var texts by remember { mutableStateOf(listOf<String>()) }
            var hidden by remember {mutableStateOf(false)}
            var setHidden by remember {mutableStateOf(false)}

            //Log.d("TEXT","${textPart.name}=>${data[textPart.name]}")

            var fontWeight = when(textPart.fontWeight){
                "BOLD" -> FontWeight.Bold
                "THIN" -> FontWeight.Thin
                "LIGHT" -> FontWeight.Light
                else -> FontWeight.Normal
            }
            if(data["fonts"]!=null && textPart.fontFamily!=null){
                val fontFamilies = data["fonts"] as Map<*,*>
                fontfamily = fontFamilies[textPart.fontFamily] as
                        FontListFontFamily
            }
            val text = if(data[textPart.name]!=null){
                val datas = data[textPart.name] as Map<*,*>
                if(datas["fontFamily"]!=null){
                    fontfamily = datas["fontFamily"] as
                            FontListFontFamily
                }
                if(datas["fontWeight"]!=null){
                    fontWeight = when(datas["fontWeight"]){
                        "BOLD" -> FontWeight.Bold
                        "THIN" -> FontWeight.Thin
                        "LIGHT" -> FontWeight.Light
                        else -> FontWeight.Normal
                    }
                }
                if(datas["hidden"]!=null) {
                    hidden = datas["hidden"] as Boolean
                    setHidden = true
                }
                if(datas["texts"]!=null && datas["texts"] is MutableList<*>) {
                    texts = (datas["texts"] as MutableList<String>).map {
                        //Log.d("MutableList",it)
                        it
                    }
                }
                if(datas["text"]!=null) {
                    datas["text"] as String
                }else{
                    ""
                }
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
            var verticalAnimateScroll = false

            if(textPart.color!=null){
                color = textPart.color.color
            }

            textPart.props.forEach { (key, value) ->
                //Log.d("PROPS","$key => $value")
                when(key){
                    "fillMaxWidth" -> partModifier = if(value == true){
                            partModifier.fillMaxWidth()
                        }else{
                            partModifier.fillMaxWidth((value as Double).toFloat())
                        }
                    "fillMaxHeight" -> partModifier = if(value == true){
                        partModifier.fillMaxHeight()
                    }else{
                        partModifier.fillMaxHeight((value as Double).toFloat())
                    }
                    "padding" -> {
                        val padding = value as Map<String,Double>
                        padding["start"]?.let {
                            partModifier = partModifier.padding(start = it.dp)
                        }
                        padding["end"]?.let {
                            partModifier = partModifier.padding(end = it.dp)
                        }
                        padding["top"]?.let {
                            partModifier = partModifier.padding(top = it.dp)
                        }
                        padding["bottom"]?.let {
                            partModifier = partModifier.padding(bottom = it.dp)
                        }
                    }
                    "hidden" -> if(!setHidden){
                            hidden = value as Boolean
                        }
                    "background" -> partModifier =
                        partModifier.background(value.toString().color)
                    "clip" -> {
                        val clip = value as Map<String,*>
                        when("${clip["type"]}"){
                            "ROUND" -> {
                                var topEnd = 0.dp
                                var topStart = 0.dp
                                var bottomStart = 0.dp
                                var bottomEnd = 0.dp
                                if(clip["topEnd"]!=null){
                                    topEnd = (clip["topEnd"] as Double).dp
                                }
                                if(clip["topStart"]!=null){
                                    topStart = (clip["topStart"] as Double).dp
                                }
                                if(clip["bottomStart"]!=null){
                                    bottomStart = (clip["bottomStart"] as Double).dp
                                }
                                if(clip["bottomEnd"]!=null){
                                    bottomEnd = (clip["bottomEnd"] as Double).dp
                                }
                                partModifier =
                                    partModifier.clip(RoundedCornerShape(topEnd = topEnd, topStart = topStart, bottomStart = bottomStart, bottomEnd = bottomEnd))
                            }
                            "PARALLELOGRAM" -> {
                                val cornerSize = if (clip["offset"] != null) {
                                    (clip["offset"] as Double).toFloat()
                                } else {
                                    0.toFloat()
                                }
                                val rightOffset = if (clip["rightOffset"] != null) {
                                    (clip["rightOffset"] as Double).toFloat()
                                } else {
                                    0.toFloat()
                                }
                                val leftOffset = if (clip["leftOffset"] != null) {
                                    (clip["leftOffset"] as Double).toFloat()
                                } else {
                                    0.toFloat()
                                }
                                partModifier =
                                    partModifier.clip(Parallelogram(cornerSize,leftOffset,rightOffset))
                            }
                        }
                    }
                    "basicMarquee" -> {
                        partModifier =
                            partModifier.basicMarquee(iterations = Int.MAX_VALUE)
                    }
                    "animated" -> {
                        val animated = value as Map<String,String>
                        //Log.d("PADDING","${animated["type"]}")
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
                            "vertical_scroll" -> {
                                verticalAnimateScroll = true
                            }
                        }
                    }
                }
            }

            if(!hidden) {
                if (verticalAnimateScroll && texts.isNotEmpty()) {
                    AutoScrollingLazyRow(list = texts, modifier = partModifier) {
                        Text(
                            maxLines = textPart.maxLines,
                            text = it,
                            color = color,
                            modifier = partModifier,
                            textAlign = textAlign,
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            fontFamily = fontfamily
                        )
                    }

                } else {
                    Text(
                        maxLines = textPart.maxLines,
                        text = text,
                        color = color,
                        modifier = partModifier,
                        textAlign = textAlign,
                        fontSize = fontSize,
                        fontWeight = fontWeight,
                        fontFamily = fontfamily
                    )
                }
            }
        }

        Type.ANIMATED_VISIBILITY -> {
            //val animVisibleState = remember { MutableTransitionState(false) }.apply { targetState = true }

            var visible by remember { mutableStateOf(false) }
            var prevState by remember { mutableStateOf(
                EnterExitState.PostExit) }
            val animated = listItems as AnimatedVisibility
            val items = animated.listItems
            var hidden by remember {mutableStateOf(false)}

            LaunchedEffect(Unit) {
                while(true) {
                    visible = !visible
                    val map = mapOf<String,Any>(
                        "name" to animated.name,
                        "visible" to visible)
                    if(visible) {
                        delay(animated.enterDelay)
                    }else{
                        delay(animated.exitDelay)
                    }
                    event(map)
                }
            }
            var enterAnimation = EnterTransition.None
            var exitAnimation = ExitTransition.None
            if(animated.animationType.isNotEmpty()){
                animated.animationType.forEach {
                    enterAnimation += when(it){
                        "SLIDE_RIGHT" -> slideInHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth ->
                            // Offsets the content by 1/3 of its width to the left, and slide towards right
                            // Overwrites the default animation with tween for this slide animation.
                            -fullWidth
                        }
                        "SLIDE_LEFT" -> slideInHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth ->
                            // Offsets the content by 1/3 of its width to the left, and slide towards right
                            // Overwrites the default animation with tween for this slide animation.
                            +fullWidth
                        }
                        "SLIDE_UP" -> slideInVertically(animationSpec = tween(durationMillis = 200)) { fullHeight ->
                            // Offsets the content by 1/3 of its width to the left, and slide towards right
                            // Overwrites the default animation with tween for this slide animation.
                            +fullHeight
                        }
                        "FADE" -> fadeIn(
                            // Overwrites the default animation with tween
                            animationSpec = tween(durationMillis = 200)
                        )
                        else -> slideInHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth ->
                            // Offsets the content by 1/3 of its width to the left, and slide towards right
                            // Overwrites the default animation with tween for this slide animation.
                            -fullWidth
                        }
                    }

                    exitAnimation += when(it){
                        "SLIDE_RIGHT" -> slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { fullWidth->
                            // Overwrites the ending position of the slide-out to 200 (pixels) to the right
                            -fullWidth
                        }
                        "SLIDE_LEFT" -> slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { fullWidth->
                            // Overwrites the ending position of the slide-out to 200 (pixels) to the right
                            +fullWidth
                        }
                        "SLIDE_UP" -> slideOutVertically(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { fullHeight->
                            // Overwrites the ending position of the slide-out to 200 (pixels) to the right
                            +fullHeight
                        }
                        "FADE" -> fadeOut()
                        else -> slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { fullWidth->
                            // Overwrites the ending position of the slide-out to 200 (pixels) to the right
                            -fullWidth
                        }
                    }
                }
            }

            //var hidden = false
            if(data[animated.name]!=null){
                val animateData = data[animated.name] as Map<*, *>
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            //Log.d("IMAGE","${animated.name} hidden $hidden")

            if(!hidden) {
                AnimatedVisibility(
                    modifier = modifier,
                    visible = visible,
                    enter = enterAnimation,
                    exit = exitAnimation
                ) {
                    // Content that needs to appear/disappear goes here:

                    //Log.d("EVENT","${animated.name} ${transition.currentState} ${transition.targetState}")

                    val map = mapOf<String, Any>(
                        "name" to animated.name,
                        "currentState" to transition.currentState.name,
                        "targetState" to transition.targetState.name
                    )

                    if (prevState != transition.targetState) {
                        prevState = transition.targetState

                    }

                    items?.let {
                        for (item in items) {
                            item.props.forEach { (key, value) ->
                                //Log.d("PROPS", "$key => $value")
                            }
                            ConstructPart(item, modifier = modifier, data)
                        }
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
            var hidden = false
            if(data[imagePart.name]!=null){
                val animateData = data[imagePart.name] as Map<*, *>
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            if(!hidden) {
                Image(image, "", modifier = partModifier, contentScale = contentScale)
            }
        }

        Type.VIDEO -> {
            val videoPart = listItems as Video

            val uris = remember { mutableStateListOf<String>() }
            val httpHeaders =  remember { mutableStateListOf<Map<String, String>>() }

            //val image: Painter = painterResource(id = R.drawable.temp_masjid_img)

            /*
            var partModifier = modifier
            var contentScale = ContentScale.Fit
            val imageAlign = when (videoPart.imageAlign) {
                Align.START -> Alignment.TopStart
                Align.END -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            if(videoPart.props["contentScale"] == "FillWidth"){

                contentScale = ContentScale.FillWidth
            }
            if(videoPart.props["fillMaxWidth"] == true){
                partModifier = partModifier.fillMaxWidth()
            }

             */

            var start = 0
            //LaunchedEffect(videoPart.url) {
                videoPart.url?.let{
                    //Log.d("VIDEO_URL", it)
                    if(uris.isEmpty()){
                        uris.add(it)
                    }else{
                        uris[0] = it
                    }
                    start = 1
                    (videoPart.headers?:mapOf()).run {
                        if (httpHeaders.isEmpty()) {
                            httpHeaders.add(this)
                        } else {
                            httpHeaders[0] = this
                        }
                    }
                }
            //}

            if(data[videoPart.name]!=null){
                val map = data[videoPart.name] as Map<*, *>

                var index = start
                for(url in map["url"].toListOrEmpty<String>()){
                    val mapHeaders = map["headers"].toMapOrEmpty<String,String>()

                    //Log.d("VIDEO_URL", "$index")
                    with(mapHeaders){
                        if(index < uris.size){
                            httpHeaders[index] = this
                            uris[index] = url
                        }else{
                            uris.add(url)
                            httpHeaders.add(this)
                        }
                    }
                    index = index.inc()
                }
                //uris.addAll(map["url"] as List<String>)
                //map["url"] as List<String>
            }

            //Log.d("VIDEO_URL", "${uris.size}")

            var hidden by remember {mutableStateOf(false)}
            if(data[videoPart.name]!=null){
                val animateData = data[videoPart.name] as Map<*, *>
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            if(!hidden && uris.isNotEmpty()) {
                VideoPlayer(uris,httpHeaders){state,track->
                    val map = mapOf(
                        "name" to videoPart.name,
                        "state" to state,
                        "track" to track.toString())
                    //Log.d("event","$state")
                    event(map)
                }
            }

            //Image(image , "", modifier = partModifier, contentScale = contentScale)
        }

        Type.GLIDE_IMAGE -> {
            var contentScale = ContentScale.Fit
            val imagePart = listItems as GlideImagePart
            var imageUrl by remember {mutableStateOf("")}
            val headersHttp = Headers.Builder()
            val imageAlign = when (imagePart.imageAlign) {
                Align.START -> Alignment.TopStart
                Align.END -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            var partModifier = modifier
            //var image: Painter = painterResource(id = R.drawable.temp_masjid_img)

            contentScale = when(imagePart.props["contentScale"]){
                "FillWidth" -> ContentScale.FillWidth
                "FillHeight" -> ContentScale.FillHeight
                "Fit" -> ContentScale.Fit
                "Inside" -> ContentScale.Inside
                else -> ContentScale.None
            }
            if(imagePart.props["fillMaxWidth"] != null){
                val value = imagePart.props["fillMaxWidth"]
                partModifier = if(value == true){
                    partModifier.fillMaxWidth()
                }else{
                    partModifier.fillMaxWidth((value as Double).toFloat())
                }
            }
            if(imagePart.props["fillMaxHeight"] != null){
                val value = imagePart.props["fillMaxHeight"]
                partModifier = if(value == true){
                    partModifier.fillMaxHeight()
                }else{
                    partModifier.fillMaxHeight((value as Double).toFloat())
                }
            }
            if(imagePart.props["url"] != null){
                imageUrl = imagePart.props["url"].toString()
            }

            if(imagePart.props["swing"]!=null){
                val angleOffset = 10f
                val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition swing")
                val angle by infiniteTransition.animateFloat(
                    initialValue = -angleOffset,
                    targetValue = angleOffset,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 4000 + (Math.random() * 100).toInt(),
                            easing = FastOutSlowInEasing
                        ),
                        repeatMode = RepeatMode.Reverse,
                    ), label = "animate float swing"
                )

                partModifier = partModifier.graphicsLayer(
                    transformOrigin = TransformOrigin(
                        pivotFractionX = 0.5f,
                        pivotFractionY = 0f,
                    ),
                    rotationZ = angle,
                )
            }

            if(imagePart.props["width"]!=null){
                partModifier = partModifier.width(width = (imagePart.props["width"] as Double).dp)
            }

            var hidden by remember {mutableStateOf(false)}
            if(data[imagePart.name]!=null ){
                val animateData = data[imagePart.name] as SnapshotStateMap<*, *>
                imageUrl = animateData["url"].toString()
                if (animateData["headers"] != null) {
                    val headers = animateData["headers"] as Map<String, String>
                    //token = headers["Authorization"] as String

                    headers.forEach { (key, value) ->
                        headersHttp.add(key,value)
                    }
                }
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            if(!hidden) {
                if (imagePart.glideUrl != null && data[imagePart.name] != null) {
                    val builder = LazyHeaders.Builder()

                    val values = data[imagePart.name] as Map<String, Any?>
                    val url = imagePart.glideUrl
                    if (values["headers"] != null) {
                        val headers = values["headers"] as Map<String, String>
                        headers.forEach { (key, value) ->
                            builder.addHeader(key, value)
                        }
                    }

                    val glideUrl = GlideUrl(
                        url,
                        builder.build()
                    )

                    GlideImage(
                        model = glideUrl,
                        modifier = partModifier,
                        contentDescription = "", contentScale = contentScale
                    ) {
                        it.timeout(60000)
                    }
                } else {

                    val context = LocalContext.current
                    Log.d("GLIDE_URL",imageUrl)
                    Image(
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(imageUrl)
                                .headers(headersHttp.build())
                                .diskCacheKey(imageUrl)
                                .memoryCacheKey(imageUrl)
                                .build()
                        ),
                        null,modifier = partModifier, contentScale = contentScale
                    )
                    //Image(image, "", modifier = partModifier, contentScale = contentScale)
                }
            }
            //Image(image , "", modifier = partModifier, contentScale = contentScale)
        }

        Type.BOX -> {
            val box = listItems as Box
            val items = box.listItems
            var partModifier = modifier.collectBoxProps(box.props)

            var hidden by remember {mutableStateOf(false)}

            if(box.props["hidden"]!=null){
                hidden = box.props["hidden"] as Boolean
            }
            LaunchedEffect(Unit){
                if(data[box.name]!=null){
                    //    Log.d("BOX","box.name")
                    val datas = data[box.name] as Map<*,*>
                    datas["props"]?.let { props ->
                        partModifier = modifier.collectBoxProps(props)
                    }
                    if(datas["hidden"]!=null) {
                        hidden = datas["hidden"] as Boolean
                    }
                }
            }

            val contentAlignment: Alignment = if(box.contentAlignment!=null) {
                when (box.contentAlignment) {
                    "CENTER" -> Alignment.Center
                    "TOP_START" -> Alignment.TopStart
                    "TOP_END" -> Alignment.TopEnd
                    "BOTTOM_START" -> Alignment.BottomStart
                    "BOTTOM_END" -> Alignment.BottomEnd
                    else -> Alignment.TopStart
                }
            }else{
                Alignment.TopStart
            }

            //partModifier = content(partModifier,box.props)
//            if(data[box.name]!=null){
//                val animateData = data[box.name] as Map<*, *>
//                if(animateData["hidden"]!=null) {
//                    hidden = animateData["hidden"] as Boolean
//                }
//            }

            //Log.d("IMAGE","${animated.name} hidden $hidden")

            if(!hidden) {
                Box(
                    modifier = partModifier, contentAlignment = contentAlignment
                ) {
                    items?.let {
                        for (item in items) {
                            var modifierItem = Modifier.padding(0.dp)
                            item.props = item.props.filter { (key, value) ->
                                //Log.d("PROPS","$key => $value")
                                when (key) {
                                    "align" -> {
                                        modifierItem = modifierItem.align(
                                            alignment = when (value) {
                                                "TOP_START" -> Alignment.TopStart
                                                "TOP_CENTER" -> Alignment.TopCenter
                                                "TOP_END" -> Alignment.TopEnd
                                                "CENTER" -> Alignment.Center
                                                "CENTER_START" -> Alignment.CenterStart
                                                "CENTER_END" -> Alignment.CenterEnd
                                                "BOTTOM_START" -> Alignment.BottomStart
                                                "BOTTOM_CENTER" -> Alignment.BottomCenter
                                                "BOTTOM_END" -> Alignment.BottomEnd
                                                else -> Alignment.TopStart
                                            }
                                        )
                                        false
                                    }

                                    else -> true
                                }
                            }
                            ConstructPart(item, modifier = modifierItem, data, event)
                        }
                    }
                }
            }
        }

        Type.SPACER -> {
            Spacer(modifier = Modifier)
        }

        Type.SHAPE -> {
            val shape = listItems as ShapePart
            var partModifier = modifier
            val items = shape.listItems

            //val width = shape.props["width"] as Double
            //val height = shape.props["height"] as Double
            //val background = if(shape.props["background"]!=null){
            //    shape.props["background"].toString().color
            //}else{
            //    "#bbbbbb00".color
            //}
            var hidden by remember {mutableStateOf(false)}
            if(data[shape.name]!=null ){
                val shapeData = data[shape.name] as SnapshotStateMap<*, *>
                if(shapeData["hidden"]!=null) {
                    hidden = shapeData["hidden"] as Boolean
                }
            }
            partModifier = partModifier
                //.size(Size(width = width ,height = height))
                //.height(height.dp)
                .clip(
                    when (shape.shapeType) {
                        "ROUND" -> {
                            val cornerSize = if (shape.props["cornerSize"] != null) {
                                (shape.props["cornerSize"] as Double).dp
                            } else {
                                10.dp
                            }
                            RoundedCornerShape(cornerSize)
                        }

                        "PARALLELOGRAM" -> {
                            val cornerSize = if (shape.props["offset"] != null) {
                                (shape.props["offset"] as Double).toFloat()
                            } else {
                                0.toFloat()
                            }
                            val rightOffset = if (shape.props["rightOffset"] != null) {
                                (shape.props["rightOffset"] as Double).toFloat()
                            } else {
                                0.toFloat()
                            }
                            val leftOffset = if (shape.props["leftOffset"] != null) {
                                (shape.props["leftOffset"] as Double).toFloat()
                            } else {
                                0.toFloat()
                            }
                            Parallelogram(cornerSize,leftOffset,rightOffset)
                        }

                        "CUSTOM" -> {
                            val start = if (shape.props["start"] != null) {
                                val p = shape.props["start"] as Map<*,*>
                                PointF((p["x"] as Double).toFloat(),(p["y"] as Double).toFloat())
                            } else {
                                PointF(0f,0f)
                            }
                            var points = mutableListOf<PointF>()
                            if (shape.props["points"] != null) {
                                val listPoints = shape.props["points"] as List<*>
                                listPoints.forEach {
                                    val p = it as Map<*,*>
                                    val point = PointF((p["x"] as Double).toFloat(),(p["y"] as Double).toFloat())
                                    points.add(point)
                                }
                            }
                            CustomShape(start, points)
                        }

                        else -> RoundedCornerShape(10.dp)
                    }
                )
                //.background(background)
                //.fillMaxWidth()
            shape.props.let { props ->
                partModifier = partModifier.collectBoxProps(props)
            }
            if(!hidden) {
                Box(
                    modifier = partModifier
                ) {
                    items.let {
                        for (item in items) {
                            var modifierItem = Modifier.padding(0.dp)
                            item.props = item.props.filter { (key, value) ->
                                //Log.d("PROPS","$key => $value")
                                when (key) {
                                    "align" -> {
                                        modifierItem = modifierItem.align(
                                            alignment = when (value) {
                                                "TOP_START" -> Alignment.TopStart
                                                "TOP_CENTER" -> Alignment.TopCenter
                                                "TOP_END" -> Alignment.TopEnd
                                                "CENTER" -> Alignment.Center
                                                "CENTER_START" -> Alignment.CenterStart
                                                "CENTER_END" -> Alignment.CenterEnd
                                                "BOTTOM_START" -> Alignment.BottomStart
                                                "BOTTOM_CENTER" -> Alignment.BottomCenter
                                                "BOTTOM_END" -> Alignment.BottomEnd
                                                else -> Alignment.TopStart
                                            }
                                        )
                                        false
                                    }

                                    else -> true
                                }
                            }
                            ConstructPart(item, modifier = modifierItem, data, event)
                        }
                    }
                }
            }
        }

        Type.COLUMN -> {
            val column = listItems as Column
            var partModifier = modifier
            val items = column.listItems
            val horizontalAlignment = when(column.horizontalAlignment){
                "START" -> Alignment.Start
                "END" -> Alignment.End
                else -> Alignment.CenterHorizontally
            }

            column.props.forEach { (key, value) ->
                //Log.d("PROPS","$key => $value")
                when(key){
                    "height" -> partModifier = partModifier.height((value as Double).dp)
                    "background" -> partModifier = partModifier.background(value.toString().color)
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                    "gradientBackground" -> {
                        val background = value as Map<*,*>
                        val angle = background["angle"] as Double
                        val listColors = (background["colors"] as List<*>).map {
                            it.toString().color
                        }
                        partModifier = partModifier.gradientBackground(listColors, angle = angle.toFloat())
                    }
                }
            }

            Column(
                horizontalAlignment = horizontalAlignment,
                modifier = partModifier
            ) {
                for (item in items) {
                    var modifierItem = createModifier(listItems = item)
                    item.props = item.props.filter { (key, value) ->
                        //Log.d("COLUMN_PROPS","$key => $value")
                        when(key){
                            "align" ->  {
                                modifierItem = modifierItem.align(alignment = when(value) {
                                    "START" -> Alignment.Start
                                    "END" -> Alignment.End
                                    else -> Alignment.Start
                                })
                                false
                            }
                            "background" -> {
                                modifierItem = modifierItem.background(value.toString().color)
                                false
                            }
                            "weight" -> {
                                modifierItem = modifierItem.weight((value as Double).toFloat())
                                false
                            }
                            "fillWeight" -> {
                                modifierItem = modifierItem.weight((value as Double).toFloat(),fill = true)
                                false
                            }
                            else -> true
                        }
                    }
                    ConstructPart(item, modifier = modifierItem,data,event)
                }
            }
        }

        Type.ROW -> {
            val row = listItems as Row
            val items = row.listItems
            var partModifier = modifier


            row.props.forEach { (key, value) ->
                //Log.d("PROPS","$key => $value")
                when(key){
                    "height" -> partModifier = partModifier.height((value as Double).dp)
                    "background" -> partModifier = partModifier.background(value.toString().color)
                    "gradientBackground" -> {
                        val background = value as Map<*,*>
                        val angle = background["angle"] as Double
                        val listColors = (background["colors"] as List<*>).map {
                            it.toString().color
                        }
                        partModifier = partModifier.gradientBackground(listColors, angle = angle.toFloat())
                    }
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                    "intrinsicSizeMax" -> partModifier = partModifier.height(IntrinsicSize.Max)

                    "padding" -> {
                        val padding = value as Map<String,Double>
                        padding["start"]?.let {
                            partModifier = partModifier.padding(start = it.dp)
                        }
                        padding["end"]?.let {
                            partModifier = partModifier.padding(end = it.dp)
                        }
                        padding["top"]?.let {
                            partModifier = partModifier.padding(top = it.dp)
                        }
                        padding["bottom"]?.let {
                            partModifier = partModifier.padding(bottom = it.dp)
                        }
                    }
                }
            }

            val verticalAlignment = when(row.verticalAlignment){
                "TOP" -> Alignment.Top
                "BOTTOM" -> Alignment.Bottom
                else -> Alignment.CenterVertically
            }

            Row(
                verticalAlignment = verticalAlignment,
                modifier = partModifier
            ) {
                for (item in items) {
//                    var dt by remember {
//                        mutableStateOf(mapOf<String,Any>())
//                    }
//
//                    LaunchedEffect(Unit){
//                        Log.d("props","${data[item.name]}")
//                        if(data[item.name] !=null) {
//                            dt = data[item.name] as Map<String, Any>
//                        }
//                    }
//                    var props:Map<String,Any> = mapOf()
//                    if(dt["props"]!=null) {
//                        props = dt["props"] as Map<String,Any>
//                    }
                    var modifierItem = createModifier(item)
                    collectRowScopeProps(modifierItem,item.props){ mod,props->
                        modifierItem = mod
                        item.props = props
                    }
                    /*
                    item.props = item.props.filter { (key, value) ->
                        //Log.d("Row_child","$key => $value")
                        when(key){
                            "weight" -> {
                                modifierItem = modifierItem.weight((value as Double).toFloat())
                                false
                            }
                            "fillWeight" -> {
                                modifierItem = modifierItem.weight((value as Double).toFloat(),fill = true)
                                false
                            }
                            "padding" -> {
                                val padding = value as Map<String,Double>
                                padding["start"]?.let {
                                    modifierItem = modifierItem.padding(start = it.dp)
                                }
                                padding["end"]?.let {
                                    modifierItem = modifierItem.padding(end = it.dp)
                                }
                                false
                            }
                            "background" -> {
                                modifierItem = modifierItem.background(value.toString().color)
                                false
                            }
                            else -> true
                        }
                    }

                     */
                    ConstructPart(item, modifierItem,data,event)
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
                //Log.d("CARD_PROPS","$key => $value")
                when(key){
                    "height" -> partModifier = partModifier.height((value as Double).dp)
                    "background" -> partModifier = partModifier.background(value.toString().color)
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                }
            }
            var hidden by remember {
                mutableStateOf(false)
            }

            //LaunchedEffect(Unit){
                if(data[card.name]!=null){
                    val dt = data[card.name] as Map<*, *>
                    //Log.d("CARD_PROPS","hidden => ${dt["hidden"]}")
                    if(dt["hidden"]!=null) {
                        hidden = dt["hidden"] as Boolean
                    }
                }
            //}

            if(!hidden) {
                //Log.d("CARD","${card.name} hidden $hidden")
                Card(
                    modifier = partModifier,
                    elevation = elevation,
                    backgroundColor = color
                ) {
                    for (item in items) {
                        var modifierItem = Modifier.background("#00FFFFFF".color)
                        item.props = item.props.filter { (key, value) ->
                            //Log.d("CARD_PROPS","$key => $value")
                            when (key) {
                                "background" -> {
                                    modifierItem =
                                        modifierItem.background(value.toString().color)
                                    false
                                }

                                else -> true
                            }
                        }
                        ConstructPart(item, modifierItem, data)
                    }
                }
            }
        }
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(uris: List<String>, headers:List<Map<String,String>>, listener: (Int,String?) -> Unit = { _, _ ->}) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {

                val defaultDataSourceFactory = DefaultHttpDataSource.Factory()
                //val defaultDataSourceFactory = DefaultDataSource.Factory(context)

                // val progressiveMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)

                uris.forEachIndexed { index, it ->
                    val uri = if(it.startsWith("http")){
                        Uri.parse(it)
                    }else{
                        //Log.d("File","${File(it).exists()}")
                        Uri.fromFile(File(it))
                    }
                    defaultDataSourceFactory.setDefaultRequestProperties(headers[index])

                    if(headers[index]["volume"] != null){
                        volume = headers[index]["volume"]?.toFloat() ?: 0f
                    }else {
                        volume = 0f
                    }
                   //Log.d("lastPathSegment","${uri.lastPathSegment}")
                    val source = if(uri.lastPathSegment?.endsWith("m3u8")==true){
                        // Create a data source factory.
                        //val dataSourceFactory = DefaultHttpDataSource.Factory()

                        HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
                    }else if(uri.scheme?.startsWith("http")==true){
                        val cacheDataSourceFactory = CacheDataSource.Factory()
                        Log.d("VIDEO_URI","$uri")

                        cacheDataSourceFactory.setCache(SimpleCacheBuilder.build(context))
                        cacheDataSourceFactory.setUpstreamDataSourceFactory(defaultDataSourceFactory)
                        cacheDataSourceFactory.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                        ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(uri))
                    }else{
                        ProgressiveMediaSource.Factory(FileDataSource.Factory()).createMediaSource(MediaItem.fromUri(uri))
                    }
                    //val source = DefaultMediaSourceFactory(cacheDataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
                    addMediaSource(source)
                }
                prepare()
            }
    }

    exoPlayer.playWhenReady = true
    exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    exoPlayer.repeatMode = Player.REPEAT_MODE_ALL

    DisposableEffect(
        AndroidView(factory = {
            PlayerView(context).apply {
                hideController()
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        })
    ) {
        val playerListener = object: Player.Listener {

            override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                //Log.d("mediaMetadata","${mediaMetadata.mediaUri}")
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                //listener(playbackState)
                listener(0,mediaItem?.localConfiguration?.uri?.lastPathSegment)
                //Log.d("MediaItem","${mediaItem?.localConfiguration?.uri}")
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                //Log.d("playbackState","$playbackState")
                listener(playbackState,null)
            }

            override fun onPlayerError(error: PlaybackException) {
                listener(99,error.errorCodeName)
            }
        }
        exoPlayer.addListener(playerListener)
        onDispose {
            exoPlayer.removeListener(playerListener)
            exoPlayer.release()
        }
    }
}