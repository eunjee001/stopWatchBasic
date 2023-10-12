
package com.kkyoungs.stopwatch

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kkyoungs.stopwatch.ui.theme.StopWatchTheme
import java.util.*
import kotlin.concurrent.timer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<MainViewModel>()

            val sec = viewModel.sec.value
            val milli = viewModel.milli.value
            val isRunning = viewModel.isRunning.value
            val lapTimes = viewModel.lapTimes.value

            MainScreen(
                sec = sec,
                milli = milli,
                isRunning = isRunning,
                lapTimes = lapTimes,
                onReset = { viewModel.reset() },
                onToggle = { running ->
                    if (running) {
                        viewModel.pause()
                    } else {
                        viewModel.start()
                    }
                },
                onLapTime = { viewModel.recordLapTime() })
        }
    }


    //기능
    class MainViewModel : ViewModel() {

        private var time = 0
        private var timerTask: Timer? = null

        private val _isRunning = mutableStateOf(false)
        val isRunning: State<Boolean> = _isRunning

        private val _sec = mutableStateOf(0)
        val sec: State<Int> = _sec

        private val _milli = mutableStateOf(0)
        val milli: State<Int> = _milli

        private val _lapTimes = mutableStateOf(mutableListOf<String>())
        val lapTimes: State<List<String>> = _lapTimes

        private var lap = 1

        fun start() {
            _isRunning.value = true

            timerTask = timer(period = 10) {
                time++
                _sec.value = time / 100
                _milli.value = time % 100
            }
        }

        fun pause() {
            _isRunning.value = false
            timerTask?.cancel()
        }

        fun reset() {
            timerTask?.cancel()
            time = 0
            _isRunning.value = false
            _sec.value = 0
            _milli.value = 0

            _lapTimes.value.clear()
            lap = 1

        }

        fun recordLapTime() {
            _lapTimes.value.add(0, "$lap Lap : ${sec.value}. ${milli.value}")
            lap++
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(
        sec: Int,
        milli: Int,
        isRunning: Boolean,
        lapTimes: List<String>,
        onReset: () -> Unit,
        onToggle: (Boolean) -> Unit,
        onLapTime: () -> Unit,
    ) {
        Scaffold(
            topBar = { MediumTopAppBar(colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer, titleContentColor =  MaterialTheme.colorScheme.primary), title = { Text("stopWatch") }) }
        ) {innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Spacer(modifier = Modifier.height(200.dp))
                Row() {
                    Text(text = "$sec", fontSize = 100.sp)
                    Text(text = "$milli")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    lapTimes.forEach { lapTimes ->
                        Text(text = lapTimes)
                    }

                }
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    FloatingActionButton(
                        onClick = { onReset() },
                        Modifier.background(color = Color.Red)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_refresh_24),
                            contentDescription = "reset"
                        )

                    }
                    FloatingActionButton(
                        onClick = { onToggle(isRunning) },
                        Modifier.background(color = Color.Green)
                    ) {
                        Image(
                            painter = painterResource(id = if (isRunning) R.drawable.baseline_pause_circle_outline_24 else R.drawable.baseline_play_circle_outline_24),
                            contentDescription = "start/pause"
                        )

                    }
                    Button(onClick = {
                        onLapTime()
                    }) {
                        Text(text = "랩타임")

                    }


                }
            }
        }

    }
}
