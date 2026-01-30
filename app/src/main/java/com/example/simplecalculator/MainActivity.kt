package com.example.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplecalculator.ui.theme.SimpleCalculatorTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCalculatorTheme {
                ShowValue()
            }
        }
    }

    @Composable
    fun ShowValue() {
        var calcinput by remember { mutableStateOf("") }
        val scrollState = rememberScrollState()

        LaunchedEffect(calcinput) {
            scrollState.scrollTo(scrollState.maxValue)
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp)
                .background(color = Color.Black)
        ) {
            TextField(
                value = calcinput,
                onValueChange = { input ->
                    calcinput = input
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .horizontalScroll(scrollState, reverseScrolling = false),

                enabled = false,
                singleLine = true,
                placeholder = {
                    Text("0.0", fontSize = 40.sp)
                },
                textStyle = TextStyle(
                    fontSize = 48.sp,
                    textAlign = TextAlign.Start
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    disabledContainerColor = Color.Black,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.White,
                )
            )

            ButtonLayout(onDigitClick = { digit ->

                when (digit) {
                    "C" -> calcinput = ""
                    "=" -> {
                        try {
                            calcinput = calculateResult(calcinput)
                        } catch (e: Exception) {
                            calcinput = "Error"
                        }
                    }

                    else -> {
                        if (calcinput == "Error") calcinput = ""
                        calcinput += digit
                    }

                }

            })

        }
    }

    @Composable
    fun ButtonLayout(onDigitClick: (String) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.wrapContentSize()) {
                CalcButton("1", onDigitClick)
                CalcButton("4", onDigitClick)
                CalcButton("7", onDigitClick)
                CalcButton("+", onDigitClick)
                CalcButton("*", onDigitClick)
                CalcButton("=", onDigitClick)
            }
            Column(modifier = Modifier.wrapContentSize()) {
                CalcButton("2", onDigitClick)
                CalcButton("5", onDigitClick)
                CalcButton("8", onDigitClick)
                CalcButton("-", onDigitClick)
                CalcButton("/", onDigitClick)
            }
            Column(modifier = Modifier.wrapContentSize()) {
                CalcButton("3", onDigitClick)
                CalcButton("6", onDigitClick)
                CalcButton("9", onDigitClick)
                CalcButton("C", onDigitClick)
                CalcButton(".", onDigitClick)
            }
        }
    }


    @Composable
    fun CalcButton(
        text: String,
        onClick: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val coroutineScope = rememberCoroutineScope()
        val animationProgress = remember { Animatable(0f) }
        var tapOffset by remember { mutableStateOf(Offset.Zero) }
        val shape = RoundedCornerShape(10.dp)

        Card(
            modifier = modifier
                .size(100.dp)
                .padding(8.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = shape,
                    ambientColor = Color.White,
                    spotColor = Color.White
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            ),
            shape = shape
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                tapOffset = offset
                                coroutineScope.launch {
                                    animationProgress.snapTo(0f)
                                    animationProgress.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(1000, easing = LinearOutSlowInEasing)
                                    )
                                }
                                onClick(text)
                            }
                        )
                    }
                    .drawBehind {
                        val progress = animationProgress.value
                        if (progress > 0f && progress < 1f) {
                            val maxRadius = size.maxDimension * 1.2f

                            // Base splash fill
                            drawCircle(
                                color = Color.White.copy(alpha = 0.2f * (1f - progress)),
                                radius = maxRadius * progress,
                                center = tapOffset
                            )

                            // First wave ring
                            drawCircle(
                                color = Color.White.copy(alpha = 0.4f * (1f - progress)),
                                radius = maxRadius * progress,
                                center = tapOffset,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Second lagging wave ring
                            val progress2 = (progress - 0.2f).coerceAtLeast(0f) / 0.8f
                            if (progress2 > 0f) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.25f * (1f - progress2)),
                                    radius = maxRadius * 0.7f * progress2,
                                    center = tapOffset,
                                    style = Stroke(width = 1.5.dp.toPx())
                                )
                            }

                            // Third faint ripple
                            val progress3 = (progress - 0.4f).coerceAtLeast(0f) / 0.6f
                            if (progress3 > 0f) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.15f * (1f - progress3)),
                                    radius = maxRadius * 0.4f * progress3,
                                    center = tapOffset,
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 28.sp
                )
            }
        }
    }


}

private fun calculateResult(expression: String): String {
    if (expression.isEmpty()) return ""
    val tokens = Regex("(?<=[-+*/])|(?=[-+*/])").split(expression)

    if (tokens.size < 3) return expression // Nothing to calculate

    var result = tokens[0].toDoubleOrNull() ?: 0.0
    var i = 1
    while (i < tokens.size) {
        val op = tokens[i]
        val nextVal = tokens.getOrNull(i + 1)?.toDoubleOrNull() ?: 0.0

        when (op) {
            "+" -> result += nextVal
            "-" -> result -= nextVal
            "*" -> result *= nextVal
            "/" -> if (nextVal != 0.0) result /= nextVal else return "Error"
        }
        i += 2
    }

    return if (result % 1 == 0.0) {
        result.toInt().toString()
    } else {
        String.format("%.2f", result)
    }
}
