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

package io.github.xiaotong6666.fusehide.ui.app

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.xiaotong6666.fusehide.R
import io.github.xiaotong6666.fusehide.ui.core.model.ConfigCallbacks
import io.github.xiaotong6666.fusehide.ui.core.model.ConfigUiState
import io.github.xiaotong6666.fusehide.ui.core.model.DebugCallbacks
import io.github.xiaotong6666.fusehide.ui.core.model.DebugUiState
import io.github.xiaotong6666.fusehide.ui.core.model.HideConfigDiff
import io.github.xiaotong6666.fusehide.ui.core.model.HomeCallbacks
import io.github.xiaotong6666.fusehide.ui.core.model.HookStatusUiState
import io.github.xiaotong6666.fusehide.ui.core.model.MainDestination
import io.github.xiaotong6666.fusehide.ui.core.model.SettingsCallbacks
import io.github.xiaotong6666.fusehide.ui.core.model.SettingsUiState
import io.github.xiaotong6666.fusehide.ui.feature.config.ConfigPage
import io.github.xiaotong6666.fusehide.ui.feature.config.applist.AppListViewModel
import io.github.xiaotong6666.fusehide.ui.feature.debug.DebugPage
import io.github.xiaotong6666.fusehide.ui.feature.home.HomePage
import io.github.xiaotong6666.fusehide.ui.feature.settings.SettingsPage
import io.github.xiaotong6666.uihelper.chrome.ComposableContent
import io.github.xiaotong6666.uihelper.chrome.LocalAppChromeState
import io.github.xiaotong6666.uihelper.chrome.LocalMaterialNestedScrollConnection
import io.github.xiaotong6666.uihelper.chrome.LocalMiuixCollapsedFractionProvider
import io.github.xiaotong6666.uihelper.chrome.LocalMiuixNestedScrollConnection
import io.github.xiaotong6666.uihelper.chrome.rememberAppChromeState
import io.github.xiaotong6666.uihelper.material.ExpressiveScaffold
import io.github.xiaotong6666.uihelper.material.expressiveTopAppBarColors
import io.github.xiaotong6666.uihelper.material.materialScaffoldEdgeToEdgeInsets
import io.github.xiaotong6666.uihelper.material.materialTopBarEdgeToEdgeInsets
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import kotlin.math.abs
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem

