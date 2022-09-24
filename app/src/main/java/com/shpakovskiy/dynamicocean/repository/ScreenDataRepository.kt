package com.shpakovskiy.dynamicocean.repository

import com.shpakovskiy.dynamicocean.model.DisplayCutout

interface ScreenDataRepository {
    var screenWidth: Int
    var screenHeight: Int
    var displayCutout: DisplayCutout?
}