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

package io.github.xiaotong6666.uihelper.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.xiaotong6666.uihelper.components.material.ActionGridMaterial
import io.github.xiaotong6666.uihelper.components.material.AppTextFieldMaterial
import io.github.xiaotong6666.uihelper.components.material.ConfigTextFieldMaterial
import io.github.xiaotong6666.uihelper.components.material.ConfigToggleCardMaterial
import io.github.xiaotong6666.uihelper.components.material.DeviceStatusListMaterial
import io.github.xiaotong6666.uihelper.components.material.DualActionRowMaterial
import io.github.xiaotong6666.uihelper.components.material.InfoBannerMaterial
import io.github.xiaotong6666.uihelper.components.material.InfoPanelMaterial
import io.github.xiaotong6666.uihelper.components.material.InlineTextButtonMaterial
import io.github.xiaotong6666.uihelper.components.material.MetricCardMaterial
import io.github.xiaotong6666.uihelper.components.material.MonospaceBlockMaterial
import io.github.xiaotong6666.uihelper.components.material.PrimaryActionButtonMaterial
import io.github.xiaotong6666.uihelper.components.material.RuntimeSummaryCardMaterial
import io.github.xiaotong6666.uihelper.components.material.SectionCardMaterial
import io.github.xiaotong6666.uihelper.components.material.SectionDescriptionMaterial
import io.github.xiaotong6666.uihelper.components.material.SectionTitleMaterial
import io.github.xiaotong6666.uihelper.components.material.SettingsGroupDividerMaterial
import io.github.xiaotong6666.uihelper.components.material.SettingsGroupHeaderMaterial
import io.github.xiaotong6666.uihelper.components.material.SettingsGroupMaterial
import io.github.xiaotong6666.uihelper.components.material.SettingsInfoItemMaterial
import io.github.xiaotong6666.uihelper.components.material.SettingsToggleItemMaterial
import io.github.xiaotong6666.uihelper.components.material.StatusChipMaterial
import io.github.xiaotong6666.uihelper.components.material.WarningBannerMaterial
import io.github.xiaotong6666.uihelper.components.miuix.ActionGridMiuix
import io.github.xiaotong6666.uihelper.components.miuix.AppTextFieldMiuix
import io.github.xiaotong6666.uihelper.components.miuix.ConfigTextFieldMiuix
import io.github.xiaotong6666.uihelper.components.miuix.ConfigToggleCardMiuix
import io.github.xiaotong6666.uihelper.components.miuix.DeviceStatusListMiuix
import io.github.xiaotong6666.uihelper.components.miuix.DualActionRowMiuix
import io.github.xiaotong6666.uihelper.components.miuix.InfoBannerMiuix
import io.github.xiaotong6666.uihelper.components.miuix.InfoPanelMiuix
import io.github.xiaotong6666.uihelper.components.miuix.InlineTextButtonMiuix
import io.github.xiaotong6666.uihelper.components.miuix.MetricCardMiuix
import io.github.xiaotong6666.uihelper.components.miuix.MonospaceBlockMiuix
import io.github.xiaotong6666.uihelper.components.miuix.PrimaryActionButtonMiuix
import io.github.xiaotong6666.uihelper.components.miuix.RuntimeSummaryCardMiuix
import io.github.xiaotong6666.uihelper.components.miuix.SectionCardMiuix
import io.github.xiaotong6666.uihelper.components.miuix.SectionDescriptionMiuix
import io.github.xiaotong6666.uihelper.components.miuix.SectionTitleMiuix
import io.github.xiaotong6666.uihelper.components.miuix.SettingsGroupDividerMiuix
import io.github.xiaotong6666.uihelper.components.miuix.SettingsGroupHeaderMiuix
import io.github.xiaotong6666.uihelper.components.miuix.SettingsGroupMiuix
import io.github.xiaotong6666.uihelper.components.miuix.SettingsInfoItemMiuix
import io.github.xiaotong6666.uihelper.components.miuix.SettingsToggleItemMiuix
import io.github.xiaotong6666.uihelper.components.miuix.StatusChipMiuix
import io.github.xiaotong6666.uihelper.components.miuix.WarningBannerMiuix
import io.github.xiaotong6666.uihelper.components.model.GridActionItem
import io.github.xiaotong6666.uihelper.components.model.SectionDescriptionStyle
import io.github.xiaotong6666.uihelper.components.model.SectionTitleStyle
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode

@Composable
fun SectionTitle(
    text: String,
    style: SectionTitleStyle = SectionTitleStyle.Large,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SectionTitleMiuix(text, style)
        UiMode.Material -> SectionTitleMaterial(text, style)
    }
}

@Composable
fun SectionDescription(
    text: String,
    style: SectionDescriptionStyle = SectionDescriptionStyle.Body,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SectionDescriptionMiuix(text, style)
        UiMode.Material -> SectionDescriptionMaterial(text, style)
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> AppTextFieldMiuix(value, onValueChange, label, modifier, singleLine)
        UiMode.Material -> AppTextFieldMaterial(value, onValueChange, label, modifier, singleLine)
    }
}

