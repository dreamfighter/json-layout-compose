package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor
import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import java.util.UUID

sealed class ListItems(
    val type: Type,
    val alignment: Align = Align.NONE,
    val weight: Float = 0f,
    val backgroundColor: ItemColor = ItemColor.NONE,
    val props:Map<String,Any> = mapOf(),
    val name:String = UUID.randomUUID().toString()
)