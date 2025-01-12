package com.example.pants

import com.example.pants.data.api.ColorApiService
import com.example.pants.data.repositoryImpl.ColorRepositoryImpl
import com.example.pants.domain.entites.ColorModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class RepositoryImplTest {


    private lateinit var colorRepository: ColorRepositoryImpl


    @Before
    fun setUp() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.thecolorapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ColorApiService::class.java)
        colorRepository = ColorRepositoryImpl(apiService)
    }

    private suspend fun getColors(count: Int): Result<Set<ColorModel>> {
        return colorRepository.getRandomColors(count)
    }

    private fun checkSuccessAndGetSetColorModel(response: Result<Set<ColorModel>>): Set<ColorModel>? {
        assertTrue(response.isSuccess)
        val colors = response.getOrNull()
        assertNotNull(colors)
        return colors
    }

    @Test
    fun `getRandomColors returns unique colors and Set(ColorModel)`() = runTest {

        val count = 5

        val colorList = getColors(count)

        val colors = checkSuccessAndGetSetColorModel(colorList)
        assertTrue(colors is Set<ColorModel>)
        assertEquals(count, colors!!.size)
    }

    @Test
    fun `getRandomColors does not return colors below saturation and brightness thresholds and has unique colors`() =
        runTest {

            val count = 5


            val colorList = getColors(count)


            val colors = checkSuccessAndGetSetColorModel(colorList)

            assertTrue(colors?.distinct()?.size == colors?.size)
            for (color in colors!!) {
                assertTrue(color.saturation * 100 > 20)
                assertTrue(color.value * 100 > 30)
            }
        }

    @Test
    fun `getRandomColors does not return common colors`() = runTest {

        val count = 5

        val colorList = getColors(count)

        val colors = checkSuccessAndGetSetColorModel(colorList)

        for (color in colors!!) {
            assertTrue(color.name.lowercase(Locale.getDefault()) !in COMMON_USE_NAMES)
        }
    }

    private val COMMON_USE_NAMES = setOf(
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
