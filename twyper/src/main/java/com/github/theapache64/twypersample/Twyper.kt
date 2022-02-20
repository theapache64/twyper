package com.github.theapache64.twyper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput

enum class SwipedOutDirection {
    LEFT, RIGHT
}

@Composable
inline fun <reified T> Twyper(
    items: List<T>,
    onItemRemoved: (T, SwipedOutDirection) -> Unit,
    onEmpty: () -> Unit = {},
    twyperController: TwyperController = rememberTwyperController(),
    modifier: Modifier = Modifier,
    crossinline renderItem: @Composable (T) -> Unit
) {
    Box(modifier = modifier) {
        // Rendering only the first 2 items at a time
        val list = items.take(2).reversed()
        list.forEachIndexed { index, item ->
            key(item) {
                val cardController = rememberCardController()
                if (index == list.lastIndex) {
                    twyperController.currentCardController = cardController
                }
                if (!cardController.isCardOut()) {
                    val paddingTop by animateFloatAsState(targetValue = (index * 40f))
                    Card(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        cardController.onDragEnd()
                                    },
                                    onDragCancel = {
                                        cardController.onDragCancel()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consumePositionChange()
                                        cardController.onDrag(dragAmount)
                                    }
                                )
                            }
                            .graphicsLayer(
                                translationX = cardController.cardX,
                                translationY = cardController.cardY + paddingTop,
                                rotationZ = cardController.rotation,
                            )
                    ) {
                        renderItem(item)
                    }
                } else {
                    cardController.swipedOutDirection?.let { outDirection ->
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
