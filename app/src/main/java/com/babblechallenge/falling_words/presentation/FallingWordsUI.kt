package com.babblechallenge.falling_words.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import com.babblechallenge.R

@Preview
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
fun ErrorScreen(
    retryButtonListener: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier
                .width(64.dp)
                .height(64.dp),
            onClick = retryButtonListener,
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
        ) {
            Icon(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp),
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun InProgressScreen(
    progress: Float = 0.01f,
    score: String = "Score: 0",
    originalWord: String = "Hello",
    translatedWord: String = "World",
    positiveButtonListener: () -> Unit = {},
    negativeButtonListener: () -> Unit = {},
    interactionSource: MutableInteractionSource = MutableInteractionSource()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(false, color = Color.Green),
                onClick = {},
                enabled = true
            )
    ) {
        WordsContent(
            modifier = Modifier.weight(1f, true),
            progress = progress,
            originalWord = originalWord,
            translatedWord = translatedWord,
        )
        Controller(
            score = score,
            positiveButtonListener = positiveButtonListener,
            negativeButtonListener = negativeButtonListener
        )
    }
}

@Preview
@Composable
fun FinishScreen(
    score: String = "Score: 0",
    newRoundButtonListener: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = score,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Button(modifier = Modifier.padding(top = 16.dp), onClick = newRoundButtonListener) {
                Text(text = "One more round")
            }
        }

    }
}

@Preview
@Composable
fun Controller(
    modifier: Modifier = Modifier,
    score: String = "Score: 0",
    positiveButtonListener: () -> Unit = {},
    negativeButtonListener: () -> Unit = {},
) {
    Row(
        modifier = modifier.then(
            Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            modifier = Modifier
                .width(64.dp)
                .height(64.dp),
            contentPadding = PaddingValues(18.dp),
            onClick = positiveButtonListener,
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Outlined.Check,
                contentDescription = null
            )

        }

        Text(
            modifier = Modifier.weight(1f, true),
            text = score,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Button(
            modifier = Modifier
                .width(64.dp)
                .height(64.dp),
            contentPadding = PaddingValues(18.dp),
            onClick = negativeButtonListener,
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Outlined.Close,
                contentDescription = null
            )

        }
    }
}

@OptIn(ExperimentalMotionApi::class)
@Preview
@Composable
fun WordsContent(
    modifier: Modifier = Modifier,
    progress: Float = 0.01f,
    originalWord: String = "Hello",
    translatedWord: String = "World"
) {
    val context = LocalContext.current
    val motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }
    MotionLayout(
        modifier = modifier.then(Modifier.fillMaxSize()),
        motionScene = MotionScene(content = motionSceneContent),
        progress = progress
    ) {
        Text(
            modifier = Modifier
                .layoutId("original_text")
                .fillMaxWidth(),
            text = originalWord,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )
        Text(
            modifier = Modifier
                .layoutId("translated_text")
                .fillMaxWidth(),
            text = translatedWord,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )
    }
}