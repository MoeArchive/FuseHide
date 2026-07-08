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

package io.github.xiaotong6666.fusehide.ui.feature.config

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.xiaotong6666.fusehide.ui.core.model.ConfigCallbacks
import io.github.xiaotong6666.fusehide.ui.core.model.ConfigUiState
import io.github.xiaotong6666.fusehide.ui.core.model.HookStatusUiState
import io.github.xiaotong6666.fusehide.ui.feature.config.applist.AppListScreen
import io.github.xiaotong6666.fusehide.ui.feature.config.applist.AppListViewModel

@Composable
fun ConfigPage(
    hookStatus: HookStatusUiState,
    state: ConfigUiState,
    callbacks: ConfigCallbacks,
    contentPadding: PaddingValues,
    isCurrentPage: Boolean = true,
    modifier: Modifier = Modifier,
    appListViewModel: AppListViewModel,
    onOpenGlobalConfig: () -> Unit,
    onOpenAppConfig: (String) -> Unit,
) {
    AppListScreen(
        state = state,
        callbacks = callbacks,
        appListViewModel = appListViewModel,
        contentPadding = contentPadding,
        isCurrentPage = isCurrentPage,
        onNavigateToGlobalConfig = onOpenGlobalConfig,
        onNavigateToAppConfig = onOpenAppConfig,
    )
}
