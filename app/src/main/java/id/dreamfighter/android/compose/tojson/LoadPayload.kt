package id.dreamfighter.android.compose.tojson

import id.dreamfighter.android.compose.tojson.ui.model.type.ItemColor
import id.dreamfighter.android.compose.tojson.ui.model.type.Type
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import id.dreamfighter.android.compose.tojson.ui.model.parts.*

fun loadPayload(jsonPayload: String): Payload? {
    val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(ListItems::class.java, "type")
                .withSubtype(Text::class.java, Type.TEXT.name)
                .withSubtype(Button::class.java, Type.BUTTON.name)
                .withSubtype(Image::class.java, Type.IMAGE.name)
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
                        backgroundColor = ItemColor.RED
                    )
                )
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    val adapter: JsonAdapter<Payload> = moshi.adapter(Payload::class.java)
    return adapter.fromJson(jsonPayload)
}
