package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor

class AnimatedVisibility(
    val listItems: List<ListItems>?,
    props:Map<String,Any> = mapOf()
) : ListItems(Type.ANIMATED_VISIBILITY,props = props)