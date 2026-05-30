package com.shpak.dynamicocean.old.repository

import com.shpak.dynamicocean.old.model.DeviceScreen
import com.shpak.dynamicocean.old.model.DisplayCutout

interface ScreenDataRepository {
    var deviceScreen: DeviceScreen?
    var displayCutout: DisplayCutout?
}