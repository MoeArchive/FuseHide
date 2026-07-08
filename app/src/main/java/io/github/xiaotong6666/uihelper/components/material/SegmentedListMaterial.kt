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

package io.github.xiaotong6666.uihelper.components.material

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

val LocalListItemShape = compositionLocalOf<Shape?> { null }
private val SEGMENTED_OUTER_RADIUS = 16.dp
private val SEGMENTED_INNER_RADIUS = 4.dp
private const val SEGMENTED_SPRING_STIFFNESS = 800f
private const val SEGMENTED_SPRING_DAMPING = 0.9f

@Composable
private fun defaultSegmentedColors(): ListItemColors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceBright)

@DslMarker
annotation class SegmentedColumnDsl

@Composable
private fun defaultSingleSegmentedShape(index: Int, count: Int): Shape = if (count == 1) {
    MaterialTheme.shapes.large
} else {
    androidx.compose.foundation.shape.RoundedCornerShape(
        topStart = if (index == 0) SEGMENTED_OUTER_RADIUS else SEGMENTED_INNER_RADIUS,
        topEnd = if (index == 0) SEGMENTED_OUTER_RADIUS else SEGMENTED_INNER_RADIUS,
        bottomStart = if (index == count - 1) SEGMENTED_OUTER_RADIUS else SEGMENTED_INNER_RADIUS,
        bottomEnd = if (index == count - 1) SEGMENTED_OUTER_RADIUS else SEGMENTED_INNER_RADIUS,
    )
}

@Composable
fun SegmentedColumn(modifier: Modifier = Modifier, title: String = "", visibleLen: Int = 0, content: List<@Composable () -> Unit>) {
    if (content.isEmpty()) return
    Column(modifier = modifier) {
        if (title.isNotEmpty()) Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            content.forEachIndexed { index, itemContent ->
                CompositionLocalProvider(LocalListItemShape provides defaultSingleSegmentedShape(index = index, count = if (visibleLen > 0) visibleLen else content.size)) { itemContent() }
            }
        }
    }
}

@SegmentedColumnDsl
class SegmentedColumnScope {
    internal data class Entry(val key: Any?, val visible: Boolean, val content: @Composable () -> Unit)
    internal val entries = mutableListOf<Entry>()
    fun item(key: Any? = null, visible: Boolean = true, content: @Composable () -> Unit) {
        entries.add(Entry(key ?: entries.size, visible, content))
    }
}

@Composable
fun SegmentedColumn(modifier: Modifier = Modifier, title: String = "", content: SegmentedColumnScope.() -> Unit) {
    val entries = SegmentedColumnScope().apply(content).entries
    if (entries.isEmpty()) return
    Column(modifier = modifier) {
        if (title.isNotEmpty()) Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
        val floatSpring = spring<Float>(SEGMENTED_SPRING_DAMPING, SEGMENTED_SPRING_STIFFNESS)
        val dpSpring = spring<Dp>(SEGMENTED_SPRING_DAMPING, SEGMENTED_SPRING_STIFFNESS)
        val progresses = entries.mapIndexed { index, entry -> key(entry.key ?: index) { animateFloatAsState(targetValue = if (entry.visible) 1f else 0f, animationSpec = floatSpring, label = "SegmentedProgress") } }
        val firstVisible = entries.indexOfFirst { it.visible }
        val lastVisible = entries.indexOfLast { it.visible }
        Layout(content = {
            entries.forEachIndexed { index, entry ->
                key(entry.key ?: index) {
                    val isFirst = if (firstVisible == -1) index == 0 else index == firstVisible
                    val isLast = if (lastVisible == -1) index == entries.lastIndex else index == lastVisible
                    val topRadius by animateDpAsState(if (isFirst) SEGMENTED_OUTER_RADIUS else SEGMENTED_INNER_RADIUS, dpSpring, label = "SegmentedTopRadius")
                    val bottomRadius by animateDpAsState(if (isLast) SEGMENTED_OUTER_RADIUS else SEGMENTED_INNER_RADIUS, dpSpring, label = "SegmentedBottomRadius")
                    val gap by animateDpAsState(if (isFirst) 0.dp else 2.dp, dpSpring, label = "SegmentedGap")
                    val shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = topRadius, topEnd = topRadius, bottomStart = bottomRadius, bottomEnd = bottomRadius)
                    Box(
                        modifier = Modifier.zIndex(if (entry.visible) (entries.size - index).toFloat() else -index.toFloat()).graphicsLayer {
                            val progress = progresses[index].value.coerceAtLeast(0f)
                            clip = true
                            this.shape = object : Shape {
                                override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline = Outline.Rectangle(Rect(0f, 0f, size.width, size.height * progress))
                            }
                            alpha = (progress * 1.5f).coerceIn(0f, 1f)
                        },
                    ) {
                        CompositionLocalProvider(LocalListItemShape provides shape) { Column(modifier = Modifier.padding(top = gap)) { entry.content() } }
                    }
                }
            }
        }) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            val positions = IntArray(placeables.size)
            var y = 0f
            placeables.forEachIndexed { index, placeable ->
                positions[index] = y.roundToInt()
                y += placeable.height * progresses[index].value.coerceAtLeast(0f)
            }
            layout(constraints.maxWidth, y.roundToInt().coerceAtLeast(0)) { placeables.forEachIndexed { index, placeable -> placeable.placeRelative(0, positions[index]) } }
        }
    }
}

@Composable
fun SegmentedItem(index: Int, count: Int, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalListItemShape provides defaultSingleSegmentedShape(index, count)) { content() }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun SegmentedListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: ListItemColors = defaultSegmentedColors(),
    interactionSource: MutableInteractionSource? = null,
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    selected: Boolean = false,
) {
    val shape = LocalListItemShape.current ?: androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
    if (onLongClick != null) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .combinedClickable(
                    enabled = enabled,
                    onClick = onClick ?: {},
                    onLongClick = onLongClick,
                ),
            shape = shape,
            color = MaterialTheme.colorScheme.surfaceBright,
        ) {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                overlineContent = overlineContent,
                supportingContent = supportingContent,
                headlineContent = headlineContent,
            )
        }
    } else {
        Surface(modifier = modifier.fillMaxWidth(), shape = shape, color = MaterialTheme.colorScheme.surfaceBright, onClick = onClick ?: {}, enabled = enabled) {
            ListItem(colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent), leadingContent = leadingContent, trailingContent = trailingContent, overlineContent = overlineContent, supportingContent = supportingContent, headlineContent = headlineContent)
        }
    }
}
