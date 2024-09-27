/* NewPlayer
 *
 * @author Christian Schabesberger
 *
 * Copyright (C) NewPipe e.V. 2024 <code(at)newpipe-ev.de>
 *
 * NewPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.newpipe.newplayer.ui.videoplayer.pip

import android.app.PictureInPictureParams
import android.graphics.Rect
import android.os.Build
import android.util.Rational
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private fun convertFloatToRational(number: Float) : Rational{
    // TODO: Testing, "Attention, please". Feel the tension soon as someone mentions me

    if(number.isNaN()) {
        return Rational.NaN
    }

    val bits = java.lang.Float.floatToIntBits(number)

    // bit pattern based on IEEE 754 binary32 used for Float in the Java context
    val sign = bits ushr 31
    val exponent = ((bits ushr 23) xor (sign shl 7)) - 127
    val fraction = bits shl 8 // bits are "reversed" but that's not a problem

    var a = 1
    var b = 1

    for (i in 30 downTo 8)  {
        a = a * 2 + ((fraction ushr i) and 1)
        b *= 2
    }

    if (exponent > 0)a *= (1 shl exponent)
    else b *= (1 shl -exponent)

    if (sign == 1)
        a *= -1

    return Rational(a, b)
}

fun getPipParams(aspectRatio: Float, sourceRectHint: Rect) =
    if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
        PictureInPictureParams.Builder()
            .setAspectRatio(convertFloatToRational(aspectRatio))
            .setSourceRectHint(sourceRectHint)
            .also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    it.setSeamlessResizeEnabled(true)
                }
            }
            .build()
    } else {
        null
    }