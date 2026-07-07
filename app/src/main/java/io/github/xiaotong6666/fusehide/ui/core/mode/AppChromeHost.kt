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

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection

typealias ComposableContent = @Composable () -> Unit
typealias MiuixTopBarWrapper = @Composable (content: ComposableContent) -> Unit

data class AppChromeSpec(
    val materialTopBar: (@Composable () -> Unit)? = null,
    val miuixTopBar: (@Composable () -> Unit)? = null,
    val miuixTopBarWrapper: MiuixTopBarWrapper? = null,
    val materialActions: (@Composable RowScope.() -> Unit)? = null,
    val miuixActions: (@Composable () -> Unit)? = null,
    val miuixPopupHost: (@Composable () -> Unit)? = null,
    val overlayContent: (@Composable (PaddingValues) -> Unit)? = null,
    val hideBottomBar: Boolean = false,
    val consumeOuterScroll: Boolean = false,
)

class AppChromeState {
    private var owner: Any? = null
    var spec by mutableStateOf(AppChromeSpec())
        private set

    fun set(owner: Any, spec: AppChromeSpec) {
        Log.d(
            "FuseHideUi",
            "chrome.set owner=${owner.hashCode()} materialTopBar=${spec.materialTopBar != null} miuixTopBar=${spec.miuixTopBar != null} miuixTopBarWrapper=${spec.miuixTopBarWrapper != null} materialActions=${spec.materialActions != null} miuixActions=${spec.miuixActions != null} popup=${spec.miuixPopupHost != null} consumeOuterScroll=${spec.consumeOuterScroll}",
        )
        this.owner = owner
        this.spec = spec
    }

    fun clear(owner: Any) {
        if (this.owner === owner) {
            Log.d("FuseHideUi", "chrome.clear owner=${owner.hashCode()}")
            this.owner = null
            this.spec = AppChromeSpec()
        }
    }
}

val LocalAppChromeState = compositionLocalOf<AppChromeState?> { null }
val LocalMiuixNestedScrollConnection = compositionLocalOf<NestedScrollConnection?> { null }
val LocalMiuixCollapsedFractionProvider = compositionLocalOf<(() -> Float)?> { null }

@Composable
fun rememberAppChromeState(): AppChromeState = remember { AppChromeState() }

@Composable
fun PageChrome(spec: AppChromeSpec, enabled: Boolean = true) {
    val appChromeState = LocalAppChromeState.current ?: return
    val owner = remember { Any() }

    if (!enabled) {
        DisposableEffect(appChromeState, owner) {
            appChromeState.clear(owner)
            onDispose {
                appChromeState.clear(owner)
            }
        }
        return
    }

    SideEffect {
        appChromeState.set(owner, spec)
    }

    DisposableEffect(appChromeState, owner) {
        onDispose {
            appChromeState.clear(owner)
        }
    }
}
