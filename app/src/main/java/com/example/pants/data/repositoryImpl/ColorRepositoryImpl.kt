package com.example.pants.data.repositoryImpl

import com.example.pants.data.api.ColorApiService
import com.example.pants.domain.entites.ColorModel
import com.example.pants.domain.repository.ColorRepository
import com.example.pants.utils.generateRandomColor
import com.example.pants.utils.toColorModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Locale

class ColorRepositoryImpl(
    private val apiService: ColorApiService,
) : ColorRepository {

    override suspend fun getRandomColors(count: Int): Result<Set<ColorModel>> = runCatching {
        val colorList = mutableSetOf<ColorModel>()
        val mutex = Mutex()

        withContext(Dispatchers.IO) {
            coroutineScope {
                repeat(count) {
                    launch {
                        while (true) {
                            val color = apiService.getColor(generateRandomColor()).toColorModel()

                            val isCommonName = color.name.lowercase(Locale.getDefault()) in COMMON_USE_NAMES

                            if (!isCommonName) {
                                mutex.withLock {
                                    if (color !in colorList) {
                                        colorList.add(color)
                                        return@launch
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        colorList
    }

    private companion object {
        val COMMON_USE_NAMES = setOf(
            "beige",
            "black",
            "blue violet",
            "blue",
            "brown",
            "crimson",
            "cyan",
            "gold",
            "gray",
            "green",
            "indigo",
            "khaki",
            "lavender",
            "lime green",
            "magenta",
            "maroon",
            "navy blue",
            "olive",
            "orange",
            "pink",
            "plum",
            "purple",
            "red",
            "salmon",
            "silver",
            "sky blue",
            "teal",
            "violet",
            "white",
            "yellow",
        )
    }
}