@Composable
fun ConfigTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> ConfigTextFieldMiuix(value, onValueChange, label, modifier, minLines, maxLines)
        UiMode.Material -> ConfigTextFieldMaterial(value, onValueChange, label, modifier, minLines, maxLines)
    }
}

@Composable
fun ConfigToggleCard(
    checked: Boolean,
    title: String,
    description: String,
    onToggle: () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> ConfigToggleCardMiuix(checked, title, description, onToggle)
        UiMode.Material -> ConfigToggleCardMaterial(checked, title, description, onToggle)
    }
}

@Composable
fun InlineTextButton(
    label: String,
    onClick: () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> InlineTextButtonMiuix(label, onClick)
        UiMode.Material -> InlineTextButtonMaterial(label, onClick)
    }
}

@Composable
fun DualActionRow(
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    secondaryLabel: String,
    onSecondaryClick: () -> Unit,
    primaryFilled: Boolean = true,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> DualActionRowMiuix(primaryLabel, onPrimaryClick, secondaryLabel, onSecondaryClick, primaryFilled)
        UiMode.Material -> DualActionRowMaterial(primaryLabel, onPrimaryClick, secondaryLabel, onSecondaryClick, primaryFilled)
    }
}

@Composable
fun PrimaryActionButton(
    label: String,
    onClick: () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> PrimaryActionButtonMiuix(label, onClick)
        UiMode.Material -> PrimaryActionButtonMaterial(label, onClick)
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SettingsGroupMiuix(content)
        UiMode.Material -> SettingsGroupMaterial(content)
    }
}

@Composable
fun SettingsGroupHeader(text: String) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SettingsGroupHeaderMiuix(text)
        UiMode.Material -> SettingsGroupHeaderMaterial(text)
    }
}

@Composable
fun SettingsToggleItem(
    checked: Boolean,
    title: String,
    description: String,
    onToggle: () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SettingsToggleItemMiuix(checked, title, description, onToggle)
        UiMode.Material -> SettingsToggleItemMaterial(checked, title, description, onToggle)
    }
}

@Composable
fun SettingsInfoItem(
    title: String,
    value: String,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SettingsInfoItemMiuix(title, value)
        UiMode.Material -> SettingsInfoItemMaterial(title, value)
    }
}

@Composable
fun SettingsGroupDivider() {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SettingsGroupDividerMiuix()
        UiMode.Material -> SettingsGroupDividerMaterial()
    }
}

@Composable
fun ActionGrid(actions: List<GridActionItem>) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> ActionGridMiuix(actions)
        UiMode.Material -> ActionGridMaterial(actions)
    }
}

@Composable
fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SectionCardMiuix(content)
        UiMode.Material -> SectionCardMaterial(content)
    }
}

@Composable
fun WarningBanner(
    message: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> WarningBannerMiuix(message, modifier, onClick)
        UiMode.Material -> WarningBannerMaterial(message, modifier, onClick)
    }
}

@Composable
fun InfoBanner(
    message: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> InfoBannerMiuix(message, modifier, onClick)
        UiMode.Material -> InfoBannerMaterial(message, modifier, onClick)
    }
}

@Composable
fun StatusChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    metaText: String? = null,
    emphasized: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> StatusChipMiuix(label, value, modifier, supportingText, metaText, emphasized, onClick)
        UiMode.Material -> StatusChipMaterial(label, value, modifier, supportingText, metaText, emphasized, onClick)
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueMaxLines: Int = 2,
    monospace: Boolean = false,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> MetricCardMiuix(label, value, modifier, valueMaxLines, monospace)
        UiMode.Material -> MetricCardMaterial(label, value, modifier, valueMaxLines, monospace)
    }
}

@Composable
fun InfoPanel(
    title: String,
    text: String,
    monospace: Boolean = false,
    emphasized: Boolean = false,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> InfoPanelMiuix(title, text, monospace, emphasized)
        UiMode.Material -> InfoPanelMaterial(title, text, monospace, emphasized)
    }
}

@Composable
fun MonospaceBlock(text: String, modifier: Modifier = Modifier) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> MonospaceBlockMiuix(text, modifier)
        UiMode.Material -> MonospaceBlockMaterial(text, modifier)
    }
}

@Composable
fun DeviceStatusList(infoPairs: List<Pair<String, String>>) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> DeviceStatusListMiuix(infoPairs)
        UiMode.Material -> DeviceStatusListMaterial(infoPairs)
    }
}

@Composable
fun RuntimeSummaryCard(
    summaryText: String,
    snapshotText: String,
    emphasized: Boolean,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> RuntimeSummaryCardMiuix(summaryText, snapshotText, emphasized)
        UiMode.Material -> RuntimeSummaryCardMaterial(summaryText, snapshotText, emphasized)
    }
}
