@file:OptIn(ExperimentalMaterial3Api::class)

package com.iliano.chrono_calcul_mobile.ui.screens.calculator

import android.app.Application
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.format.DateFormat
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.iliano.chrono_calcul_mobile.core.Constants
import com.iliano.chrono_calcul_mobile.core.toFormattedString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.time.Duration

class CalculatorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        CalculatorState(
            timePickerState = createTimePickerState()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _eventsFlow = MutableSharedFlow<ScreenEvent>()
    val eventsFlow = _eventsFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            while (true) {
                updateTextResult()
                delay(Constants.UPDATE_DELAY)
            }
        }
    }

    fun onOffsetToggle(enable: Boolean) {
        _uiState.update {
            _uiState.value.copy(
                checkBoxState = enable,
                        calculation = _uiState.value.calculation.copy(applyOffset = enable)
            )
        }
        updateTextResult()
    }

    fun showTimePicker() {
    val currentChosenTime = _uiState.value.calculation.getTargetTime()
        _uiState.update {
            _uiState.value.copy(
                showTimePicker = true
                , timePickerState = TimePickerState(
                    is24Hour = DateFormat.is24HourFormat(application.applicationContext),
                    initialHour = currentChosenTime.hour,
                    initialMinute = currentChosenTime.minute
                )
            )
        }
    }

    fun hideTimePicker() {
        viewModelScope.launch {
            _uiState.update {
                _uiState.value.copy(
                    showTimePicker = false
                )
            }
            val timePickerState = _uiState.value.timePickerState
            _uiState.value.calculation.setTargetTime(timePickerState.hour, timePickerState.minute)

            if (_uiState.value.calculation.duration == Duration.ZERO) {
                _eventsFlow.emit(ScreenEvent.EnteredTimeInvalid)
            }
            updateTextResult()
        }
    }

    fun vibrateDevice(context: Context, type: VibrationTypes) {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
             context.getSystemService(VibratorManager::class.java)

            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(
                when (type) {
                    VibrationTypes.Short -> {
                        VibrationEffect.createOneShot(
                            Constants.Vibrations.SMALL_DURATION,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    }

                    VibrationTypes.Tick -> {
                        VibrationEffect.createOneShot(
                            Constants.Vibrations.SMALL_DURATION,
                            Constants.Vibrations.TICK_AMPLITUDE
                        )
                    }
                }
            )
        }

    }

    private fun createTimePickerState(): TimePickerState {
        val now = LocalTime.now()

        return TimePickerState(
            is24Hour = DateFormat.is24HourFormat(application.applicationContext),
            initialHour = now.hour,
            initialMinute = now.minute
        )
    }


    private fun updateTextResult() {
        _uiState.update {
            _uiState.value.copy(
                resultText = _uiState.value.calculation.duration.toFormattedString()
            )
        }
    }

    sealed class ScreenEvent {
        data object EnteredTimeInvalid : ScreenEvent()
        data object GenericError : ScreenEvent()
    }

    sealed class VibrationTypes {
        data object Short : VibrationTypes()
        data object Tick : VibrationTypes()
    }

}