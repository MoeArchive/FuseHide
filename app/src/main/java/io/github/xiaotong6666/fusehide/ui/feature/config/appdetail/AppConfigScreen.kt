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

package io.github.xiaotong6666.fusehide.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.fusehide.R
import io.github.xiaotong6666.fusehide.config.HideConfigDefaults
import io.github.xiaotong6666.fusehide.config.PackageHideRule

@Composable
fun AppConfigPage(
    packageName: String,
    state: ConfigUiState,
    callbacks: ConfigCallbacks,
    appListViewModel: AppListViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    val view = LocalView.current
    val uiState by appListViewModel.uiState.collectAsState()
    val appInfo = uiState.groupedApps.flatMap { it.apps }.find { it.packageName == packageName }

    val isHiddenGlobally = state.currentHideConfig.hiddenPackages.contains(packageName)
    val specificRule = state.currentHideConfig.packageRules.find { it.packageName == packageName }
    val isHiddenSpecifically = specificRule != null
    val isEnabled = isHiddenGlobally || isHiddenSpecifically

    val specificTargetsText = if (specificRule != null) {
        val rootEntries = specificRule.hiddenRootEntryNames
        val relativePaths = specificRule.hiddenRelativePaths.map { "|$it" }
        HideConfigDefaults.toEditorText(rootEntries + relativePaths)
    } else {
        ""
    }
    var specificTargetsDraft by rememberSaveable(packageName) { mutableStateOf(specificTargetsText) }
    val topBarTitle = appInfo?.label ?: packageName

    AppConfigPageScaffold(
        title = topBarTitle,
        onBack = onBack,
        onSave = onSave,
    ) { contentPadding, scrollModifier ->
        ConfigDetailPageBody(
            contentPadding = contentPadding,
            scrollModifier = scrollModifier.fillMaxSize(),
        ) {
            AppConfigInfoCard(
                label = appInfo?.label ?: packageName,
                packageName = appInfo?.packageName ?: packageName,
                versionName = appInfo?.packageInfo?.versionName.orEmpty(),
                versionCode = appInfo?.packageInfo?.longVersionCode ?: 0L,
                uid = appInfo?.uid ?: -1,
                appIcon = appInfo?.let {
                    {
                        AppIconImage(
                            applicationInfo = it.applicationInfo,
                            label = it.label,
                            modifier = Modifier.size(if (LocalUiMode.current == UiMode.Miuix) 64.dp else 48.dp),
                        )
                    }
                },
            )
            AppConfigToggleCard(
                checked = isEnabled,
                title = stringResource(R.string.enable_hide_title),
                description = stringResource(R.string.enable_hide_desc),
                onToggle = {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    updateAppRule(
                        packageName = packageName,
                        enabled = !isEnabled,
                        targetsText = specificTargetsDraft,
                        currentState = state,
                        callbacks = callbacks,
                    )
                },
            )
            AppConfigTargetsCard(
                value = specificTargetsDraft,
                onValueChange = { newText ->
                    specificTargetsDraft = newText
                    updateAppRule(
                        packageName = packageName,
                        enabled = isEnabled,
                        targetsText = newText,
                        currentState = state,
                        callbacks = callbacks,
                    )
                },
                label = stringResource(R.string.subdirectory_title),
                description = stringResource(R.string.subdirectory_desc),
            )
        }
    }
}

private fun updateAppRule(
    packageName: String,
    enabled: Boolean,
    targetsText: String,
    currentState: ConfigUiState,
    callbacks: ConfigCallbacks,
) {
    val currentConfig = currentState.currentHideConfig
    val parsedLines = HideConfigDefaults.parseEditorText(targetsText)

    val newHiddenPackages = currentConfig.hiddenPackages.toMutableList()
    val newPackageRules = currentConfig.packageRules.toMutableList()

    newHiddenPackages.remove(packageName)
    newPackageRules.removeAll { it.packageName == packageName }

    if (enabled) {
        if (parsedLines.isEmpty()) {
            newHiddenPackages.add(packageName)
        } else {
            val relativePaths = parsedLines
                .filter { it.startsWith("|") || it.contains('/') }
                .map { it.removePrefix("|") }
            val rootEntries = parsedLines.filterNot { it.startsWith("|") || it.contains('/') }
            newPackageRules.add(
                PackageHideRule(
                    packageName = packageName,
                    hiddenRootEntryNames = rootEntries,
                    hiddenRelativePaths = relativePaths,
                ),
            )
        }
    }

    callbacks.onConfigUpdate(
        currentConfig.copy(
            hiddenPackages = newHiddenPackages,
            packageRules = newPackageRules,
        ),
    )
}
