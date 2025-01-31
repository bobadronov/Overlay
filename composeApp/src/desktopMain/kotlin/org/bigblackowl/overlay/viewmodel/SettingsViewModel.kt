package org.bigblackowl.overlay.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.bigblackowl.overlay.data.Settings
import org.bigblackowl.overlay.storage.DataStorage

class SettingsViewModel(private val dataStorage: DataStorage) : ViewModel() {

    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings

    private val _savedIsMinutes = MutableStateFlow(false)
    val savedIsMinutes: StateFlow<Boolean> = _savedIsMinutes

    private val _singleImage = MutableStateFlow<Int?>(null)
    val singleImage: StateFlow<Int?> = _singleImage

    private val _selectedImages = MutableStateFlow<List<String>>(emptyList())
    val selectedImages: StateFlow<List<String>> = _selectedImages

    private val _animationSpeed = MutableStateFlow(5000f)
    val animationSpeed: StateFlow<Float> = _animationSpeed

    private val _animationStatic = MutableStateFlow(false)
    val isAnimationStatic: StateFlow<Boolean> = _animationStatic

    private val _windowPosition = MutableStateFlow(WindowPosition.Absolute(100.dp, 100.dp))
    val windowPosition: StateFlow<WindowPosition.Absolute> = _windowPosition

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _selectedImages.value = dataStorage.get(Settings.IMAGE_URLS)?.split(";") ?: emptyList()
        _animationSpeed.value = dataStorage.get(Settings.ANIMATION_SPEED)?.toFloatOrNull() ?: 5000f
        _singleImage.value = dataStorage.get(Settings.SINGLE_IMAGE)?.toIntOrNull() ?: 0
        _animationStatic.value = dataStorage.get(Settings.MODE_IS_STATIC)?.toBoolean() ?: false
        _savedIsMinutes.value = dataStorage.get(Settings.IS_MINUTES)?.toBoolean() ?: false

        val positionString = dataStorage.get(Settings.WINDOW_POSITION)
        _windowPosition.value = parseWindowPosition(positionString)
        println("SettingsViewModel loadSettings _windowPosition $positionString")
        println("SettingsViewModel _windowPosition  ${_windowPosition.value}")
    }

    fun onShowSettingsClicked(value: Boolean) {
        _showSettings.value = value
    }

    fun savePosition(position: WindowPosition.Absolute) {
        val positionString = "${position.x.value},${position.y.value}"
        dataStorage.save(Settings.WINDOW_POSITION, positionString)
        println("SettingsViewModel savePosition  $positionString")
        _windowPosition.value = position
    }

    private fun parseWindowPosition(positionString: String?): WindowPosition.Absolute {
        if (positionString.isNullOrEmpty()) return WindowPosition.Absolute(100.dp, 100.dp)

        return try {
            val (x, y) = positionString.split(",").map { it.toFloat().dp }
            WindowPosition.Absolute(x, y)
        } catch (e: Exception) {
            println("SettingsViewModel: Error parsing window position: $positionString, using default")
            WindowPosition.Absolute(100.dp, 100.dp)
        }
    }

    fun toggleAnimationStatic() {
        _animationStatic.value = !_animationStatic.value
        saveCurrentMode()
    }

    private fun saveCurrentMode() {
        dataStorage.save(Settings.MODE_IS_STATIC, _animationStatic.value.toString())
    }

    fun setSpeed(value: Float, isMinutes: Boolean) {
        _animationSpeed.value = value
        _savedIsMinutes.value = isMinutes
        saveAnimationSpeed()
        saveIsMinutes()
    }

    private fun saveIsMinutes() {
        dataStorage.save(Settings.IS_MINUTES, _savedIsMinutes.value.toString())
    }

    private fun saveAnimationSpeed() {
        dataStorage.save(Settings.ANIMATION_SPEED, _animationSpeed.value.toString())
    }

    fun addUrl(url: String) {
        _selectedImages.value += url
        saveUrls()
    }



    fun removeUrl(url: String) {
        _selectedImages.value -= url
        saveUrls()
    }

    private fun saveUrls() {
        dataStorage.save(Settings.IMAGE_URLS, _selectedImages.value.joinToString(";"))
    }

    fun updateUrls(newUrls: List<String>) {
        _selectedImages.value = newUrls
        saveUrls()
    }

    fun selectSingleImage(index: Int) {
        _singleImage.value = index
        dataStorage.save(Settings.SINGLE_IMAGE, index.toString())
    }
}
