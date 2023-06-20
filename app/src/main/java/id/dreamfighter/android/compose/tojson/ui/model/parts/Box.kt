package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Type

class Box(
    val listItems: List<ListItems>?,
    props:Map<String,Any> = mapOf(),
    var contentAlignment: String = "CENTER"
) : ListItems(Type.BOX, props = props)