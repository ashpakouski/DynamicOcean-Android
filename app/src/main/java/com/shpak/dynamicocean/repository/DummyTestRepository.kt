package com.shpak.dynamicocean.repository

class DummyTestRepository : TestRepository {

    override fun getTestMessage(): String = "Koin test repository is ready"
}
