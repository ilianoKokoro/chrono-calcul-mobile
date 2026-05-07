@file:OptIn(ExperimentalMaterial3Api::class)

package com.iliano.chrono_calcul_mobile.ui.screens.calculator

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.iliano.chrono_calcul_mobile.core.Constants
import com.iliano.chrono_calcul_mobile.models.Calculation
import java.time.LocalTime

data class CalculatorState(
    val calculation: Calculation = Calculation(),
    val resultText: String = "",
    val checkBoxState: Boolean = Constants.DefaultValues.OFFSET,
    val timePickerState: TimePickerState,
    val showTimePicker: Boolean = false
)