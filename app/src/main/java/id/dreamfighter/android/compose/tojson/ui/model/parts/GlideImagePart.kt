package id.dreamfighter.android.compose.tojson.ui.model.parts

import com.bumptech.glide.load.model.GlideUrl
import id.dreamfighter.android.compose.tojson.ui.model.type.Align
import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor
import id.dreamfighter.android.compose.tojson.ui.model.type.Type


class GlideImagePart(
    val imageAlign: Align = Align.CENTER,
    alignment: Align = Align.CENTER,
    weight: Float = 0f,
    backgroundColor: ItemColor = ItemColor.NONE,
    props:Map<String,Any> = mapOf(),
    val glideUrl:GlideUrl?
) : ListItems(Type.GLIDE_IMAGE, alignment, weight, backgroundColor, props)