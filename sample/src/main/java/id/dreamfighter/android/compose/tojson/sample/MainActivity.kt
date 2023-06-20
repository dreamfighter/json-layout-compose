package id.dreamfighter.android.compose.tojson.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import id.dreamfighter.android.compose.tojson.Payload
import id.dreamfighter.android.compose.tojson.loadPayload
import id.dreamfighter.android.compose.tojson.ui.model.ConstructPart
import id.dreamfighter.android.compose.tojson.sample.ui.theme.CustomFont
import id.dreamfighter.android.compose.tojson.sample.ui.theme.getCustomFont
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadPayload(payloadMajidTv1)?.let{
            setContent {
                Box(modifier = Modifier.background(color = Color.White)){
                    Greeting(payload = it)
                }

            }
        }
    }

    fun remainingTime(adzan:State<List<Map<String,String>>>,
                      nexPrayer:(String)-> Unit,
                      nexPrayerTime:(String)-> Unit,text:String):String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val now = Calendar.getInstance()
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DATE,1)

        adzan.value.let {

            if(!it[0]["value"].isNullOrBlank() && it[0]["value"]!="null"){

                val imsak = sdf.parse("${sdfDate.format(now.time)} ${it[0]["value"]}")
                val subuh = sdf.parse("${sdfDate.format(now.time)} ${it[1]["value"]}")
                val dzuhur = sdf.parse("${sdfDate.format(now.time)} ${it[4]["value"]}")
                val ashar = sdf.parse("${sdfDate.format(now.time)} ${it[5]["value"]}")
                val maghrib = sdf.parse("${sdfDate.format(now.time)} ${it[6]["value"]}")
                val isya = sdf.parse("${sdfDate.format(now.time)} ${it[7]["value"]}")
                val subuhTomorrow = sdf.parse("${sdfDate.format(tomorrow.time)} ${it[1]["value"]}")

                var jadwal = ""
                val diff = if (now.time.before(imsak)) {
                    jadwal = "Imsak"
                    imsak.time - now.timeInMillis
                } else if (now.time.before(subuh)) {
                    jadwal = "Subuh"
                    subuh.time - now.timeInMillis
                } else if (now.time.before(dzuhur)) {
                    jadwal = "Dzuhur"
                    dzuhur.time - now.timeInMillis
                } else if (now.time.before(ashar)) {
                    jadwal = "Ashar"
                    ashar.time - now.timeInMillis
                } else if (now.time.before(maghrib)) {
                    jadwal = "Maghrib"
                    maghrib.time - now.timeInMillis
                } else if (now.time.before(isya)) {
                    jadwal = "Isya"
                    isya.time - now.timeInMillis
                } else {
                    jadwal = "Subuh"
                    subuhTomorrow.time - now.timeInMillis
                }

                val diffHours = diff / (60 * 60 * 1000)
                val diffMinutes = diff / (60 * 1000) % 60
                val diffSecond = (diff / 1000) % 60
                nexPrayer(jadwal)
                nexPrayerTime("$diffHours hours $diffMinutes minutes ${text}remaining")
                if(diffHours<=0){
                    return "$jadwal - ${"$diffMinutes".lefpad(2,"0")}:${"$diffSecond".lefpad(2,"0")}"
                }
                return "$jadwal - ${"$diffHours".lefpad(2,"0")}:${"$diffMinutes".lefpad(2,"0")}"

            }

        }
        return ""
    }
}

