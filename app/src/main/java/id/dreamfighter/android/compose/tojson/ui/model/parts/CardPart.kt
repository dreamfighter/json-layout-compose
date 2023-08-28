package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import java.util.UUID

class CardPart(
    val listItems: List<ListItems>,
    val cardBackgroundColor: String?,
    val elevation: Int?,
    props:Map<String,Any> = mapOf(),
    name:String = UUID.randomUUID().toString()
) : ListItems(Type.CARD, props = props, name = name)