private data class MainDestinationSpec(
    val destination: MainDestination,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

private class MainPagerState(
    val pagerState: PagerState,
    private val coroutineScope: CoroutineScope,
) {
    var selectedPage by mutableIntStateOf(pagerState.currentPage)
        private set

    var isNavigating by mutableStateOf(false)
        private set

    private var navJob: Job? = null

    fun animateToPage(targetIndex: Int) {
        if (targetIndex == selectedPage) return

        navJob?.cancel()
        selectedPage = targetIndex
        isNavigating = true

        navJob = coroutineScope.launch {
            val myJob = coroutineContext.job
            try {
                pagerState.springAnimateToPage(targetIndex)
            } finally {
                if (navJob == myJob) {
                    isNavigating = false
                    if (pagerState.settledPage != targetIndex) {
                        selectedPage = pagerState.settledPage
                    }
                }
            }
        }
    }

    fun syncPage() {
        if (!isNavigating && selectedPage != pagerState.currentPage) {
            selectedPage = pagerState.currentPage
        }
    }

    suspend fun cancelNavigation() {
        navJob?.cancelAndJoin()
        navJob = null
        isNavigating = false
        selectedPage = pagerState.settledPage
    }
}

@Composable
private fun rememberMainPagerState(
    pagerState: PagerState,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): MainPagerState = remember(pagerState, coroutineScope) {
    MainPagerState(pagerState, coroutineScope)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainPage(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    hookStatus: HookStatusUiState,
    configState: ConfigUiState,
    debugState: DebugUiState,
    homeCallbacks: HomeCallbacks,
    configCallbacks: ConfigCallbacks,
    appListViewModel: AppListViewModel,
    onOpenGlobalConfig: () -> Unit,
    onOpenAppConfig: (String) -> Unit,
    debugCallbacks: DebugCallbacks,
    settingsState: SettingsUiState,
    settingsCallbacks: SettingsCallbacks,
) {
    val pageSpecs = remember {
        listOf(
            MainDestinationSpec(MainDestination.Home, "", Icons.Outlined.Home, Icons.Filled.Home),
            MainDestinationSpec(MainDestination.Config, "", Icons.Outlined.Tune, Icons.Filled.Tune),
            MainDestinationSpec(MainDestination.Probe, "", Icons.Outlined.Search, Icons.Filled.Search),
            MainDestinationSpec(MainDestination.Settings, "", Icons.Outlined.Settings, Icons.Filled.Settings),
        )
    }.map { spec ->
        spec.copy(
            title = when (spec.destination) {
                MainDestination.Home -> stringResource(R.string.nav_home)
                MainDestination.Config -> stringResource(R.string.nav_config)
                MainDestination.Probe -> stringResource(R.string.nav_probe)
                MainDestination.Settings -> stringResource(R.string.nav_settings)
            },
        )
    }
    val pagerState = rememberPagerState(initialPage = selectedTab, pageCount = { pageSpecs.size })
    val mainPagerState = rememberMainPagerState(pagerState)
    val settledPage = pagerState.settledPage
    val activePageIndex = mainPagerState.selectedPage.coerceIn(0, pageSpecs.lastIndex)
    val activePage = pageSpecs[activePageIndex]
    val appChromeState = rememberAppChromeState()
    val chromeSpec = appChromeState.spec
    val miuixScrollBehavior = MiuixScrollBehavior()
    val onPageSelected: (Int) -> Unit = { index ->
        if (mainPagerState.selectedPage != index) {
            mainPagerState.animateToPage(index)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        Log.d("FuseHideUi", "main.currentPage=${pagerState.currentPage} settled=${pagerState.settledPage} selectedTab=$selectedTab")
        mainPagerState.syncPage()
    }

    LaunchedEffect(settledPage) {
        Log.d("FuseHideUi", "main.settledPage=$settledPage selectedTab=$selectedTab activePageIndex=$activePageIndex")
        if (selectedTab != settledPage) {
            onTabSelected(settledPage)
        }
    }

    LaunchedEffect(selectedTab) {
        Log.d("FuseHideUi", "main.selectedTab=$selectedTab current=${mainPagerState.selectedPage} settled=${pagerState.settledPage}")
        val coercedTarget = selectedTab.coerceIn(0, pageSpecs.lastIndex)
        if (!mainPagerState.isNavigating && coercedTarget != mainPagerState.selectedPage) {
            mainPagerState.animateToPage(coercedTarget)
        }
    }

    LaunchedEffect(pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress && mainPagerState.isNavigating) {
            mainPagerState.cancelNavigation()
        }
    }

    when (LocalUiMode.current) {
        UiMode.Miuix -> {
            top.yukonga.miuix.kmp.basic.Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    val defaultTopBar: ComposableContent = {
                        top.yukonga.miuix.kmp.basic.TopAppBar(
                            title = activePage.title,
                            color = MiuixTheme.colorScheme.surface,
                            titleColor = MiuixTheme.colorScheme.onSurface,
                            actions = {
                                chromeSpec.miuixActions?.invoke()
                            },
                            scrollBehavior = miuixScrollBehavior,
                        )
                    }
                    val miuixTopBar = chromeSpec.miuixTopBar
                    val miuixTopBarWrapper = chromeSpec.miuixTopBarWrapper
                    when {
                        miuixTopBar != null -> miuixTopBar()
                        miuixTopBarWrapper != null -> miuixTopBarWrapper(defaultTopBar)
                        else -> defaultTopBar()
                    }
                },
                popupHost = chromeSpec.miuixPopupHost ?: {},
                bottomBar = if (chromeSpec.hideBottomBar) {
                    {}
                } else {
                    {
                        MiuixNavigationBar {
                            pageSpecs.forEachIndexed { index, page ->
                                MiuixNavigationBarItem(
                                    selected = activePageIndex == index,
                                    onClick = { onPageSelected(index) },
                                    icon = page.icon,
                                    label = page.title,
                                )
                            }
                        }
                    }
                },
            ) { paddingValues ->
                CompositionLocalProvider(
                    LocalAppChromeState provides appChromeState,
                    LocalMiuixNestedScrollConnection provides miuixScrollBehavior.nestedScrollConnection,
                    LocalMiuixCollapsedFractionProvider provides { miuixScrollBehavior.state.collapsedFraction },
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            overscrollEffect = null,
                        ) { page ->
                            val pageModifier = Modifier
                                .then(if (page == pagerState.currentPage) Modifier.overScrollVertical() else Modifier)
                                .then(
                                    if (chromeSpec.consumeOuterScroll) {
                                        Modifier
                                    } else {
                                        Modifier.nestedScroll(miuixScrollBehavior.nestedScrollConnection)
                                    },
                                )

                            when (page) {
                                0 -> HomePage(hookStatus = hookStatus, configState = configState, callbacks = homeCallbacks, contentPadding = paddingValues, isCurrentPage = page == pagerState.currentPage, modifier = pageModifier)
                                1 -> ConfigPage(hookStatus = hookStatus, state = configState, callbacks = configCallbacks, contentPadding = paddingValues, isCurrentPage = page == pagerState.currentPage, modifier = pageModifier, appListViewModel = appListViewModel, onOpenGlobalConfig = onOpenGlobalConfig, onOpenAppConfig = onOpenAppConfig)
                                2 -> DebugPage(state = debugState, callbacks = debugCallbacks, contentPadding = paddingValues, isCurrentPage = page == pagerState.currentPage, modifier = pageModifier)
                                else -> SettingsPage(state = settingsState, callbacks = settingsCallbacks, contentPadding = paddingValues, isCurrentPage = page == pagerState.currentPage, modifier = pageModifier)
                            }
                        }
                        chromeSpec.overlayContent?.invoke(paddingValues)
                    }
                }
            }
        }

        UiMode.Material -> {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            ExpressiveScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (chromeSpec.consumeOuterScroll) {
                            Modifier
                        } else {
                            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                        },
                    ),
                contentWindowInsets = materialScaffoldEdgeToEdgeInsets(),
                topBar = chromeSpec.materialTopBar ?: {
                    LargeFlexibleTopAppBar(
                        title = { Text(text = activePage.title) },
                        actions = {
                            chromeSpec.materialActions?.invoke(this)
                        },
                        colors = expressiveTopAppBarColors(),
                        scrollBehavior = scrollBehavior,
                        windowInsets = materialTopBarEdgeToEdgeInsets(),
                    )
                },
                bottomBar = if (chromeSpec.hideBottomBar) {
                    {}
                } else {
                    {
                        ShortNavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            windowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout).only(
                                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                            ),
                        ) {
                            pageSpecs.forEachIndexed { index, page ->
                                val selected = activePageIndex == index
                                ShortNavigationBarItem(
                                    selected = activePageIndex == index,
                                    onClick = { onPageSelected(index) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) page.selectedIcon else page.icon,
                                            contentDescription = page.title,
                                        )
                                    },
                                    label = { Text(page.title) },
                                )
                            }
                        }
                    }
                },
            ) { paddingValues ->
                CompositionLocalProvider(
                    LocalAppChromeState provides appChromeState,
                    LocalMaterialNestedScrollConnection provides scrollBehavior.nestedScrollConnection,
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            overscrollEffect = null,
                        ) { page ->
                            when (page) {
                                0 -> HomePage(hookStatus = hookStatus, configState = configState, callbacks = homeCallbacks, contentPadding = paddingValues, isCurrentPage = page == pagerState.currentPage)
                                1 -> ConfigPage(hookStatus = hookStatus, state = configState, callbacks = configCallbacks, contentPadding = paddingValues, isCurrentPage = page == pagerState.currentPage, appListViewModel = appListViewModel, onOpenGlobalConfig = onOpenGlobalConfig, onOpenAppConfig = onOpenAppConfig)
                                2 -> DebugPage(state = debugState, callbacks = debugCallbacks, contentPadding = paddingValues, isCurrentPage = page == pagerState.currentPage)
                                else -> SettingsPage(state = settingsState, callbacks = settingsCallbacks, contentPadding = paddingValues, isCurrentPage = page == pagerState.currentPage)
                            }
                        }
                        chromeSpec.overlayContent?.invoke(paddingValues)
                    }
                }
            }
        }
    }
}

