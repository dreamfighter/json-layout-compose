package id.dreamfighter.android.compose.tojson.ui.model.parts

import android.graphics.fonts.FontFamily
import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor

class Text(
    val message: String,
    val textAlign: Align = Align.CENTER,
    val textFont: String = "DEFAULT",
    val color: String = "#FF000000",
    val maxLines:Int = 3,
    val fontWeight:String? = "NORMAL",
    val fontFamily: String? = null,
    alignment: Align = Align.CENTER,
    weight: Float = 0f,
    backgroundColor: ItemColor = ItemColor.NONE,
    props:Map<String,Any> = mapOf(),
    name:String = ""
) : ListItems(Type.TEXT, alignment, weight, backgroundColor, props, name)