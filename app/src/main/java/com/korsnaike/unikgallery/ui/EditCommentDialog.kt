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
        title = { Text("Редактировать комментарий") },
        text = {
            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                label = { Text("Комментарий") },
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
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отменить")
            }
        }
    )
}