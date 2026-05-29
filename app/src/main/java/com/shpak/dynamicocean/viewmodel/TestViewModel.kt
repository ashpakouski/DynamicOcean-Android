package com.shpak.dynamicocean.viewmodel

import androidx.lifecycle.ViewModel
import com.shpak.dynamicocean.repository.TestRepository

class TestViewModel(
    private val testRepository: TestRepository
) : ViewModel() {

    val testMessage: String
        get() = testRepository.getTestMessage()
}
