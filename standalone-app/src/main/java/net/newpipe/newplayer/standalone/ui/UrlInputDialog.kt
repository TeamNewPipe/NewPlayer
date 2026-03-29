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

package net.newpipe.newplayer.standalone.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import net.newpipe.newplayer.standalone.R
import net.newpipe.newplayer.standalone.ui.theme.StandaloneTheme

fun isValidStreamUrl(url: String): Boolean {
    val parsed = try {
        java.net.URL(url.trim())
    } catch (_: java.net.MalformedURLException) {
        return false
    }
    val scheme = parsed.protocol?.lowercase()
    return scheme == "http" || scheme == "https"
}

@Composable
fun UrlInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var url by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val onSubmit = {
        if (isValidStreamUrl(url)) {
            showError = false
            onConfirm(url)
        } else {
            showError = true
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.enter_stream_url)) },
        text = {
            OutlinedTextField(
                value = url,
                onValueChange = {
                    url = it
                    showError = false
                },
                label = { Text(stringResource(R.string.url_hint)) },
                isError = showError,
                supportingText = if (showError) {
                    { Text(stringResource(R.string.invalid_url_error), color = MaterialTheme.colorScheme.error) }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSubmit() },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmit() },
                enabled = url.isNotBlank(),
            ) {
                Text(stringResource(R.string.play))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Preview(name = "UrlInputDialog – portrait", widthDp = 400, heightDp = 800, showBackground = true)
@Composable
private fun UrlInputDialogPortraitPreview() {
    StandaloneTheme {
        UrlInputDialog(onDismiss = {}, onConfirm = {})
    }
}

@Preview(name = "UrlInputDialog – landscape", widthDp = 800, heightDp = 400, showBackground = true)
@Composable
private fun UrlInputDialogLandscapePreview() {
    StandaloneTheme {
        UrlInputDialog(onDismiss = {}, onConfirm = {})
    }
}