package id.dreamfighter.android.compose.tojson

import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor
import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import id.dreamfighter.android.compose.tojson.ui.model.parts.*
import java.util.*

fun loadPayload(jsonPayload: String): Payload? {
    val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(ListItems::class.java, "type")
                .withSubtype(Text::class.java, Type.TEXT.name)
                .withSubtype(Button::class.java, Type.BUTTON.name)
                .withSubtype(Image::class.java, Type.IMAGE.name)
                .withSubtype(Video::class.java, Type.VIDEO.name)
                .withSubtype(GlideImagePart::class.java, Type.GLIDE_IMAGE.name)
                .withSubtype(CardPart::class.java, Type.CARD.name)
                .withSubtype(Spacer::class.java, Type.SPACER.name)
                .withSubtype(AnimatedVisibility::class.java, Type.ANIMATED_VISIBILITY.name)
                .withSubtype(
                    Row::class.java,
                    Type.ROW.name
                )
                .withSubtype(
                    Box::class.java,
                    Type.BOX.name
                )
                .withSubtype(
                    Column::class.java,
                    Type.COLUMN.name
                )
                .withDefaultValue(
                    Text(
                        "Oh, what's that?",
                        backgroundColor = ItemColor.RED,
                        name = UUID.randomUUID().toString()
                    )
                )
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    val adapter: JsonAdapter<Payload> = moshi.adapter(Payload::class.java)
    return adapter.fromJson(jsonPayload)
}
