package org.bigblackowl.overlay.storage

interface DataStorage {
    fun save(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
    fun clear()
}