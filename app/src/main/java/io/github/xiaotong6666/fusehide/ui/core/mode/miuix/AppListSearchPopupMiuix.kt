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
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Composable
fun AppListSearchPopupMiuix(
    searchStatus: SearchStatus,
    searchResults: List<GroupedApps>,
    hiddenPackages: Set<String>,
    dynamicTopPadding: Dp,
    bottomInnerPadding: Dp,
    onSearchStatusChange: (SearchStatus) -> Unit,
    onNavigateToAppConfig: (String) -> Unit,
) {
    val expandedSearchUids = remember { mutableStateOf(setOf<Int>()) }
    LaunchedEffect(searchResults) {
        expandedSearchUids.value = searchResults.filter { it.apps.size > 1 }.map { it.uid }.toSet()
    }
    with(searchStatus) {
        SearchPager(
            onSearchStatusChange = onSearchStatusChange,
            defaultResult = {
                val imeBottomPadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
                LazyColumn(modifier = Modifier.fillMaxSize().overScrollVertical()) {
                    item { Spacer(Modifier.height(maxOf(bottomInnerPadding, imeBottomPadding))) }
                }
            },
            searchBarTopPadding = dynamicTopPadding,
        ) {
            val imeBottomPadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
            LazyColumn(modifier = Modifier.fillMaxSize().overScrollVertical()) {
                item { Spacer(Modifier.height(6.dp)) }
                items(searchResults, key = { it.uid }, contentType = { "group" }) { group ->
                    val expanded = expandedSearchUids.value.contains(group.uid)
                    AnimatedVisibility(
                        visible = searchResults.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        AppListGroupMiuix(
                            group = group,
                            hiddenPackages = hiddenPackages,
                            expanded = expanded,
                            onToggleExpand = {
                                if (group.apps.size > 1) {
                                    expandedSearchUids.value = if (expanded) expandedSearchUids.value - group.uid else expandedSearchUids.value + group.uid
                                }
                            },
                            onOpenApp = {
                                onSearchStatusChange(searchStatus.copy(current = SearchStatus.Status.COLLAPSED))
                                onNavigateToAppConfig(it)
                            },
                            matchedPackageNames = group.matchedPackageNames,
                            alwaysShowChildren = true,
                        )
                    }
                }
                item { Spacer(Modifier.height(maxOf(bottomInnerPadding, imeBottomPadding))) }
            }
        }
    }
}
