package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor
import id.dreamfighter.android.compose.tojson.ui.model.type.Type


class Image(
    val imageAlign: Align = Align.CENTER,
    alignment: Align = Align.CENTER,
    weight: Float = 0f,
    backgroundColor: ItemColor = ItemColor.NONE,
    props:Map<String,Any> = mapOf()
) : ListItems(Type.IMAGE, alignment, weight, backgroundColor, props)