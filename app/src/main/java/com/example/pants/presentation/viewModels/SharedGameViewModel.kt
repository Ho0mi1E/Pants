package com.example.pants.presentation.viewModels


import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pants.domain.entites.ColorModel
import com.example.pants.domain.useCases.CheckBoardOrderUseCase
import com.example.pants.domain.useCases.GetColorBoardUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedGameViewModel(
    private val getColorBoardUseCase: GetColorBoardUseCase,
    private val checkBoardOrderUseCase: CheckBoardOrderUseCase
) : ViewModel() {

    private val _colorBoard = MutableStateFlow(EMPTY_BOARD)
    val colorBoard: StateFlow<List<ColorModel>> = _colorBoard.asStateFlow()

    private val _currentColorName = MutableStateFlow<String?>(null)
    val currentColorName: StateFlow<String?> = _currentColorName.asStateFlow()

    private val _selectedColor = MutableStateFlow(Color.Black)
    val selectedColor: StateFlow<Color> = _selectedColor.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    init {
        initColorBoard()
    }

    fun setColorModelByName(name: String) {
        _colorBoard.value.find { it.name == name }?.let { colorModel ->
            _currentColorName.value = colorModel.name
            updateColorSettings(colorModel.guessHue ?: 0f)
        }
    }

    fun saveColor(newHue: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedColors = _colorBoard.value.toMutableList().apply {
                val index = indexOfFirst { it.name == currentColorName.value }
                if (index != -1){
                    this[index] = this[index].updateHue(newHue)
                }
            }
            _colorBoard.value = updatedColors
        }
    }

    fun updateColorSettings(hue: Float) {
        _selectedColor.value = Color.hsv(hue, 1f, 1f)
    }

    fun checkColorOrder(board: List<ColorModel>): List<ColorModel>? {
        if(checkBoardOrderUseCase(board)){
            initColorBoard()
            return null
        }
        return board.sortedBy { it.realHue }
    }


    private fun initColorBoard() {
        viewModelScope.launch(Dispatchers.IO) {
            getColorBoardUseCase(BOARD_SIZE).fold(
                onSuccess = { _colorBoard.value = it.toPersistentList() },
                onFailure = { _errorMessage.emit(it.message.orEmpty()) }
            )
        }
    }

    private companion object {
        const val BOARD_SIZE = 5
        val EMPTY_BOARD = emptyList<ColorModel>()
    }
}

