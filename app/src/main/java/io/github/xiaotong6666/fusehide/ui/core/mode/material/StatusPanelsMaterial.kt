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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun StatusChipMaterial(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    metaText: String? = null,
    emphasized: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val containerColor = if (emphasized) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest
    val contentColor = if (emphasized) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    if (onClick != null) {
        ElevatedCard(modifier = modifier.heightIn(min = 118.dp), colors = CardDefaults.elevatedCardColors(containerColor = containerColor, contentColor = contentColor), onClick = onClick, shape = MaterialTheme.shapes.small) {
            StatusChipMaterialContent(label, value, supportingText, metaText, emphasized, contentColor)
        }
    } else {
        ElevatedCard(modifier = modifier.heightIn(min = 118.dp), colors = CardDefaults.elevatedCardColors(containerColor = containerColor, contentColor = contentColor), shape = MaterialTheme.shapes.small) {
            StatusChipMaterialContent(label, value, supportingText, metaText, emphasized, contentColor)
        }
    }
}

@Composable
private fun StatusChipMaterialContent(label: String, value: String, supportingText: String?, metaText: String?, emphasized: Boolean, contentColor: Color) {
    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label.uppercase(Locale.US), style = MaterialTheme.typography.labelSmall, color = if (emphasized) contentColor.copy(alpha = 0.72f) else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = if (emphasized) contentColor else MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (supportingText != null) Text(text = supportingText, style = MaterialTheme.typography.bodySmall, color = if (emphasized) contentColor.copy(alpha = 0.84f) else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        if (metaText != null) Text(text = metaText, style = MaterialTheme.typography.bodySmall, color = if (emphasized) contentColor.copy(alpha = 0.84f) else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun MetricCardMaterial(label: String, value: String, modifier: Modifier = Modifier, valueMaxLines: Int = 2, monospace: Boolean = false) {
    ElevatedCard(modifier = modifier.heightIn(min = 96.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest, contentColor = MaterialTheme.colorScheme.onSurface), shape = MaterialTheme.shapes.small) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = label.uppercase(Locale.US), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default, maxLines = valueMaxLines, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun InfoPanelMaterial(title: String, text: String, monospace: Boolean = false, emphasized: Boolean = false) {
    val containerColor = if (emphasized) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHighest
    val contentColor = if (emphasized) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = containerColor, contentColor = contentColor), shape = MaterialTheme.shapes.small) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (title.isNotEmpty()) Text(title, style = MaterialTheme.typography.labelSmall, color = contentColor.copy(alpha = 0.6f))
            SelectionContainer {
                Text(text = text, modifier = Modifier.fillMaxWidth().wrapContentHeight(), fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun MonospaceBlockMaterial(text: String, modifier: Modifier = Modifier) {
    SelectionContainer { Text(text = text, modifier = modifier.fillMaxWidth().wrapContentHeight(), fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium) }
}

@Composable
fun DeviceStatusListMaterial(infoPairs: List<Pair<String, String>>) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            infoPairs.forEachIndexed { index, pair ->
                val isLast = index == infoPairs.lastIndex
                Text(text = pair.first, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(text = pair.second, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp, bottom = if (isLast) 0.dp else 24.dp))
            }
        }
    }
}

@Composable
fun RuntimeSummaryCardMaterial(summaryText: String, snapshotText: String, emphasized: Boolean) {
    val contentColor = if (emphasized) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
    val cardColors = if (emphasized) CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = contentColor) else CardDefaults.elevatedCardColors(contentColor = contentColor)
    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = cardColors, shape = MaterialTheme.shapes.small) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (summaryText.isNotEmpty()) Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) { Text(text = summaryText, style = MaterialTheme.typography.bodyMedium, color = contentColor) }
            if (summaryText.isNotEmpty() && snapshotText.isNotEmpty()) SettingsGroupDividerMaterial()
            if (snapshotText.isNotEmpty()) {
                SelectionContainer {
                    Box(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Text(text = snapshotText, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium, color = contentColor)
                    }
                }
            }
        }
    }
}
