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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import io.github.xiaotong6666.fusehide.R
import io.github.xiaotong6666.fusehide.config.HideConfigDefaults
import io.github.xiaotong6666.fusehide.config.formatHiddenTargetRules
import io.github.xiaotong6666.fusehide.config.parseHiddenTargetRules

@Composable
fun GlobalConfigPage(
    state: ConfigUiState,
    callbacks: ConfigCallbacks,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    val view = LocalView.current
    val visibleExemptionsText = HideConfigDefaults.toEditorText(state.currentHideConfig.hideAllRootEntriesExemptions)
    var visibleExemptionsDraft by rememberSaveable { mutableStateOf(visibleExemptionsText) }
    val hiddenTargetsText = formatHiddenTargetRules(state.currentHideConfig)
    var hiddenTargetsDraft by rememberSaveable { mutableStateOf(hiddenTargetsText) }
    val hiddenPackagesText = HideConfigDefaults.toEditorText(state.currentHideConfig.hiddenPackages)
    var hiddenPackagesDraft by rememberSaveable { mutableStateOf(hiddenPackagesText) }

    AppConfigPageScaffold(
        title = stringResource(R.string.global_hide_config_title),
        onBack = onBack,
        onSave = onSave,
        overflowActions = listOf(
            ConfigPageOverflowAction(
                label = stringResource(R.string.button_restore_defaults),
                onClick = {
                    visibleExemptionsDraft = HideConfigDefaults.toEditorText(HideConfigDefaults.value.hideAllRootEntriesExemptions)
                    hiddenTargetsDraft = formatHiddenTargetRules(HideConfigDefaults.value)
                    hiddenPackagesDraft = HideConfigDefaults.toEditorText(HideConfigDefaults.value.hiddenPackages)
                    callbacks.onResetConfigClick()
                },
            ),
        ),
    ) { contentPadding, scrollModifier ->
        ConfigDetailPageBody(
            contentPadding = contentPadding,
            scrollModifier = scrollModifier,
        ) {
            InfoBanner(
                message = stringResource(R.string.section_editable_draft_desc),
            )

            AppConfigToggleCard(
                checked = state.currentHideConfig.enableHideAllRootEntries,
                title = stringResource(R.string.field_hide_all_title),
                description = stringResource(R.string.field_hide_all_desc),
                onToggle = {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    callbacks.onConfigUpdate(
                        state.currentHideConfig.copy(enableHideAllRootEntries = !state.currentHideConfig.enableHideAllRootEntries),
                    )
                },
            )

            AppConfigTargetsCard(
                value = visibleExemptionsDraft,
                onValueChange = { newValue ->
                    visibleExemptionsDraft = newValue
                    callbacks.onConfigUpdate(
                        state.currentHideConfig.copy(hideAllRootEntriesExemptions = HideConfigDefaults.parseEditorText(newValue)),
                    )
                },
                label = stringResource(R.string.field_visible_exemptions),
                description = stringResource(R.string.field_visible_exemptions_help),
                minLines = 5,
                maxLines = 5,
            )

            AppConfigTargetsCard(
                value = hiddenTargetsDraft,
                onValueChange = { newValue ->
                    hiddenTargetsDraft = newValue
                    val parsed = parseHiddenTargetRules(newValue)
                    callbacks.onConfigUpdate(
                        state.currentHideConfig.copy(
                            hiddenRootEntryNames = parsed.hiddenRootEntryNames,
                            hiddenRelativePaths = parsed.hiddenRelativePaths,
                            packageRules = parsed.packageRules,
                        ),
                    )
                },
                label = stringResource(R.string.field_hidden_targets),
                description = stringResource(R.string.field_hidden_targets_help),
                minLines = 5,
                maxLines = 5,
            )

            AppConfigTargetsCard(
                value = hiddenPackagesDraft,
                onValueChange = { newValue ->
                    hiddenPackagesDraft = newValue
                    callbacks.onConfigUpdate(
                        state.currentHideConfig.copy(
                            hiddenPackages = HideConfigDefaults.parseEditorText(newValue),
                        ),
                    )
                },
                label = stringResource(R.string.field_hidden_package_names),
                description = stringResource(R.string.field_hidden_package_names_help),
                minLines = 5,
                maxLines = 5,
            )
        }
    }
}
