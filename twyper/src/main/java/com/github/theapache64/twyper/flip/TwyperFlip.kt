package com.github.theapache64.twyper.flip

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import com.github.theapache64.twyper.SwipedOutDirection

enum class FlipState {
    FRONT, BACK
}

@Composable
inline fun <reified T> TwyperFlip(
    items: List<T>,
    onItemRemoved: (T, SwipedOutDirection) -> Unit,
    onEmpty: () -> Unit = {},
    twyperFlipController: TwyperFlipController = rememberTwyperFlipController(),
    stackCount: Int = 2,
    paddingBetweenCards: Float = 40f,
    modifier: Modifier = Modifier,
    cardModifier: () -> Modifier,
    crossinline front: @Composable (T) -> Unit,
    crossinline back: @Composable (T) -> Unit
) {

    Box(modifier = Modifier) {
        val list = items.take(stackCount).reversed()
        list.forEachIndexed { index, item ->
            key(item) {
                val boxModifier by remember { mutableStateOf(cardModifier.invoke()) }
                val flipCardController: FlipCardController = rememberFlipCardController()

                if (index == list.lastIndex) {
                    twyperFlipController.currentCardController = flipCardController
                }

                if (!flipCardController.isCardOut()) {
                    val paddingTop by animateFloatAsState(targetValue = (index * paddingBetweenCards))

                    Card(
                        modifier = modifier
                            .clickable {
                                flipCardController.setCardFlipState()
                            }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        flipCardController.onDragEnd()
                                    },
                                    onDragCancel = {
                                        flipCardController.onDragCancel()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consumePositionChange()
                                        flipCardController.onDrag(dragAmount)
                                    }
                                )
                            }
                            .graphicsLayer {
                                translationX = flipCardController.cardX
                                translationY = flipCardController.cardY + paddingTop
                                rotationZ = flipCardController.rotation
                                rotationY = flipCardController.cardFlipRotation
                                cameraDistance = 12 * density
                            }
                    ) {
                        Box(
                            modifier = boxModifier
                                .zIndex(1f - flipCardController.cardBackAlpha)
                                .graphicsLayer {
                                    alpha = flipCardController.cardFrontAlpha
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            front(item)
                        }

                        Box(
                            modifier = boxModifier
                                .zIndex(1f - flipCardController.cardFrontAlpha)
                                .graphicsLayer {
                                    alpha = flipCardController.cardBackAlpha
                                    rotationY = -180f
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            back(item)
                        }

                    }
                } else {
                    flipCardController.swipedOutDirection?.let { outDirection ->
                        onItemRemoved(item, outDirection)
                        if (items.isEmpty()) {
                            onEmpty()
                        }
                    }
                }
            }
        }
    }
}

