package io.github.xiaotong6666.fusehide.ui.navigation3

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize

sealed interface Route :
    NavKey,
    Parcelable {
    @Parcelize
    data object Main : Route

    @Parcelize
    data object GlobalConfig : Route

    @Parcelize
    data class AppConfig(val packageName: String) : Route
}