private suspend fun PagerState.springAnimateToPage(target: Int) {
    if (target !in 0 until pageCount) return
    var shouldSnapToTarget = false
    scroll(MutatePriority.UserInput) {
        val pageSize = layoutInfo.pageSize + layoutInfo.pageSpacing
        val distance = target - currentPage - currentPageOffsetFraction
        val scrollPixels = distance * pageSize
        if (abs(scrollPixels) <= 0.5f) {
            return@scroll
        }

        var consumedScroll = 0f
        var skipScroll = false
        Animatable(0f).animateTo(
            targetValue = scrollPixels,
            animationSpec = spring(
                stiffness = 322.2f,
                dampingRatio = 32.31f / (2f * kotlin.math.sqrt(322.2f)),
                visibilityThreshold = 0.5f,
            ),
        ) {
            if (skipScroll) return@animateTo

            val delta = value - consumedScroll
            if (abs(delta) > 0.5f) {
                val consumed = scrollBy(delta)
                consumedScroll += consumed
                if (abs(delta - consumed) > 0.1f) {
                    shouldSnapToTarget = true
                    skipScroll = true
                }
            } else {
                consumedScroll = value
            }

            if (abs(velocity) < 0.1f && abs(scrollPixels - consumedScroll) < 1.0f) {
                skipScroll = true
            }
        }

        val remaining = scrollPixels - consumedScroll
        if (abs(remaining) > 0.5f) {
            scrollBy(remaining)
        }
    }

    if (shouldSnapToTarget || currentPage != target) {
        scrollToPage(target)
    }
}

