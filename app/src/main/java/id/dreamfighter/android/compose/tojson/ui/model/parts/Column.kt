package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor
import id.dreamfighter.android.compose.tojson.ui.model.type.Type

class Column(
    val listItems: List<ListItems>,
    alignment: Align = Align.CENTER,
    weight: Float = 0f,
    val horizontalAlignment:String?,
    backgroundColor: ItemColor = ItemColor.NONE,
    props:Map<String,Any> = mapOf()
) : ListItems(Type.COLUMN, alignment, weight, backgroundColor,props)