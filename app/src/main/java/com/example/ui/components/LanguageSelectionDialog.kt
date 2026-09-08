package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldDark
import com.example.ui.theme.TurquoisePrimary
import com.example.util.AppLanguage
import com.example.util.LocalAppStrings

@Composable
fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismissRequest: () -> Unit
) {
    val appStrings = LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.testTag("dialog_language_selection"),
        icon = {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = TurquoisePrimary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = appStrings.languageLabel,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppLanguage.entries.forEach { language ->
                    val isSelected = language == currentLanguage
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageSelected(language)
                                onDismissRequest()
                            }
                            .testTag("lang_option_${language.code}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) TurquoisePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) TurquoisePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (language) {
                                        AppLanguage.PERSIAN -> "🇮🇷"
                                        AppLanguage.ARABIC -> "🇸🇦"
                                        AppLanguage.ENGLISH -> "🇬🇧"
                                    },
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = language.nativeName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isSelected) TurquoisePrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = language.displayName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = TurquoisePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("btn_close_lang_dialog")
            ) {
                Text(
                    text = appStrings.btnDismiss,
                    color = GoldDark,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
