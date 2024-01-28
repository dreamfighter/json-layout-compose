package id.dreamfighter.android.compose.tojson.ui.model.utils

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor
import id.dreamfighter.android.compose.tojson.ui.model.parts.ListItems

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
    var partModifier = this
    return partModifier.collectBoxProps(props as Map<String, Any>)
}
fun Modifier.collectBoxProps(
    props: Map<String, Any>
): Modifier {
    var partModifier = this
    props.forEach { map ->
        when(map.key){
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
            "fillMaxWidth" -> partModifier = partModifier.fillMaxWidth()
            "fillMaxHeight" -> partModifier = partModifier.fillMaxHeight()
            "background" -> partModifier = partModifier.background(map.value.toString().color)
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
