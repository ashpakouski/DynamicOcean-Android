package com.shpak.dynamicocean.old.viewmodel

import androidx.lifecycle.ViewModel
import com.shpak.dynamicocean.old.repository.TestRepository

class TestViewModel(
    private val testRepository: TestRepository
) : ViewModel() {

    val testMessage: String
        get() = testRepository.getTestMessage()
}
