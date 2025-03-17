package com.korsnaike.unikgallery.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.korsnaike.unikgallery.R
import com.korsnaike.unikgallery.data.Comment

@Composable
fun EditCommentDialog(
    comment: Comment,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(comment.text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.edit_comment)) },
        text = {
            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                label = { Text(stringResource(id = R.string.comment)) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (editedText.isNotBlank()) {
                        onSave(editedText)
                    }
                }
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}