fun String.lefpad(length:Int,additional:String):String{
    return String.format("%1$" + length + "s", this).replace(" ", additional)
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Greeting(payload: Payload) {
    Log.d("PAYLOAD","PAYLOAD")
    val dynamicListItem = payload.listItems
    val modifier = Modifier
    val texts = mapOf<String,Any>("text" to "Ashr", "fontFamily" to CustomFont.getCustomFont())
    val data:SnapshotStateMap<String,Any?> = mutableStateMapOf("ashr" to texts)
    //val data: MutableState<Map<String, Any?>> = mutableStateOf(mutableMapOf("ashr" to "Ashr"))


    val imgList = listOf<String>("https://img.freepik.com/free-vector/islamic-with-mosque-paper-style-design_1017-30710.jpg?w=1380&t=st=1682645647~exp=1682646247~hmac=30d6097c3c7b641df0ba3abcd1c6b2b228f8341ae11e22410c244b9ce4b778d2",
        "https://img.freepik.com/premium-psd/3d-rendering-ramadan-kareem-with-crescent-moon-star_8306-952.jpg?w=1380")
    var i = 0

    data["backgroundImg1"] = mutableStateMapOf("hidden" to true)

    for(item in dynamicListItem) {
        ConstructPart(
            item,
            modifier,
            data
        ){
            Log.d("EVENT","${it["name"]} ${it["targetState"]}")
            if(it["name"] == "backgroundImgAnim" && it["visible"] == false){
                val map = mutableStateMapOf<String, Any?>()
                map["url"] = imgList[i % 2]
                data["backgroundImg"] = map
                i++
            }
        }
    }

    LaunchedEffect(Unit) {
        while(true) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val now = Calendar.getInstance()
            val texts = mapOf<String,Any>("text" to sdf.format(now.time), "fontFamily" to CustomFont)
            data.put("remainingTime",texts)

            //data.setValue("remainingTime",sdf.format(now.time))
            delay(10000)
        }
    }

}


