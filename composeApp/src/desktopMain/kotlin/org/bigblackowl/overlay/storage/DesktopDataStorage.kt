package org.bigblackowl.overlay.storage

import org.bigblackowl.overlay.data.Settings
import java.io.File
import java.util.Properties


class DesktopDataStorage : DataStorage {
    private val file = File(Settings.APP_DATA_STORAGE)
    private val properties = Properties()

    init {
        if (file.exists()) {
            file.inputStream().use { properties.load(it) }
        }
    }

    override fun save(key: String, value: String) {
        properties[key] = value
        file.outputStream().use { properties.store(it, null) }
    }

    override fun get(key: String): String? {
        return properties.getProperty(key)
    }

    override fun remove(key: String) {
        properties.remove(key)
        file.outputStream().use { properties.store(it, null) }
    }

    override fun clear() {
        properties.clear()
        file.outputStream().use { properties.store(it, null) }
    }
}