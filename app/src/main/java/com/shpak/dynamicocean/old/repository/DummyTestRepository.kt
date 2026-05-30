package com.shpak.dynamicocean.old.repository

class DummyTestRepository : TestRepository {

    override fun getTestMessage(): String = "Koin test repository is ready"
}
