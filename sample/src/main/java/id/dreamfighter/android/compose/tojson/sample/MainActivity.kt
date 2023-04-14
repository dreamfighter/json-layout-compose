package id.dreamfighter.android.compose.tojson.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import id.dreamfighter.android.compose.tojson.Payload
import id.dreamfighter.android.compose.tojson.loadPayload
import id.dreamfighter.android.compose.tojson.ui.model.ConstructPart


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadPayload(payloadMajidTv1)?.let{
            setContent {
                Greeting(payload = it)

            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Greeting(payload: Payload) {
    val dynamicListItem = payload.listItems
    val modifier = Modifier
    var data: MutableState<Map<String, Any?>> = mutableStateOf(mutableMapOf("ashr" to "Ashr"))

    for(item in dynamicListItem) {
        ConstructPart(
            item,
            modifier,
            data
        )
    }
}

val payloadMajidTv1 = """
{
   "listItems":[
      {
         "type":"IMAGE",
         "message":"Hello Here",
         "props":{"contentScale":"FillWidth","fillMaxWidth":true},
         "backgroundColor": "GREEN"
      },
      {
        "type":"COLUMN",
        "horizontalAlignment":"START",
        "listItems":[
            {
                "type":"BOX",
                "props":{"fillMaxWidth":true},
                "listItems":[
                    {
                        "type":"ROW",
                        "props":{
                            "height":90,
                            "background":"#55000000",
                            "align":"CENTER",
                            "fillMaxWidth":true
                        },
                        "listItems":[
                            {
                                 "type":"TEXT",
                                 "message":"Sabtu, 11 Mar 2023\n19 Sha'ban 1444",
                                 "textAlign":"START",
                                 "color":"#FFFFFFFF",
                                 "textFont":20,
                                 "props":{
                                    "padding":{"start":16}
                                 }
                            },{
                                 "type":"TEXT",
                                 "message":"Masjid AT-Taqwa Pesona Ciganitri",
                                 "textAlign":"CENTER",
                                 "color":"#FFFFFFFF",
                                 "textFont":30,
                                 "props":{
                                    "padding":{"start":16,"end":16},
                                    "fillWeight":1
                                 }
                            },{
                                 "type":"TEXT",
                                 "message":"11:50:00",
                                 "textAlign":"CENTER",
                                 "color":"#FFFFFFFF",
                                 "textFont":50,
                                 "props":{
                                    "padding":{"end":16}
                                 }
                            }
                        ]
                    }
                ]
            },{
                "type":"BOX",
                "props":{"fillMaxWidth":true, "fillWeight":1}
            },{
                "type":"ANIMATED_VISIBILITY",
                "listItems":[
                    {
                        "type":"TEXT",
                        "message":"Subuh - 02:20:34",
                        "textAlign":"START",
                        "maxLines":1,
                        "color":"#FFFFFFFF",
                        "fontWeight":"BOLD",
                        "textFont":25,
                        "props":{
                            "clip": {"type":"ROUND", "topEnd":24},
                            "background":"#99D85A5C",
                            "align": "START",
                            "padding":{"start":16,"end":16},
                            "animated":{"type":"visibility"}
                        }
                    }
                ]
            },{
                "type":"TEXT",
                "message":"Sabtu, 11 Mar 2023, 19 Sha'ban 1444, Sabtu, 11 Mar 2023, 19 Sha'ban 1444 Sabtu, 11 Mar 2023, 19 Sha'ban 1444 Sabtu, 11 Mar 2023, 19 Sha'ban 1444 Sabtu, 11 Mar 2023, 19 Sha'ban 1444 Sabtu, 11 Mar 2023, 19 Sha'ban 1444",
                "textAlign":"CENTER",
                "maxLines":1,
                "alignment":"CENTER",
                "color":"#FF000000",
                "fontWeight":"BOLD",
                "textFont":20,
                "props":{
                    "background":"#99DDDDDD",
                    "fillMaxWidth":true,
                    "animated":{"type":"scroll"}
                }
            },{
                "type":"BOX",
                "props":{"background":"#99DDDDDD"},
                "listItems":[
                    {
                        "type":"ROW",
                        "props":{
                            "height":90,
                            "align":"CENTER"
                        },
                        "listItems":[
                            {
                                "type":"CARD",
                                "props":{
                                    "weight":1,
                                    "padding":{"start": 8, "end": 8}
                                },
                                "cardBackgroundColor":"#D85A5C",
                                "listItems":[
                                    {
                                        "type":"COLUMN",
                                        "listItems":[
                                            {
                                                "type":"TEXT",
                                                "name":"ashr",
                                                "message":"Fajr",
                                                "color":"#FFFFFFFF",
                                                "textFont":20
                                            },{
                                                "type":"TEXT",
                                                "message":"04:30",
                                                "textFont":30,
                                                "props":{
                                                    "background":"#FFFFFFFF",
                                                    "fillMaxWidth":true
                                                }
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                "type":"CARD",
                                "props":{
                                    "weight":1,
                                    "padding":{"start": 8, "end": 8},
                                    "backgroundColor": "#D85A5C"
                                },
                                "listItems":[
                                    {
                                        "type":"COLUMN",
                                        "listItems":[
                                            {
                                                "type":"TEXT",
                                                "message":"Fajr"
                                            },{
                                                "type":"TEXT",
                                                "message":"04:30"
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
         ]
      }
   ]
}
""".trimIndent()

