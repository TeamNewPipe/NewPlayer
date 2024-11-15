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

package net.newpipe.newplayer.ui.common

import android.content.Context
import android.widget.Toast
import net.newpipe.newplayer.R

/**
 * This piece of code should eventually vanish once NewPlayer is sufficiently done ;-)
 *
 * @hide
 */
internal fun showNotYetImplementedToast(context: Context) {
    Toast.makeText(
        context,
        context.resources.getString(R.string.function_not_yet_implemented_toast),
        Toast.LENGTH_SHORT
    ).show()
}