package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import java.util.UUID

class Box(
    val listItems: List<ListItems>?,
    props:Map<String,Any> = mapOf(),
    var contentAlignment: String = "START",
    name:String = UUID.randomUUID().toString()
) : ListItems(Type.BOX, props = props, name = name)