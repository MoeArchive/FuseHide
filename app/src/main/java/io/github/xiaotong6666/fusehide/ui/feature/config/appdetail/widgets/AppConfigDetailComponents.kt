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

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.fusehide.ui.feature.config.appdetail.widgets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.xiaotong6666.uihelper.chrome.DetailSettingsBody
import io.github.xiaotong6666.uihelper.chrome.DetailSettingsHost
import io.github.xiaotong6666.uihelper.chrome.DetailSettingsOverflowAction
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode

typealias ConfigPageOverflowAction = DetailSettingsOverflowAction

@Composable
fun AppConfigPageScaffold(
    title: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    overflowActions: List<ConfigPageOverflowAction> = emptyList(),
    content: @Composable (PaddingValues, Modifier) -> Unit,
) {
    DetailSettingsHost(title, onBack, onSave, overflowActions, content)
}

@Composable
fun ConfigDetailPageBody(
    contentPadding: PaddingValues,
    scrollModifier: Modifier,
    content: @Composable () -> Unit,
) {
    DetailSettingsBody(contentPadding, scrollModifier, content)
}

@Composable
fun AppConfigInfoCard(
    label: String,
    packageName: String,
    versionName: String,
    versionCode: Long,
    uid: Int,
    modifier: Modifier = Modifier,
    appIcon: (@Composable () -> Unit)? = null,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> AppConfigInfoCardMiuix(label, packageName, versionName, versionCode, uid, modifier, appIcon)
        UiMode.Material -> AppConfigInfoCardMaterial(label, packageName, versionName, versionCode, uid, modifier, appIcon)
    }
}

@Composable
fun AppConfigToggleCard(
    checked: Boolean,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> AppConfigToggleCardMiuix(checked, title, description, modifier, onToggle)
        UiMode.Material -> AppConfigToggleCardMaterial(checked, title, description, modifier, onToggle)
    }
}
