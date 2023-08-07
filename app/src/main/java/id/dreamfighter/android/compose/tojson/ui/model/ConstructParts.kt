package id.dreamfighter.android.compose.tojson.ui.model

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.media3.common.Player
import androidx.media3.common.TracksInfo
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
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
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.FontSize
import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import id.dreamfighter.android.compose.tojson.ui.model.utils.color
import id.dreamfighter.android.compose.tojson.ui.model.utils.createModifier
import id.dreamfighter.android.compose.tojson.ui.theme.DefaultFont
import kotlinx.coroutines.delay
import okhttp3.Headers
import java.io.File

class SimpleCacheBuilder private constructor() {
    companion object {

        @Volatile
        private var instance: SimpleCache? = null

        @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
        fun build(context:Context) =
            instance ?: synchronized(this) {
                val databaseProvider = StandaloneDatabaseProvider(context)
                val cacheDir = context.externalCacheDir
                Log.d("CACHE_DIR","${cacheDir?.exists()} ${cacheDir?.absolutePath}")
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


@OptIn(ExperimentalAnimationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun ConstructPart(
    listItems: ListItems,
    modifier: Modifier = Modifier,
    data: MutableMap<String,Any?> = mutableMapOf<String,Any?>(),
    event:(Map<String,Any>) -> Unit = {_ ->}
) {
    when (listItems.type) {
        Type.TEXT -> {
            val textPart = listItems as Text
            var partModifier = modifier
            var fontfamily = DefaultFont
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
                datas["text"] as String
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

            textPart.props.forEach { (key, value) ->
                //Log.d("PROPS","$key => $value")
                when(key){
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                    "padding" -> {
                        val padding = value as Map<String,Double>
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
                                var topStart = 0.dp
                                if(clip["topEnd"]!=null){
                                    topEnd = (clip["topEnd"] as Double).dp
                                }
                                if(clip["topStart"]!=null){
                                    topStart = (clip["topStart"] as Double).dp
                                }
                                partModifier =
                                    partModifier.clip(RoundedCornerShape(topEnd = topEnd, topStart = topStart))
                            }
                        }
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
                fontWeight = fontWeight,
                fontFamily = fontfamily
            )

        }

        Type.ANIMATED_VISIBILITY -> {
            val animVisibleState = remember { MutableTransitionState(false) }
                .apply { targetState = true }
            var visible by remember { mutableStateOf(false) }
            var prevState by remember { mutableStateOf(
                EnterExitState.PostExit) }
            val animated = listItems as AnimatedVisibility
            val items = animated.listItems

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
                        "FADE" -> fadeOut()
                        else -> slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { fullWidth->
                            // Overwrites the ending position of the slide-out to 200 (pixels) to the right
                            -fullWidth
                        }
                    }
                }
            }

            var hidden = false
            if(data[animated.name]!=null){
                val animateData = data[animated.name] as Map<*, *>
                if(animateData["hidden"]!=null) {
                    hidden = animateData["hidden"] as Boolean
                }
            }

            //Log.d("IMAGE","${animated.name} hidden $hidden")

            if(!hidden) {
                AnimatedVisibility(
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

            var contentScale = ContentScale.Fit
            val imageAlign = when (videoPart.imageAlign) {
                Align.START -> Alignment.TopStart
                Align.END -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            var httpHeaders = mapOf<String,String>()
            var partModifier = modifier
            val image: Painter = painterResource(id = R.drawable.temp_masjid_img)

            if(videoPart.props["contentScale"] == "FillWidth"){

                contentScale = ContentScale.FillWidth
            }
            if(videoPart.props["fillMaxWidth"] == true){
                partModifier = partModifier.fillMaxWidth()
            }
            val uris = if(data[videoPart.name]!=null){
                val map = data[videoPart.name] as Map<*, *>
                if(map["headers"]!=null) {
                    httpHeaders = map["headers"] as Map<String, String>
                }
                map["url"] as List<String>
            }else if(videoPart.url!=null){
                listOf<String>(videoPart.url!!)
            }else{
                listOf<String>()
            }

            var hidden = false
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
                        "track" to track)
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
            var headersHttp = Headers.Builder()
            val imageAlign = when (imagePart.imageAlign) {
                Align.START -> Alignment.TopStart
                Align.END -> Alignment.BottomEnd
                else -> Alignment.Center
            }
            var partModifier = modifier
            var image: Painter = painterResource(id = R.drawable.temp_masjid_img)

            if(imagePart.props["contentScale"] == "FillWidth"){

                contentScale = ContentScale.FillWidth
            }
            if(imagePart.props["fillMaxWidth"] == true){
                partModifier = partModifier.fillMaxWidth()
            }

            var hidden = false
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
            var partModifier = modifier

            //Log.d("BOX_PROPS","${box.props}")
            box.props.forEach { (key, value) ->
                //Log.d("BOX_PROPS","$key => $value")
                when(key){
                    "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
                    "fillMaxHeight" -> partModifier = partModifier.fillMaxHeight()
                    "background" -> partModifier = partModifier.background(value.toString().color)
                }
            }

            val contentAlignment: Alignment = when(box.contentAlignment){
                "CENTER" -> Alignment.Center
                "TOP_START" -> Alignment.TopStart
                "BOTTOM_START" -> Alignment.BottomStart
                "BOTTOM_END" -> Alignment.BottomEnd
                else -> Alignment.TopStart
            }

            //partModifier = content(partModifier,box.props)

            Box(
                modifier = partModifier, contentAlignment = contentAlignment
            ) {
                items?.let {
                    for (item in items) {
                        val modifier = Modifier
                        item.props.forEach { (key, value) ->
                            //Log.d("PROPS","$key => $value")
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
                        //Log.d("COLUMN_PROPS","$key => $value")
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
                //Log.d("PROPS","$key => $value")
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
                        //Log.d("PROPS","$key => $value")
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
                //Log.d("CARD_PROPS","$key => $value")
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
                        //Log.d("CARD_PROPS","$key => $value")
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



@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(uris: List<String>, headers:Map<String,String>, listener: (Int,String) -> Unit = { _, _ ->}) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {

                val defaultDataSourceFactory = DefaultHttpDataSource.Factory()
                //val defaultDataSourceFactory = DefaultDataSource.Factory(context)
                defaultDataSourceFactory.setDefaultRequestProperties(headers)

                // val progressiveMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)

                uris.forEach {
                    val uri = if(it.startsWith("http")){
                        Uri.parse(it)
                    }else{
                        Log.d("File","${File(it).exists()}")
                        Uri.fromFile(File(it))
                    }
                    val source = if(uri.scheme?.startsWith("http")==true){
                        val cacheDataSourceFactory = CacheDataSource.Factory()

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
    if(headers["volume"] != null){
        exoPlayer.volume = headers["volume"]?.toFloat() ?: 0f
    }else {
        exoPlayer.volume = 0f
    }

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
        val listener = object: Player.Listener {

            override fun onTracksInfoChanged(tracksInfo: TracksInfo) {
                Log.d("tracksInfo","${tracksInfo.trackGroupInfos[1].trackGroup}")
            }

            override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                Log.d("mediaMetadata","${mediaMetadata.mediaUri}")
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                //listener(playbackState)
                listener(0,mediaItem?.localConfiguration?.uri.toString())
                Log.d("MediaItem","${mediaItem?.localConfiguration?.uri}")
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d("playbackState","$playbackState")
                listener(playbackState,"_")
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }
}