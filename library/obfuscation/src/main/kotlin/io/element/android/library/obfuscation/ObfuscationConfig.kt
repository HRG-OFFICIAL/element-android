/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.library.obfuscation

/**
 * Configuration interface for obfuscation behavior
 * Allows external modules to control debug mode without hard dependencies
 */
interface ObfuscationConfig {
    val isDebugMode: Boolean
}

/**
 * Default implementation using the library's own BuildConfig
 */
internal class DefaultObfuscationConfig : ObfuscationConfig {
    override val isDebugMode: Boolean = BuildConfig.DEBUG
}