val payloadMajidTv1 = """
{
  "listItems": [
    {
      "type": "ANIMATED_VISIBILITY",
      "name": "backgroundImgAnim",
      "exitDelay": 500,
      "enterDelay": 20000,
      "animationType": [
        "FADE"
      ],
      "listItems": [
        {
          "type": "GLIDE_IMAGE",
          "name": "backgroundImg",
          "message": "Hello Here",
          "props": {
            "contentScale": "FillWidth",
            "fillMaxWidth": true
          },
          "backgroundColor": "GREEN"
        }
      ]
    },
    {
      "type": "VIDEO",
      "name": "backgroundImg1",
      "url": "https://file-examples.com/storage/fe21053bab6446bba9a0947/2017/04/file_example_MP4_640_3MG.mp4",
      "props": {
        "contentScale": "FillWidth",
        "fillMaxWidth": true
      },
      "backgroundColor": "GREEN"
    },
    {
      "type": "COLUMN",
      "horizontalAlignment": "START",
      "listItems": [
        {
          "type": "BOX",
          "props": {
            "fillMaxWidth": true
          },
          "listItems": [
            {
              "type": "ROW",
              "props": {
                "height": 90,
                "background": "#55000000",
                "align": "CENTER",
                "fillMaxWidth": true
              },
              "listItems": [
                {
                  "type": "TEXT",
                  "name": "date",
                  "message": "Sabtu, 11 Mar 2023\n19 Sha'ban 1444",
                  "textAlign": "START",
                  "color": "#FFFFFFFF",
                  "textFont": 20,
                  "props": {
                    "padding": {
                      "start": 16
                    }
                  }
                },
                {
                  "type": "TEXT",
                  "name": "masjidName",
                  "message": "Masjid AT-Taqwa Pesona Ciganitri",
                  "textAlign": "CENTER",
                  "color": "#FFFFFFFF",
                  "textFont": 30,
                  "props": {
                    "padding": {
                      "start": 16,
                      "end": 16
                    },
                    "fillWeight": 1
                  }
                },
                {
                  "type": "TEXT",
                  "name": "currentTime",
                  "message": "11:50:00",
                  "textAlign": "CENTER",
                  "color": "#FFFFFFFF",
                  "textFont": 50,
                  "props": {
                    "padding": {
                      "end": 16
                    }
                  }
                }
              ]
            }
          ]
        },
        {
          "type": "BOX",
          "props": {
            "fillMaxWidth": true,
            "fillWeight": 1
          }
        },
        {
          "type": "ANIMATED_VISIBILITY",
          "animationType": [
            "SLIDE_RIGHT",
            "FADE"
          ],
          "listItems": [
            {
              "type": "TEXT",
              "name": "remainingTime",
              "message": "Subuh - 02:20:34",
              "textAlign": "START",
              "maxLines": 1,
              "color": "#FFFFFFFF",
              "fontWeight": "BOLD",
              "textFont": 25,
              "props": {
                "clip": {
                  "type": "ROUND",
                  "topEnd": 24
                },
                "background": "#99D85A5C",
                "align": "START",
                "padding": {
                  "start": 16,
                  "end": 16
                },
                "animated": {
                  "type": "visibility"
                }
              }
            }
          ]
        },
        {
          "type": "TEXT",
          "name": "info",
          "message": "Sabtu, 11 Mar 2023, 19 Sha'ban 1444, Sabtu, 11 Mar 2023, 19 Sha'ban 1444 Sabtu, 11 Mar 2023, 19 Sha'ban 1444 Sabtu, 11 Mar 2023, 19 Sha'ban 1444 Sabtu, 11 Mar 2023, 19 Sha'ban 1444 Sabtu, 11 Mar 2023, 19 Sha'ban 1444",
          "textAlign": "CENTER",
          "maxLines": 1,
          "alignment": "CENTER",
          "color": "#FF000000",
          "fontWeight": "BOLD",
          "textFont": 20,
          "props": {
            "background": "#99DDDDDD",
            "fillMaxWidth": true,
            "animated": {
              "type": "scroll"
            }
          }
        },
        {
          "type": "BOX",
          "props": {
            "background": "#99DDDDDD"
          },
          "listItems": [
            {
              "type": "ROW",
              "props": {
                "height": 90,
                "align": "CENTER"
              },
              "listItems": [
                {
                  "type": "CARD",
                  "props": {
                    "weight": 1,
                    "padding": {
                      "start": 8,
                      "end": 8
                    }
                  },
                  "cardBackgroundColor": "#D85A5C",
                  "listItems": [
                    {
                      "type": "COLUMN",
                      "listItems": [
                        {
                          "type": "TEXT",
                          "name": "subuh",
                          "message": "Fajr",
                          "color": "#FFFFFFFF",
                          "textFont": 20
                        },
                        {
                          "type": "TEXT",
                          "name": "subuhTime",
                          "message": "04:30",
                          "textFont": 30,
                          "props": {
                            "background": "#FFFFFFFF",
                            "fillMaxWidth": true
                          }
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "CARD",
                  "props": {
                    "weight": 1,
                    "padding": {
                      "start": 8,
                      "end": 8
                    }
                  },
                  "cardBackgroundColor": "#D85A5C",
                  "listItems": [
                    {
                      "type": "COLUMN",
                      "listItems": [
                        {
                          "type": "TEXT",
                          "name": "terbit",
                          "message": "Fajr",
                          "color": "#FFFFFFFF",
                          "textFont": 20
                        },
                        {
                          "type": "TEXT",
                          "name": "terbitTime",
                          "message": "04:30",
                          "textFont": 30,
                          "props": {
                            "background": "#FFFFFFFF",
                            "fillMaxWidth": true
                          }
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "CARD",
                  "props": {
                    "weight": 1,
                    "padding": {
                      "start": 8,
                      "end": 8
                    }
                  },
                  "cardBackgroundColor": "#D85A5C",
                  "listItems": [
                    {
                      "type": "COLUMN",
                      "listItems": [
                        {
                          "type": "TEXT",
                          "name": "dhuha",
                          "message": "Fajr",
                          "color": "#FFFFFFFF",
                          "textFont": 20
                        },
                        {
                          "type": "TEXT",
                          "name": "dhuhaTime",
                          "message": "04:30",
                          "textFont": 30,
                          "props": {
                            "background": "#FFFFFFFF",
                            "fillMaxWidth": true
                          }
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "CARD",
                  "props": {
                    "weight": 1,
                    "padding": {
                      "start": 8,
                      "end": 8
                    }
                  },
                  "cardBackgroundColor": "#D85A5C",
                  "listItems": [
                    {
                      "type": "COLUMN",
                      "listItems": [
                        {
                          "type": "TEXT",
                          "name": "dzuhur",
                          "message": "Fajr",
                          "color": "#FFFFFFFF",
                          "textFont": 20
                        },
                        {
                          "type": "TEXT",
                          "name": "dzuhurTime",
                          "message": "04:30",
                          "textFont": 30,
                          "props": {
                            "background": "#FFFFFFFF",
                            "fillMaxWidth": true
                          }
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "CARD",
                  "props": {
                    "weight": 1,
                    "padding": {
                      "start": 8,
                      "end": 8
                    }
                  },
                  "cardBackgroundColor": "#D85A5C",
                  "listItems": [
                    {
                      "type": "COLUMN",
                      "listItems": [
                        {
                          "type": "TEXT",
                          "name": "ashar",
                          "message": "Fajr",
                          "color": "#FFFFFFFF",
                          "textFont": 20
                        },
                        {
                          "type": "TEXT",
                          "name": "asharTime",
                          "message": "04:30",
                          "textFont": 30,
                          "props": {
                            "background": "#FFFFFFFF",
                            "fillMaxWidth": true
                          }
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "CARD",
                  "props": {
                    "weight": 1,
                    "padding": {
                      "start": 8,
                      "end": 8
                    }
                  },
                  "cardBackgroundColor": "#D85A5C",
                  "listItems": [
                    {
                      "type": "COLUMN",
                      "listItems": [
                        {
                          "type": "TEXT",
                          "name": "maghrib",
                          "message": "Fajr",
                          "color": "#FFFFFFFF",
                          "textFont": 20
                        },
                        {
                          "type": "TEXT",
                          "name": "maghribTime",
                          "message": "04:30",
                          "textFont": 30,
                          "props": {
                            "background": "#FFFFFFFF",
                            "fillMaxWidth": true
                          }
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "CARD",
                  "props": {
                    "weight": 1,
                    "padding": {
                      "start": 8,
                      "end": 8
                    }
                  },
                  "cardBackgroundColor": "#D85A5C",
                  "listItems": [
                    {
                      "type": "COLUMN",
                      "listItems": [
                        {
                          "type": "TEXT",
                          "name": "isya",
                          "message": "Fajr",
                          "color": "#FFFFFFFF",
                          "textFont": 20
                        },
                        {
                          "type": "TEXT",
                          "name": "isyaTime",
                          "message": "04:30",
                          "textFont": 30,
                          "props": {
                            "background": "#FFFFFFFF",
                            "fillMaxWidth": true
                          }
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
    },
    {
      "type": "ANIMATED_VISIBILITY",
      "name":"animatedInfo",
      "enterDelay":10000,
      "exitDelay":60000,
      "animationType": [
        "FADE"
      ],
      "listItems": [
        {
          "type": "BOX",
          "props": {
            "fillMaxWidth": true,
            "fillMaxHeight": true,
            "background": "#000000"
          },
          "contentAlignment":"CENTER",
          "listItems": [
            {
              "type": "TEXT",
              "name": "fullInfo",
              "message":"this is info",
              "textFont": 30,
              "color": "#FFFFFF",
              "textAlign":"CENTER",
              "props": {
                "padding":{"bottom":8},
                "fillMaxHeight": true,
                "background": "#000000"
              }
            }
          ]
        },{
          "type": "BOX",
          "props": {
            "fillMaxWidth": true,
            "fillMaxHeight": true
          },
          "contentAlignment":"BOTTOM_START",
          "listItems": [
            {
              "type": "TEXT",
              "name": "infoLeft",
              "message":"this is info",
              "textFont": 25,
              "color": "#FFFFFF",
              "textAlign":"END",
              "fontFamily":"CustomFont",
              "props": {
                "clip": {
                  "type": "ROUND",
                  "topEnd": 24
                },
                "background": "#99D85A5C",
                "padding":{"start":8, "end":16}
              }
            }
          ]
        },{
          "type": "BOX",
          "props": {
            "fillMaxWidth": true,
            "fillMaxHeight": true
          },
          "contentAlignment":"BOTTOM_END",
          "listItems": [
            {
              "type": "TEXT",
              "name": "infoRight",
              "message":"this is info",
              "textFont": 25,
              "color": "#FFFFFF",
              "textAlign":"END",
              "fontFamily":"CustomFont",
              "props": {
                "clip": {
                  "type": "ROUND",
                  "topStart": 24
                },
                "background": "#99D85A5C",
                "padding":{"start":8, "end":16}
              }
            }
          ]
        }
      ]
    }
  ]
}
""".trimIndent()

