package com.shpak.dynamicocean.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface OceanRoute : NavKey {

    @Serializable
    data object Loading : OceanRoute

    @Serializable
    data object Onboarding : OceanRoute

    @Serializable
    data object Permissions : OceanRoute

    @Serializable
    data object Starter : OceanRoute

    @Serializable
    data object Settings : OceanRoute
}