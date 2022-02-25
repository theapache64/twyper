package com.github.theapache64.twyper

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * To control individual card
 */
interface FlipCardController: CardController {
    val cardFlipRotation: Float
    val cardFrontAlpha: Float
    val cardBackAlpha: Float
    var state: FlipState

    fun isCardRotated(): Boolean
    fun setCardFlipState()
    fun flipToFront()
    fun flipToBack()
}

@Composable
fun rememberFlipCardController(): FlipCardController {
    val scope = rememberCoroutineScope()
    val screenWidth =
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    return remember {
        val swipeX = Animatable(0f)
        val swipeY = Animatable(0f)
        val flipRotation = Animatable(0f)
        val frontAlpha = Animatable(1f)
        val backAlpha = Animatable(0f)
        val cardState = FlipState.FRONT
        FlipCardControllerImpl(swipeX, swipeY, flipRotation, frontAlpha, backAlpha, cardState, scope, screenWidth)
    }
}

class FlipCardControllerImpl(
    swipeX: Animatable<Float, AnimationVector1D>,
    swipeY: Animatable<Float, AnimationVector1D>,
    private val flip: Animatable<Float, AnimationVector1D>,
    private val frontAlpha: Animatable<Float, AnimationVector1D>,
    private val backAlpha: Animatable<Float, AnimationVector1D>,
    private var cardState: FlipState,
    private val scope: CoroutineScope,
    screenWidth: Float
) : FlipCardController, CardControllerImpl(swipeX, swipeY, scope, screenWidth) {

    companion object {
        private const val FLIP_ROTATION_DURATION_IN_MILLIS = 500
        private const val CARD_FRONT_ALPHA_DURATION_IN_MILLIS = 400
        private const val CARD_REVERSE_ALPHA_DURATION_IN_MILLIS = 200
        private const val INITIAL_FLIP_ANGLE = 0f
        private const val FINAL_FLIP_ANGLE = 180f
    }

    override val cardFlipRotation: Float
        get() = flip.value

    override val cardFrontAlpha: Float
        get() = frontAlpha.value

    override val cardBackAlpha: Float
        get() = backAlpha.value

    override var state: FlipState = cardState

    override fun isCardRotated(): Boolean {
        return cardState == FlipState.BACK
    }

    override fun setCardFlipState() {
        if(!isCardRotated()){
            flipToBack()
        }else{
            flipToFront()
        }
    }

    override fun flipToFront() {
        scope.launch {
            cardState = FlipState.FRONT
            launch { flip.animateTo(INITIAL_FLIP_ANGLE, tween(FLIP_ROTATION_DURATION_IN_MILLIS)) }
            launch { frontAlpha.animateTo(1f, tween(CARD_FRONT_ALPHA_DURATION_IN_MILLIS)) }
            launch { backAlpha.animateTo(0f, tween(CARD_REVERSE_ALPHA_DURATION_IN_MILLIS)) }
        }
    }

    override fun flipToBack() {
        scope.launch {
            cardState = FlipState.BACK
            launch { flip.animateTo(FINAL_FLIP_ANGLE, tween(FLIP_ROTATION_DURATION_IN_MILLIS)) }
            launch { frontAlpha.animateTo(0f, tween(CARD_REVERSE_ALPHA_DURATION_IN_MILLIS)) }
            launch { backAlpha.animateTo(1f, tween(CARD_FRONT_ALPHA_DURATION_IN_MILLIS)) }
        }
    }
}