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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.fusehide.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    snackbarHostState: SnackbarHostState,
    title: @Composable () -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onClearClick: () -> Unit,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    defaultContent: @Composable (bottomPadding: Dp, closeSearch: () -> Unit) -> Unit,
    searchContent: @Composable (bottomPadding: Dp, closeSearch: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val searchBarPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else 16.dp,
        animationSpec = tween(durationMillis = 300),
        label = "searchBarPadding",
    )

    val closeSearch = { expanded = false }

    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    ) {
        if (!expanded) {
            LargeTopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                    actions()
                },
                scrollBehavior = scrollBehavior,
                windowInsets = materialTopBarEdgeToEdgeInsets(),
            )
            defaultContent(0.dp, closeSearch)
        } else {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchText,
                        onQueryChange = onSearchTextChange,
                        onSearch = {},
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text(stringResource(id = R.string.search_apps_placeholder)) },
                        leadingIcon = {
                            IconButton(onClick = { expanded = false }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { onClearClick() }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        },
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = searchBarPadding),
            ) {
                searchContent(0.dp, closeSearch)
            }
        }
    }
}
