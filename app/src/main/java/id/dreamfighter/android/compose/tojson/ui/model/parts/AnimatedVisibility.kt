package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor
import java.util.UUID

class AnimatedVisibility(
    val listItems: List<ListItems>?,
    props:Map<String,Any> = mapOf(),
    var animationType:List<String> = listOf<String>(),
    var enterDelay:Long = 10000,
    var exitDelay:Long = 10000,
    name:String = UUID.randomUUID().toString()
) : ListItems(Type.ANIMATED_VISIBILITY,props = props, name = name)