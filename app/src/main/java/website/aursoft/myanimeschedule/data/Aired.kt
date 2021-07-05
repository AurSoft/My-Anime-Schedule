package website.aursoft.myanimeschedule.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Aired(var from: String = "", var to: String? = "") : Parcelable