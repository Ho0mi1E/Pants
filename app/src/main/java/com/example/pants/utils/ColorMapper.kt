package com.example.pants.utils

import com.example.pants.data.dto.ColorResponse
import com.example.pants.domain.entites.ColorModel

fun ColorResponse.toColorModel(): ColorModel {
    return ColorModel(
        name = this.name.value,
        realHue = this.hsv.fraction.h,
        guessHue = null,
        saturation = this.hsv.fraction.s,
        value = this.hsv.fraction.v,
    )
}
