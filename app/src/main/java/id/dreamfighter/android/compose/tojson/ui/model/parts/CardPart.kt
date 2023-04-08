package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Type

class CardPart(
    val listItems: List<ListItems>,
    val cardBackgroundColor: String?,
    val elevation: Int?,
    props:Map<String,Any> = mapOf()
) : ListItems(Type.CARD, props = props)