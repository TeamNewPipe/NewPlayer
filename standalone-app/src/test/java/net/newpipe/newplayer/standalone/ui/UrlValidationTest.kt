/* NewPlayer
 *
 * @author Christian Schabesberger
 *
 * Copyright (C) NewPipe e.V. 2026 <code(at)newpipe-ev.de>
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

package net.newpipe.newplayer.standalone.ui

import org.junit.Test
import org.junit.Assert.*

class UrlValidationTest {

    // --- Valid HTTP/HTTPS URLs ---

    @Test
    fun `valid http url`() {
        assertTrue(isValidStreamUrl("http://example.com/stream"))
    }

    @Test
    fun `valid https url`() {
        assertTrue(isValidStreamUrl("https://example.com/stream"))
    }

    @Test
    fun `valid https url with port`() {
        assertTrue(isValidStreamUrl("https://example.com:8080/stream"))
    }

    // --- Invalid URLs ---

    @Test
    fun `empty string is invalid`() {
        assertFalse(isValidStreamUrl(""))
    }

    @Test
    fun `blank string is invalid`() {
        assertFalse(isValidStreamUrl("   "))
    }

    @Test
    fun `plain text is invalid`() {
        assertFalse(isValidStreamUrl("not a url at all"))
    }

    @Test
    fun `ftp scheme is invalid`() {
        assertFalse(isValidStreamUrl("ftp://example.com/stream"))
    }

    @Test
    fun `rtsp scheme is invalid`() {
        assertFalse(isValidStreamUrl("rtsp://example.com/stream"))
    }

    @Test
    fun `rtmp scheme is invalid`() {
        assertFalse(isValidStreamUrl("rtmp://example.com/live/stream"))
    }

    @Test
    fun `file scheme is invalid`() {
        assertFalse(isValidStreamUrl("file:///sdcard/video"))
    }

    @Test
    fun `content uri is invalid`() {
        assertFalse(isValidStreamUrl("content://media/external/video/1234"))
    }

    @Test
    fun `missing scheme is invalid`() {
        assertFalse(isValidStreamUrl("example.com/stream"))
    }

    @Test
    fun `missing host is invalid`() {
        assertFalse(isValidStreamUrl("https://"))
    }

    @Test
    fun `only scheme colon slash is invalid`() {
        assertFalse(isValidStreamUrl("http:/"))
    }

    @Test
    fun `javascript scheme is invalid`() {
        assertFalse(isValidStreamUrl("javascript:alert(1)"))
    }

    @Test
    fun `data uri is invalid`() {
        assertFalse(isValidStreamUrl("data:text/html,<h1>hi</h1>"))
    }

    @Test
    fun `mailto is invalid`() {
        assertFalse(isValidStreamUrl("mailto:someone@example.com"))
    }

    @Test
    fun `single word is invalid`() {
        assertFalse(isValidStreamUrl("stream"))
    }

    @Test
    fun `number is invalid`() {
        assertFalse(isValidStreamUrl("12345"))
    }

    @Test
    fun `local path is invalid`() {
        assertFalse(isValidStreamUrl("/var/media/video"))
    }

    @Test
    fun `windows path is invalid`() {
        assertFalse(isValidStreamUrl("C:\\Users\\video"))
    }
}