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

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.fusehide.R
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState as rememberPullToRefreshStateMaterial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    state: ConfigUiState,
    callbacks: ConfigCallbacks,
    appListViewModel: AppListViewModel,
    contentPadding: PaddingValues,
    isCurrentPage: Boolean = true,
    onNavigateToGlobalConfig: () -> Unit,
    onNavigateToAppConfig: (String) -> Unit,
) {
    val uiState by appListViewModel.uiState.collectAsState()
    val bottomInnerPadding = contentPadding.calculateBottomPadding() + 8.dp
    var hasActivated by remember { mutableStateOf(false) }
    val hiddenPackages = remember(state.currentHideConfig) { hiddenPackageSet(state) }
    val orderedGroups = remember(uiState.groupedApps, hiddenPackages) {
        prioritizeHiddenGroups(uiState.groupedApps, hiddenPackages)
    }
    val orderedSearchResults = remember(uiState.searchResults, hiddenPackages) {
        prioritizeHiddenGroups(uiState.searchResults, hiddenPackages)
    }

    if (isCurrentPage) {
        hasActivated = true
    }

    if (hasActivated) {
        LaunchedEffect(appListViewModel) {
            appListViewModel.loadAppList().join()
        }
    }

    when (LocalUiMode.current) {
        UiMode.Material -> AppListScreenMaterialUnified(
            uiState = uiState,
            hasUnsavedChanges = state.draftVsAppliedDiff.hasDifferences,
            hiddenPackages = hiddenPackages,
            orderedGroups = orderedGroups,
            orderedSearchResults = orderedSearchResults,
            onRefresh = { appListViewModel.loadAppList(force = true) },
            onSearchTextChange = appListViewModel::updateSearchText,
            onClearSearch = { appListViewModel.updateSearchText("") },
            onNavigateToGlobalConfig = onNavigateToGlobalConfig,
            onNavigateToAppConfig = onNavigateToAppConfig,
            contentPadding = contentPadding,
            isCurrentPage = isCurrentPage,
            bottomInnerPadding = bottomInnerPadding,
        )

        UiMode.Miuix -> AppListScreenMiuixUnified(
            uiState = uiState,
            globalState = state,
            hiddenPackages = hiddenPackages,
            orderedGroups = orderedGroups,
            orderedSearchResults = orderedSearchResults,
            onRefresh = { appListViewModel.loadAppList(force = true) },
            onSearchStatusChange = appListViewModel::updateSearchStatus,
            onNavigateToGlobalConfig = onNavigateToGlobalConfig,
            onNavigateToAppConfig = onNavigateToAppConfig,
            contentPadding = contentPadding,
            isCurrentPage = isCurrentPage,
            bottomInnerPadding = bottomInnerPadding,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppListScreenMaterialUnified(
    uiState: AppListUiState,
    hasUnsavedChanges: Boolean,
    hiddenPackages: Set<String>,
    orderedGroups: List<GroupedApps>,
    orderedSearchResults: List<GroupedApps>,
    onRefresh: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onNavigateToGlobalConfig: () -> Unit,
    onNavigateToAppConfig: (String) -> Unit,
    contentPadding: PaddingValues,
    isCurrentPage: Boolean,
    bottomInnerPadding: Dp,
) {
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshStateMaterial()
    var localSearchText by remember { mutableStateOf(uiState.searchStatus.searchText) }
    LaunchedEffect(uiState.searchStatus.searchText) { localSearchText = uiState.searchStatus.searchText }
    val density = LocalDensity.current
    var searchContainerBottom by remember { mutableStateOf(0.dp) }
    val searchTopPadding = contentPadding.calculateTopPadding() + 12.dp

    PageChrome(
        AppChromeSpec(
            materialActions = {
                IconButton(onClick = onNavigateToGlobalConfig) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = stringResource(R.string.global_hide_config_title),
                    )
                }
            },
        ),
        enabled = isCurrentPage,
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = searchTopPadding, bottom = 12.dp)
                .onGloballyPositioned { coordinates ->
                    with(density) {
                        searchContainerBottom = coordinates.positionInParent().y.toDp() + coordinates.size.height.toDp() + 2.dp
                    }
                },
        ) {
            AppListSearchFieldMaterial(
                value = localSearchText,
                onValueChange = {
                    localSearchText = it
                    onSearchTextChange(it)
                },
                onClearClick = {
                    localSearchText = ""
                    onClearSearch()
                },
            )
        }
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(top = searchContainerBottom),
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = uiState.isRefreshing,
                    state = pullToRefreshState,
                )
            },
        ) {
            val expandedUids = remember { mutableStateOf(setOf<Int>()) }
            val isSearching = localSearchText.isNotBlank()
            LaunchedEffect(isSearching, orderedGroups.size, orderedSearchResults.size, uiState.isRefreshing, searchContainerBottom) {
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp + bottomInnerPadding),
            ) {
                if (isSearching) {
                    itemsIndexed(orderedSearchResults, key = { _, item -> item.uid }) { index, group ->
                        SegmentedItem(index = index, count = orderedSearchResults.size) {
                            AppListGroupMaterial(
                                group = group,
                                hiddenPackages = hiddenPackages,
                                expanded = group.apps.size > 1,
                                onToggleExpand = {},
                                onOpenApp = onNavigateToAppConfig,
                                matchedPackageNames = group.matchedPackageNames,
                                alwaysShowChildren = true,
                            )
                        }
                    }
                } else {
                    if (hasUnsavedChanges) {
                        item {
                            WarningBanner(
                                message = stringResource(R.string.unsaved_config_changes),
                                modifier = Modifier.padding(bottom = 10.dp),
                                onClick = onNavigateToGlobalConfig,
                            )
                        }
                    }
                    itemsIndexed(orderedGroups, key = { _, item -> item.uid }) { index, group ->
                        val expanded = expandedUids.value.contains(group.uid)
                        SegmentedItem(index = index, count = orderedGroups.size) {
                            AppListGroupMaterial(
                                group = group,
                                hiddenPackages = hiddenPackages,
                                expanded = expanded,
                                onToggleExpand = {
                                    if (group.apps.size > 1) {
                                        expandedUids.value = if (expanded) expandedUids.value - group.uid else expandedUids.value + group.uid
                                    }
                                },
                                onOpenApp = onNavigateToAppConfig,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppListScreenMiuixUnified(
    uiState: AppListUiState,
    globalState: ConfigUiState,
    hiddenPackages: Set<String>,
    orderedGroups: List<GroupedApps>,
    orderedSearchResults: List<GroupedApps>,
    onRefresh: () -> Unit,
    onSearchStatusChange: (SearchStatus) -> Unit,
    onNavigateToGlobalConfig: () -> Unit,
    onNavigateToAppConfig: (String) -> Unit,
    contentPadding: PaddingValues,
    isCurrentPage: Boolean,
    bottomInnerPadding: Dp,
) {
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    val sharedNestedScrollConnection = LocalMiuixNestedScrollConnection.current
    val collapsedFractionProvider = LocalMiuixCollapsedFractionProvider.current
    val searchStatus = uiState.searchStatus
    val dynamicTopPadding by remember(collapsedFractionProvider) {
        derivedStateOf { 12.dp * (1f - (collapsedFractionProvider?.invoke() ?: 0f)) }
    }
    val refreshTexts = listOf(
        stringResource(R.string.refresh_pulling),
        stringResource(R.string.refresh_release),
        stringResource(R.string.refresh_refreshing),
        stringResource(R.string.refresh_complete),
    )

    PageChrome(
        AppChromeSpec(
            miuixTopBarWrapper = { content ->
                searchStatus.TopAppBarAnim {
                    content()
                }
            },
            miuixActions = {
                IconButton(onClick = onNavigateToGlobalConfig) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        tint = colorScheme.onSurface,
                        contentDescription = stringResource(R.string.global_hide_config_title),
                    )
                }
            },
            miuixPopupHost = {
                AppListSearchPopupMiuix(
                    searchStatus = searchStatus,
                    searchResults = orderedSearchResults,
                    hiddenPackages = hiddenPackages,
                    dynamicTopPadding = dynamicTopPadding,
                    bottomInnerPadding = bottomInnerPadding,
                    onSearchStatusChange = onSearchStatusChange,
                    onNavigateToAppConfig = onNavigateToAppConfig,
                )
            },
            consumeOuterScroll = true,
        ),
        enabled = isCurrentPage,
    )

    searchStatus.SearchBox {
        val lazyListState = rememberLazyListState()
        val pullToRefreshState = rememberPullToRefreshState()
        var searchContainerBottom by remember { mutableStateOf(0.dp) }
        val searchTopPadding = contentPadding.calculateTopPadding()
        val listSpacingBelowSearch = 12.dp

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = searchTopPadding)
                    .alpha(if (searchStatus.isCollapsed()) 1f else 0f)
                    .onGloballyPositioned { coordinates ->
                        with(density) {
                            val newOffsetY = coordinates.positionInWindow().y.toDp()
                            if (searchStatus.offsetY != newOffsetY) {
                                onSearchStatusChange(searchStatus.copy(offsetY = newOffsetY))
                            }
                            searchContainerBottom = coordinates.positionInParent().y.toDp() + coordinates.size.height.toDp() + listSpacingBelowSearch
                        }
                    }
                    .then(
                        if (searchStatus.isCollapsed()) {
                            Modifier.pointerInput(searchStatus) {
                                detectTapGestures {
                                    onSearchStatusChange(searchStatus.copy(current = SearchStatus.Status.EXPANDING))
                                }
                            }
                        } else {
                            Modifier
                        },
                    ),
            ) {
                AppListSearchFieldMiuix(
                    label = searchStatus.label,
                    dynamicTopPadding = dynamicTopPadding,
                )
            }

            if (uiState.groupedApps.isEmpty() && !uiState.hasLoaded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = searchContainerBottom, bottom = bottomInnerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    InfiniteProgressIndicator()
                }
            } else {
                val expandedUids = remember { mutableStateOf(setOf<Int>()) }
                PullToRefresh(
                    modifier = Modifier.padding(top = searchContainerBottom),
                    isRefreshing = uiState.isRefreshing,
                    pullToRefreshState = pullToRefreshState,
                    onRefresh = onRefresh,
                    refreshTexts = refreshTexts,
                    contentPadding = PaddingValues(
                        start = contentPadding.calculateStartPadding(layoutDirection),
                        end = contentPadding.calculateEndPadding(layoutDirection),
                    ),
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxHeight()
                            .scrollEndHaptic()
                            .overScrollVertical()
                            .then(
                                if (sharedNestedScrollConnection != null) {
                                    Modifier.nestedScroll(sharedNestedScrollConnection)
                                } else {
                                    Modifier
                                },
                            ),
                        contentPadding = PaddingValues(
                            start = contentPadding.calculateStartPadding(layoutDirection),
                            end = contentPadding.calculateEndPadding(layoutDirection),
                        ),
                        overscrollEffect = null,
                    ) {
                        if (globalState.draftVsAppliedDiff.hasDifferences) {
                            item {
                                WarningBanner(
                                    message = stringResource(R.string.unsaved_config_changes),
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .padding(bottom = 12.dp),
                                    onClick = onNavigateToGlobalConfig,
                                )
                            }
                        }
                        itemsIndexed(orderedGroups, key = { _, item -> item.uid }, contentType = { _, _ -> "group" }) { _, group ->
                            val expanded = expandedUids.value.contains(group.uid)
                            AppListGroupMiuix(
                                group = group,
                                hiddenPackages = hiddenPackages,
                                expanded = expanded,
                                onToggleExpand = {
                                    if (group.apps.size > 1) {
                                        expandedUids.value = if (expanded) expandedUids.value - group.uid else expandedUids.value + group.uid
                                    }
                                },
                                onOpenApp = onNavigateToAppConfig,
                            )
                        }
                        item { Spacer(Modifier.height(bottomInnerPadding)) }
                    }
                }
            }
        }
    }
}

private fun hiddenPackageSet(globalState: ConfigUiState): Set<String> = buildSet {
    addAll(globalState.currentHideConfig.hiddenPackages)
    globalState.currentHideConfig.packageRules.forEach { add(it.packageName) }
}

private fun prioritizeHiddenGroups(groups: List<GroupedApps>, hiddenPackages: Set<String>): List<GroupedApps> {
    val (hidden, normal) = groups.partition { it.primary.packageName in hiddenPackages }
    return hidden + normal
}
