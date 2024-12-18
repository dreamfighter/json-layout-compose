package id.dreamfighter.android.compose.tojson.ui.model.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor
import id.dreamfighter.android.compose.tojson.ui.model.parts.ListItems
import id.dreamfighter.android.compose.tojson.ui.model.shape.Parallelogram
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun RowScope.createModifier(
    listItems: ListItems
): Modifier {

    var modifier = if (listItems.weight > 0) {
        Modifier.weight(listItems.weight)
    } else Modifier

    modifier = commonModifier(modifier, listItems)

    return when (listItems.alignment) {
        Align.START -> modifier.align(Alignment.Top)
        Align.END -> modifier.align(Alignment.Bottom)
        Align.CENTER -> modifier.align(Alignment.CenterVertically)
        Align.FILL -> modifier.fillMaxHeight()
        else -> modifier
    }
}

@Composable
fun RowScope.collectRowScopeProps(
    modifier: Modifier,
    props: Map<String, Any>,
    result:(Modifier,Map<String, Any>) -> Unit = {_,_->}
) {
    var modifierItem = modifier

    val props = props.filter { map ->
        //Log.d("Row_child","$key => $value")
        when(map.key){
            "weight" -> {
                modifierItem = modifierItem.weight((map.value as Double).toFloat())
                false
            }
            "fillWeight" -> {
                modifierItem = modifierItem.weight((map.value as Double).toFloat(),fill = true)
                false
            }
            "padding" -> {
                val padding = map.value as Map<String,Double>
                padding["start"]?.let {
                    modifierItem = modifierItem.padding(start = it.dp)
                }
                padding["end"]?.let {
                    modifierItem = modifierItem.padding(end = it.dp)
                }
                padding["top"]?.let {
                    modifierItem = modifierItem.padding(top = it.dp)
                }
                padding["bottom"]?.let {
                    modifierItem = modifierItem.padding(bottom = it.dp)
                }
                false
            }
            "background" -> {
                modifierItem = modifierItem.background(map.value.toString().color)
                false
            }
            else -> true
        }
    }

    result(modifierItem,props)
}
fun Modifier.collectBoxProps(
    props: Any
): Modifier {
    val partModifier = this
    return partModifier.collectBoxProps(props as Map<String, Any>)
}
fun Modifier.collectBoxProps(
    props: Map<String, Any>
): Modifier {
    var partModifier = this
    props.forEach { map ->
        when(map.key){
            "width" -> {
                partModifier = when(map.value){
                    "intrinsicSizeMax" -> partModifier.width(IntrinsicSize.Max)
                    else -> partModifier.width((map.value as Double).dp)
                }
            }
            "padding" -> {
                val padding = map.value as Map<String,Double>
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
            "animateContentSize" -> partModifier = partModifier.animateContentSize()
            "height" -> partModifier = partModifier.height((map.value as Double).dp)
            "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
            "fillMaxHeight" -> partModifier = partModifier.fillMaxHeight()
            "background" -> partModifier = partModifier.background(map.value.toString().color)
            "gradientBackground" -> {
                val background = map.value as Map<*,*>
                val angle = background["angle"] as Double
                val listColors = (background["colors"] as List<*>).map {
                    it.toString().color
                }
                partModifier = partModifier.gradientBackground(listColors, angle = angle.toFloat())
            }
            "border" ->{
                val border = map.value as Map<*,*>
                var width = border["width"] as Double
                var drawBorder = true

                var defColor = Color.Black

                border["color"]?.let {
                    defColor = it.toString().color
                }

                border["start"]?.let {
                    drawBorder = false
                    width = it as Double
                    partModifier = partModifier.startBorder(width.dp,defColor)
                }
                border["end"]?.let {
                    drawBorder = false
                    width = it as Double
                    partModifier = partModifier.endBorder(width.dp,defColor)
                }
                border["top"]?.let {
                    drawBorder = false
                    width = it as Double
                    partModifier = partModifier.topBorder(width.dp,defColor)
                }
                border["bottom"]?.let {
                    drawBorder = false
                    width = it as Double
                    partModifier = partModifier.bottomBorder(width.dp,defColor)
                }

                if(drawBorder){
                    partModifier = partModifier.border(
                        BorderStroke(
                            width = width.dp,
                            color = defColor
                        )
                    )
                }
            }
            "clip" -> {
                val clip = map.value as Map<String,*>
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
        }
    }
    return partModifier
}

@Composable
fun ColumnScope.createModifier(
    listItems: ListItems
): Modifier {
    var modifier = if (listItems.weight > 0) {
        Modifier.weight(listItems.weight)
    } else Modifier

    modifier = commonModifier(modifier, listItems)
    if(listItems.alignment == null){
        return modifier
    }
    return when (listItems.alignment) {
        Align.START -> modifier.align(Alignment.Start)
        Align.END -> modifier.align(Alignment.End)
        Align.CENTER -> modifier.align(Alignment.CenterHorizontally)
        Align.FILL -> modifier.fillMaxWidth()
        else -> modifier
    }
}

@Composable
private fun commonModifier(
    modifier: Modifier,
    listItems: ListItems
): Modifier {

    if(listItems.backgroundColor == null){
        return modifier
    }
    return when (listItems.backgroundColor) {
        ItemColor.RED -> modifier.background(Color.Red)
        ItemColor.GREEN -> modifier.background(Color.Green)
        ItemColor.BLUE -> modifier.background(Color.Blue)
        else -> modifier
    }
}

val String.color
    get() = Color(android.graphics.Color.parseColor(this))

fun Modifier.gradientBackground(colors: List<Color>, angle: Float) = this.then(
    Modifier.drawBehind {
        val angleRad = angle / 180f * PI
        val x = cos(angleRad).toFloat() //Fractional x
        val y = sin(angleRad).toFloat() //Fractional y

        val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
        val offset = center + Offset(x * radius, y * radius)

        val exactOffset = Offset(
            x = min(offset.x.coerceAtLeast(0f), size.width),
            y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
        )

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(size.width, size.height) - exactOffset,
                end = exactOffset
            ),
            size = size
        )
    }
)

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx/2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width , y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.topBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx/2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = width , y = 0f),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.startBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx/2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = 0f , y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.endBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx/2

            drawLine(
                color = color,
                start = Offset(x = width, y = 0f),
                end = Offset(x = width , y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

inline fun <reified T> Any?.asOrFail(): T = this as T
//inline fun <reified T,reified T> Any.toMapOrFail(): T = this as T
inline fun <reified T: Any,reified R: Any> Any?.toMapOrEmpty(): Map<T,R> = this as? Map<T,R>?: mapOf()
inline fun <reified T> Any?.toListOrEmpty(): List<T> = this as? List<T> ?: listOf()
