/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.xiaotong6666.fusehide.ui.app

import android.content.Context
import androidx.core.content.edit
import io.github.xiaotong6666.uihelper.mode.UiMode

object FuseHideUiModeStore {
    private const val PREFS_NAME = "settings"
    private const val KEY_UI_MODE = "ui_mode"

    fun fromPrefs(context: Context): UiMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return UiMode.fromValue(prefs.getString(KEY_UI_MODE, UiMode.Miuix.value) ?: UiMode.Miuix.value)
    }

    fun saveToPrefs(context: Context, mode: UiMode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_UI_MODE, mode.value)
            }
    }
}
