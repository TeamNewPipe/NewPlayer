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

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun supportsPip(context: Context) =
    if(Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
        val isSupported by lazy {
            val pm = context.packageManager
            pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        }
        isSupported
    } else {
        false
    }
