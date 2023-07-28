/*
 * Copyright 2023 Jaewoong Cheon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.woong.wheelpicker.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import io.woong.wheelpicker.ValuePickerAdapter

/**
 * An internal adapter to use [ValuePickerAdapter] with Jetpack Compose.
 */
internal class ComposeValuePickerAdapter<T>(
    private val itemContent: @Composable (BoxScope.(value: T) -> Unit)
) : ValuePickerAdapter<T, View>() {
    override fun createItemView(parent: ViewGroup): View {
        val composeItemView = ComposeView(parent.context)
        composeItemView.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
        return composeItemView
    }

    override fun bindItemView(itemView: View, position: Int) {
        val value = getValue(position)
        itemView as ComposeView
        itemView.setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                content = { itemContent(value) }
            )
        }
    }
}
