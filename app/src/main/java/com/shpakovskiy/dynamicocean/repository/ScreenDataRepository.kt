package com.shpakovskiy.dynamicocean.repository

import com.shpakovskiy.dynamicocean.model.DeviceScreen
import com.shpakovskiy.dynamicocean.model.DisplayCutout

interface ScreenDataRepository {
    var deviceScreen: DeviceScreen?
    var displayCutout: DisplayCutout?
}