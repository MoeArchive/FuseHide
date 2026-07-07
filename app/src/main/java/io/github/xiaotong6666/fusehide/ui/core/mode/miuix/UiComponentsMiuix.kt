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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.xiaotong6666.fusehide.R
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.basic.Search
import top.yukonga.miuix.kmp.icon.basic.SearchCleanup
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import java.util.Locale

@Composable
fun SectionTitleMiuix(
    text: String,
    style: SectionTitleStyle,
) {
    Text(
        text = text,
        style = when (style) {
            SectionTitleStyle.Large -> MiuixTheme.textStyles.title3.copy(fontWeight = FontWeight.Medium)
            SectionTitleStyle.Medium -> MiuixTheme.textStyles.title4
            SectionTitleStyle.EmphasizedMedium -> MiuixTheme.textStyles.title4.copy(fontWeight = FontWeight.Medium)
            SectionTitleStyle.Small -> MiuixTheme.textStyles.title4
            SectionTitleStyle.Label -> MiuixTheme.textStyles.footnote1.copy(fontWeight = FontWeight.Medium)
            SectionTitleStyle.Subsection -> MiuixTheme.textStyles.title4.copy(fontWeight = FontWeight.Medium)
        },
        color = if (style == SectionTitleStyle.Label) MiuixTheme.colorScheme.onSurfaceVariantSummary else MiuixTheme.colorScheme.onSurface,
    )
}

@Composable
fun SectionDescriptionMiuix(
    text: String,
    style: SectionDescriptionStyle,
) {
    Text(
        text = text,
        style = when (style) {
            SectionDescriptionStyle.Body -> MiuixTheme.textStyles.body1
            SectionDescriptionStyle.Supporting -> MiuixTheme.textStyles.footnote1
        },
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
    )
}

@Composable
fun AppTextFieldMiuix(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MiuixTheme.colorScheme.surfaceContainerHighest,
            labelColor = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            borderColor = MiuixTheme.colorScheme.primary,
        ),
        textStyle = MiuixTheme.textStyles.main.copy(color = MiuixTheme.colorScheme.onSurface),
        singleLine = singleLine,
    )
}

@Composable
fun ConfigTextFieldMiuix(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MiuixTheme.colorScheme.surfaceContainerHighest,
            labelColor = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            borderColor = MiuixTheme.colorScheme.primary,
        ),
        textStyle = MiuixTheme.textStyles.main.copy(color = MiuixTheme.colorScheme.onSurfaceSecondary),
        minLines = minLines,
        maxLines = maxLines,
    )
}

@Composable
fun ConfigToggleCardMiuix(
    checked: Boolean,
    title: String,
    description: String,
    onToggle: () -> Unit,
) {
    Card(
        onClick = onToggle,
        colors = CardDefaults.defaultColors(
            color = MiuixTheme.colorScheme.surfaceContainerHighest,
            contentColor = MiuixTheme.colorScheme.onSurfaceContainerHighest,
        ),
        insideMargin = PaddingValues(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(
                state = if (checked) ToggleableState.On else ToggleableState.Off,
                onClick = null,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MiuixTheme.textStyles.headline2)
                Text(
                    text = description,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }
    }
}

@Composable
fun InlineTextButtonMiuix(
    label: String,
    onClick: () -> Unit,
) {
    TextButton(
        text = label,
        onClick = onClick,
    )
}

@Composable
fun DualActionRowMiuix(
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    secondaryLabel: String,
    onSecondaryClick: () -> Unit,
    primaryFilled: Boolean = true,
) {
    val hasPrimary = primaryLabel.isNotEmpty()
    val hasSecondary = secondaryLabel.isNotEmpty()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (hasPrimary && hasSecondary) Arrangement.spacedBy(10.dp) else Arrangement.Start,
    ) {
        val weight = if (hasPrimary && hasSecondary) Modifier.weight(1f) else Modifier.fillMaxWidth()
        if (hasPrimary) {
            if (primaryFilled) {
                Button(
                    onClick = onPrimaryClick,
                    modifier = weight.height(56.dp),
                    colors = ButtonDefaults.buttonColorsPrimary(),
                ) {
                    Text(primaryLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, style = MiuixTheme.textStyles.button.copy(fontWeight = FontWeight.Medium))
                }
            } else {
                Button(
                    onClick = onPrimaryClick,
                    modifier = weight.height(56.dp),
                    colors = ButtonDefaults.buttonColors().copy(color = MiuixTheme.colorScheme.secondary),
                ) {
                    Text(primaryLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, style = MiuixTheme.textStyles.button.copy(fontWeight = FontWeight.Medium))
                }
            }
        }
        if (hasSecondary) {
            Button(
                onClick = onSecondaryClick,
                modifier = weight.height(56.dp),
                colors = ButtonDefaults.buttonColors().copy(color = MiuixTheme.colorScheme.secondary),
            ) {
                Text(secondaryLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, style = MiuixTheme.textStyles.button.copy(fontWeight = FontWeight.Medium))
            }
        }
    }
}

@Composable
fun PrimaryActionButtonMiuix(
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColorsPrimary(),
    ) {
        Text(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = MiuixTheme.textStyles.button.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
fun ActionGridMiuix(actions: List<GridActionItem>) {
    actions.chunked(2).forEach { rowActions ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            rowActions.forEach { item ->
                val currentBgColor = if (item.isError) MiuixTheme.colorScheme.error else MiuixTheme.colorScheme.secondary
                Button(
                    onClick = item.action,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors().copy(color = currentBgColor),
                    insideMargin = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                ) {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = if (item.isError) MiuixTheme.colorScheme.onError else Color.Unspecified,
                        style = MiuixTheme.textStyles.button.copy(fontWeight = FontWeight.Medium),
                    )
                }
            }
            if (rowActions.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun SectionCardMiuix(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        content = content,
    )
}

@Composable
fun WarningBannerMiuix(
    message: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = CardDefaults.defaultColors(
        color = MiuixTheme.colorScheme.errorContainer,
        contentColor = MiuixTheme.colorScheme.onErrorContainer,
    )
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                color = MiuixTheme.colorScheme.onErrorContainer,
                fontSize = 14.sp,
            )
        }
    }
    if (onClick != null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            onClick = onClick,
            colors = colors,
            showIndication = true,
            insideMargin = PaddingValues(0.dp),
        ) { content() }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = colors,
            insideMargin = PaddingValues(0.dp),
        ) { content() }
    }
}

@Composable
fun InfoBannerMiuix(
    message: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = CardDefaults.defaultColors(
        color = MiuixTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
        contentColor = MiuixTheme.colorScheme.primary,
    )
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
    if (onClick != null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            onClick = onClick,
            colors = colors,
            showIndication = true,
            insideMargin = PaddingValues(0.dp),
        ) { content() }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = colors,
            insideMargin = PaddingValues(0.dp),
        ) { content() }
    }
}
