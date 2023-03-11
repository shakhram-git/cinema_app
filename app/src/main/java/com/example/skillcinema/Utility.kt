package com.example.skillcinema

import android.content.Context
import android.os.Parcelable
import android.util.DisplayMetrics
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*


fun Context.dpToPixel(dp: Float): Float {
    return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}

@Parcelize
enum class PhotoCategory(val keyString: String) : Parcelable {
    STILL("STILL"),
    SHOOTING("SHOOTING"),
    POSTER("POSTER"),
    FAN_ART("FAN_ART"),
    PROMO("PROMO"),
    CONCEPT("CONCEPT"),
    WALLPAPER("WALLPAPER"),
    COVER("COVER"),
    SCREENSHOT("SCREENSHOT");

    @IgnoredOnParcel
    var amount = 0
}

data class IsCheckedCoverUI<T>(
    val item: T,
    var isChecked: Boolean
)

object DefaultCollections{
    const val WATCHED = 1
    const val FAVOURITE = 2
    const val INTERESTED = 3
    const val DESIRED = 4
}

