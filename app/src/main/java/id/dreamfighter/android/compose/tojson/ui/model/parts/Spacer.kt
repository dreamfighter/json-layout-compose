package id.dreamfighter.android.compose.tojson.ui.model.parts

import id.dreamfighter.android.compose.tojson.ui.model.type.Type

class Spacer(
    val listItems: List<ListItems>?,
    props:Map<String,Any> = mapOf()
) : ListItems(Type.SPACER, props = props)