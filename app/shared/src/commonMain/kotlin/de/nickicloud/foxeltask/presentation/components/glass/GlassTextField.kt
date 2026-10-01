package de.nickicloud.foxeltask.presentation.components.glass

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.nickicloud.foxeltask.theme.FoxelColors
import de.nickicloud.foxeltask.theme.FoxelShapes
import de.nickicloud.foxeltask.theme.FoxelThemeTokens
import de.nickicloud.foxeltask.theme.neonGlow

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    shape: Shape = FoxelShapes.ShapeSm,
    activeGlowColor: Color = FoxelColors.NeonCyan
) {
    var isFocused by remember { mutableStateOf(false) }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) activeGlowColor else FoxelThemeTokens.glass.borderDefault,
        animationSpec = tween(durationMillis = 200)
    )

    val animatedBgColor by animateColorAsState(
        targetValue = if (isFocused) FoxelThemeTokens.glass.surfaceFocused else FoxelThemeTokens.glass.surfaceDefault,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .then(
                if (isFocused) {
                    Modifier.neonGlow(color = activeGlowColor, radius = 8.dp, shape = shape)
                } else Modifier
            )
            .clip(shape)
            .background(animatedBgColor)
            .border(
                width = if (isFocused) 1.5.dp else 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        animatedBorderColor,
                        animatedBorderColor.copy(alpha = animatedBorderColor.alpha * 0.4f)
                    )
                ),
                shape = shape
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(10.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = FoxelColors.TextMutedDark,
                        fontSize = 14.sp
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
                    textStyle = TextStyle(
                        color = FoxelColors.TextPrimaryDark,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(FoxelColors.NeonCyan),
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    singleLine = singleLine,
                    maxLines = maxLines
                )
            }

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(10.dp))
                trailingIcon()
            }
        }
    }
}
