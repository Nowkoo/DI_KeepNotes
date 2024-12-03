package com.example.di_keepnotes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.di_keepnotes.ui.theme.DI_KeepNotesTheme
import kotlinx.coroutines.launch
import java.lang.Float

class MainActivity : ComponentActivity() {
    @SuppressLint("RememberReturnType")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            val notas = listOf(
                "Recordar comprar leche y avena.",
                "Escribir a Luis para confirmar si puede venir al café este sábado por la tarde. Preguntarle si prefiere a las 4 o a las 5.",
                "Pendiente: revisar los apuntes del tema 3 antes de la clase del martes. Marcar las dudas para preguntarle al profe.",
                "Claves: 🗝️ - Netflix: usuario1 | Spotify: usuario2, contraseña: *****.",
                "Inspiración para tatuaje: brújula pequeña en la muñeca.",
                "Planear el viaje: decidir si vale la pena ir en tren o en autobús. Comparar precios y tiempo de trayecto antes de reservar.",
                "Idea para mejorar el proyecto: usar ejemplos más visuales en la presentación. Incluir gráficos simples pero impactantes.",
                "Tarea: recordar sacar la basura mañana temprano, porque el camión pasa a las 8 AM. Ya se me ha olvidado dos veces esta semana.",
                "Ver el capítulo 4 de esa serie nueva.",
                "Llamar a Laura antes del mediodía.",
                "Verificar cita médica para el jueves.",
                "Comprar un regalo para el cumpleaños de Sofía. Tal vez una planta pequeña, como un cactus bonito, o una taza personalizada.",
            )

            DI_KeepNotesTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    floatingActionButton = { FabBordeAnimado() },
                    topBar = {
                        LargeTopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                                navigationIconContentColor = MaterialTheme.colorScheme.primary,
                                actionIconContentColor = MaterialTheme.colorScheme.primary,
                                scrolledContainerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            title = {
                                Text(
                                    "KeepNotes",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Localized description"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { /* do something */ }) {
                                    CampanaAnimada()
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Localized description"
                                    )
                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)
                    Column (
                        modifier = modifier.fillMaxSize()
                    ) {
                        LazyColumn {
                            items(notas) { nota ->
                                var changeSize by rememberSaveable { mutableStateOf(false) }
                                val elevation by animateDpAsState(if(changeSize) 1.dp else 16.dp)
                                val scale by animateFloatAsState(if (changeSize) 1f else 0.9f)
                                val coroutineScope = rememberCoroutineScope()
                                val offsetX = remember { Animatable(0f) }
                                val offsetY = remember { Animatable(0f) }

                                Card (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .scale(scale)
                                        .clickable {
                                            changeSize = !changeSize
                                        }
                                        .offset {
                                            IntOffset (
                                                offsetX.value.toInt(),
                                                offsetY.value.toInt()
                                            )
                                        }
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                onDragEnd = {
                                                    coroutineScope.launch {
                                                        offsetX.animateTo(
                                                            targetValue = 0f,
                                                            animationSpec = tween(
                                                                durationMillis = 1000,
                                                                delayMillis = 0
                                                            )
                                                        )
                                                    }
                                                    coroutineScope.launch {
                                                        offsetY.animateTo(
                                                            targetValue = 0f,
                                                            animationSpec = tween(
                                                                durationMillis = 3000,
                                                                delayMillis = 0
                                                            )
                                                        )
                                                    }
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    coroutineScope.launch {
                                                        offsetY.snapTo(offsetY.value + dragAmount.y)
                                                    }
                                                    coroutineScope.launch {
                                                        offsetX.snapTo(offsetX.value + dragAmount.x)
                                                    }
                                                }
                                            )
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    elevation = CardDefaults.cardElevation(elevation)

                                ) {
                                    Text(
                                        text = nota,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampanaAnimada() {
    val value by rememberInfiniteTransition().animateFloat(
        initialValue = 25f,
        targetValue = -25f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Icon(
        imageVector = Icons.Filled.Notifications,
        contentDescription = "Localized description",
        modifier = Modifier
            .graphicsLayer(
                transformOrigin = TransformOrigin(
                    pivotFractionX = 0.5f,
                    pivotFractionY = 0f
                ),
                rotationZ = value
            ),
//        tint = if (value > 0) Color.White else Color.LightGray
    )
}

@Composable
fun FabBordeAnimado() {
    val value by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    )

    val colors = listOf(
        Color(0xFF405DE6),
        Color(0xFFC13584),
        Color(0xFFFD1D1D),
        Color(0xFFFFDC80)
    )
    var gradientBrush by remember {
        mutableStateOf(
            Brush.horizontalGradient(
                colors = colors,
                startX = -10.0f,
                endX = 400.0f,
                tileMode = TileMode.Repeated
            )
        )
    }

    FloatingActionButton(
        onClick = {},
        containerColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .drawBehind {
            rotate(value) {
                drawCircle(
                    gradientBrush, style = Stroke(width = 24.dp.value)
                )
            }
        },
        shape = CircleShape
    ) {
        Icon(Icons.Filled.Create, "Floating action button.")
    }
}