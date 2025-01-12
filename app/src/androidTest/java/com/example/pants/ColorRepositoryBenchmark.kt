package com.example.pants


import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pants.data.api.ColorApiService
import com.example.pants.data.repositoryImpl.ColorRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(AndroidJUnit4::class)
class ColorRepositoryBenchmark {


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


    @Test
    fun benchmarkGetRandomColors() = runBlocking {
        val repeatTime = 5
        var total = 0L


        repeat(repeatTime + 1) {
            val count = 5
            val startTime = System.nanoTime()
            val result = colorRepository.getRandomColors(count)
            val totalTime = (System.nanoTime() - startTime) / 1000000

            assertTrue(result.isSuccess)
            val colors = result.getOrNull() ?: emptySet()
            assertTrue(colors.size <= count)
            if (it != 1) {
                total += totalTime
                Log.i("Bench", "$it For $count queries time is $totalTime ms")
            }
        }
        Log.i("Top Bench", " Average time is ${total / repeatTime} ms")
        assertTrue(true)
    }
}


