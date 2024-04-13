package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import java.util.UUID

class ShapePart(
    val listItems: List<ListItems>,
    val shareBackgroundColor: String?,
    props:Map<String,Any> = mapOf(),
    name:String = UUID.randomUUID().toString(),
    val shapeType:String = "ROUND"
) : ListItems(Type.SHAPE, props = props, name = name)