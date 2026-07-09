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

package io.github.xiaotong6666.fusehide.ui.feature.config.applist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.fusehide.R
import io.github.xiaotong6666.fusehide.ui.core.model.ConfigCallbacks
import io.github.xiaotong6666.fusehide.ui.core.model.ConfigUiState
import io.github.xiaotong6666.uihelper.chrome.AppChromeSpec
import io.github.xiaotong6666.uihelper.chrome.LocalMaterialNestedScrollConnection
import io.github.xiaotong6666.uihelper.chrome.LocalMiuixCollapsedFractionProvider
import io.github.xiaotong6666.uihelper.chrome.LocalMiuixNestedScrollConnection
import io.github.xiaotong6666.uihelper.chrome.PageChrome
import io.github.xiaotong6666.uihelper.components.WarningBanner
import io.github.xiaotong6666.uihelper.components.material.AppListGroupMaterial
import io.github.xiaotong6666.uihelper.components.material.SegmentedItem
import io.github.xiaotong6666.uihelper.components.miuix.AppListGroupMiuix
import io.github.xiaotong6666.uihelper.components.miuix.AppListSearchFieldMiuix
import io.github.xiaotong6666.uihelper.components.miuix.AppListSearchPopupMiuix
import io.github.xiaotong6666.uihelper.components.miuix.SearchBox
import io.github.xiaotong6666.uihelper.components.model.GroupedApps
import io.github.xiaotong6666.uihelper.components.model.SearchStatus
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.PullToRefresh
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
            onSearchStatusChange = appListViewModel::updateSearchStatus,
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
    onSearchStatusChange: (SearchStatus) -> Unit,
    onNavigateToGlobalConfig: () -> Unit,
    onNavigateToAppConfig: (String) -> Unit,
    contentPadding: PaddingValues,
    isCurrentPage: Boolean,
    bottomInnerPadding: Dp,
) {
    val listState = rememberLazyListState()
    val searchListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshStateMaterial()
    val searchStatus = uiState.searchStatus
    val sharedNestedScrollConnection = LocalMaterialNestedScrollConnection.current
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scaledDensity = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    val searchBarState = rememberContainedSearchBarState()
    val textFieldState = rememberTextFieldState()
    val currentQuery = textFieldState.text.toString()
    val latestSearchText by rememberUpdatedState(searchStatus.searchText)
    val latestSearchResults = rememberUpdatedState(orderedSearchResults)
    val latestHiddenPackages = rememberUpdatedState(hiddenPackages)
    val latestOpenApp = rememberUpdatedState(onNavigateToAppConfig)
    val isSearchExpanded = searchBarState.currentValue != SearchBarValue.Collapsed || searchBarState.targetValue != SearchBarValue.Collapsed
    var previousSearchBarValue by remember { mutableStateOf(searchBarState.currentValue) }
    var shouldClearOnCollapse by remember { mutableStateOf(true) }

    val clearSearchText: () -> Unit = {
        textFieldState.setTextAndPlaceCursorAtEnd("")
        onSearchStatusChange(searchStatus.copy(searchText = ""))
    }
    val collapseAndClear: () -> Unit = {
        shouldClearOnCollapse = false
        clearSearchText()
        scope.launch { searchBarState.animateToCollapsed() }
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    DisposableEffect(Unit) {
        onDispose {
            keyboardController?.hide()
        }
    }

    LaunchedEffect(searchStatus.searchText) {
        val current = textFieldState.text.toString()
        if (current != searchStatus.searchText) {
            textFieldState.setTextAndPlaceCursorAtEnd(searchStatus.searchText)
        }
    }

    LaunchedEffect(textFieldState, latestSearchText) {
        snapshotFlow { textFieldState.text.toString() }
            .distinctUntilChanged()
            .collect { value ->
                if (value != latestSearchText) {
                    onSearchStatusChange(searchStatus.copy(searchText = value))
                }
            }
    }

    LaunchedEffect(searchBarState) {
        snapshotFlow { searchBarState.currentValue }
            .distinctUntilChanged()
            .collect { value ->
                val collapsedFromExpanded =
                    previousSearchBarValue != SearchBarValue.Collapsed && value == SearchBarValue.Collapsed
                previousSearchBarValue = value
                if (collapsedFromExpanded) {
                    if (shouldClearOnCollapse) {
                        clearSearchText()
                    }
                    shouldClearOnCollapse = true
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            }
    }

    BackHandler(isSearchExpanded) {
        if (isSearchExpanded) {
            collapseAndClear()
        }
    }

    val inputField: @Composable () -> Unit = {
        CompositionLocalProvider(LocalDensity provides scaledDensity) {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                placeholder = { androidx.compose.material3.Text(stringResource(R.string.search_apps_placeholder)) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
                leadingIcon = {
                    if (isSearchExpanded) {
                        androidx.compose.material3.IconButton(
                            modifier = Modifier.padding(end = 8.dp),
                            onClick = { collapseAndClear() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    } else {
                        Icon(Icons.Filled.Search, null)
                    }
                },
                trailingIcon = {
                    if (isSearchExpanded && currentQuery.isNotEmpty()) {
                        androidx.compose.material3.IconButton(onClick = { clearSearchText() }) {
                            Icon(Icons.Filled.Close, null)
                        }
                    }
                },
                interactionSource = interactionSource,
            )
        }
    }

    PageChrome(
        AppChromeSpec(
            materialActions = {
                androidx.compose.material3.IconButton(
                    onClick = onNavigateToGlobalConfig,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.global_hide_config_title),
                    )
                }
            },
            overlayContent = {
                ExpandedFullScreenContainedSearchBar(
                    state = searchBarState,
                    inputField = inputField,
                    windowInsets = { SearchBarDefaults.fullScreenWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal) },
                ) {
                    val bottomPadding = SearchBarDefaults.fullScreenWindowInsets.asPaddingValues().calculateBottomPadding()
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            state = searchListState,
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (sharedNestedScrollConnection != null) {
                                        Modifier.nestedScroll(sharedNestedScrollConnection)
                                    } else {
                                        Modifier
                                    },
                                ),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp + bottomPadding,
                            ),
                        ) {
                            itemsIndexed(latestSearchResults.value, key = { _, item -> item.uid }) { index, group ->
                                SegmentedItem(index = index, count = latestSearchResults.value.size) {
                                    AppListGroupMaterial(
                                        group = group,
                                        hiddenPackages = latestHiddenPackages.value,
                                        enabledLabel = stringResource(R.string.app_hide_enabled_label),
                                        expanded = group.apps.size > 1,
                                        onToggleExpand = {},
                                        onOpenApp = {
                                            collapseAndClear()
                                            latestOpenApp.value(it)
                                        },
                                        matchedPackageNames = group.matchedPackageNames,
                                        alwaysShowChildren = true,
                                    )
                                }
                            }
                        }
                        Box(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .imePadding()
                                .padding(bottom = 16.dp),
                        ) {
                            androidx.compose.material3.SnackbarHost(hostState = snackbarHostState)
                        }
                    }
                }
            },
        ),
        enabled = isCurrentPage,
    )

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding()),
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 74.dp),
                isRefreshing = uiState.isRefreshing,
                state = pullToRefreshState,
            )
        },
    ) {
        val expandedUids = remember { mutableStateOf(setOf<Int>()) }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (sharedNestedScrollConnection != null) {
                        Modifier.nestedScroll(sharedNestedScrollConnection)
                    } else {
                        Modifier
                    },
                ),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp + bottomInnerPadding),
        ) {
            item {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                        .padding(bottom = 18.dp),
                    state = searchBarState,
                    inputField = inputField,
                    colors = SearchBarDefaults.colors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest),
                )
            }
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
                        enabledLabel = stringResource(R.string.app_hide_enabled_label),
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
                    enabledLabel = stringResource(R.string.app_hide_enabled_label),
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
                                enabledLabel = stringResource(R.string.app_hide_enabled_label),
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