// For Android Studio preview compose interface.
@Preview(showBackground = true, device = "id:pixel_9_pro")
@Composable
private fun PreviewMainPage() {
    io.github.xiaotong6666.fusehide.ui.theme.FuseHideTheme {
        MainPage(
            selectedTab = 0,
            onTabSelected = {},
            hookStatus = HookStatusUiState(
                infoText = "Kernel: 6.1.118\nDevice: Fuxi\nSDK: 3600000",
                statusText = "Hooked: com.example.app (1234)",
                isHooked = true,
                hookedPackage = "com.example.app",
                hookedPid = 1234,
                hookCheckCompleted = true,
            ),
            configState = ConfigUiState(
                configStatusText = "The saved hidden configuration has been loaded.",
                lastAckTokenText = "-",
                lastAckResultText = "-",
                lastApplyTimeText = "-",
                draftVsAppliedDiff = HideConfigDiff(
                    hasDifferences = false,
                    summary = "None",
                    details = "",
                ),
                appliedConfigSnapshotText = "Current native config snapshot...",
                highlightConfigResults = false,
                configResultsScrollToken = 0,
                currentHideConfig = io.github.xiaotong6666.fusehide.config.HideConfigDefaults.value,
            ),
            debugState = DebugUiState(
                pathText = "/storage/emulated/0/Android",
                pathText2 = "",
                outputText = "Stat /storage/emulated/0/Android -> OK",
            ),
            homeCallbacks = HomeCallbacks(
                onStatusClick = {},
            ),
            configCallbacks = ConfigCallbacks(
                onStatusClick = {},
                onConfigUpdate = {},
                onApplyConfigClick = {},
                onResetConfigClick = {},
            ),
            appListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onOpenGlobalConfig = {},
            onOpenAppConfig = {},
            debugCallbacks = DebugCallbacks(
                onStatusClick = {},
                onPathChanged = {},
                onPath2Changed = {},
                onStatClick = {},
                onAccessClick = {},
                onListClick = {},
                onOpenClick = {},
                onGetConClick = {},
                onCreateClick = {},
                onMkdirClick = {},
                onMoveClick = {},
                onRmdirClick = {},
                onUnlinkClick = {},
                onAllPkgClick = {},
                onInsertZwjClick = {},
                onClearClick = {},
                onResetClick = {},
                onCopyAllClick = {},
                onSelfDataClick = {},
            ),
            settingsState = SettingsUiState(uiMode = UiMode.Miuix),
            settingsCallbacks = SettingsCallbacks(onToggleUiMode = {}),
        )
    }
}
