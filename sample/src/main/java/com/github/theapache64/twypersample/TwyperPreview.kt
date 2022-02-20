package com.github.theapache64.twypersample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun TwyperPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val items = remember { mutableStateListOf(*('A'..'Z').toList().toTypedArray()) }
        val twyperController = rememberTwyperController()
        Twyper(
            items = items,
            twyperController = twyperController,
            onItemRemoved = { item, direction ->
                println("Item removed: $item -> $direction")
                items.remove(item)
            },
            onEmpty = {
                println("End reached")
            }
        ) { item ->
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                rememberRandomColor(),
                                rememberRandomColor(),
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$item", fontSize = 200.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(30.dp),
        ) {

            IconButton(onClick = {
                twyperController.swipeLeft()
            }) {
                Text(text = "❌", fontSize = 30.sp)
            }

            IconButton(onClick = {
                twyperController.swipeRight()
            }) {
                Text(text = "✅", fontSize = 30.sp)
            }
        }
    }
}