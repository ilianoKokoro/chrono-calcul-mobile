package com.iliano.chrono_calcul_mobile.models

import com.iliano.chrono_calcul_mobile.core.Constants
import com.iliano.chrono_calcul_mobile.core.durationBetween
import java.time.LocalTime
import kotlin.time.Duration

data class Calculation(
    var applyOffset: Boolean = Constants.DefaultValues.OFFSET,
    private var _targetTime: LocalTime = LocalTime.now()
) {
    fun setTargetTime(hours: Int, minutes: Int) {
        _targetTime = LocalTime.of(hours, minutes)
    }

    fun getTargetTime(): LocalTime {
        return _targetTime
    }

    val duration: Duration
        get() {
            var currentTime = LocalTime.now()

            if (applyOffset) {
                currentTime = currentTime.plusMinutes(Constants.TimeOffsets.SECURITY_MINUTES)
            }

            if (_targetTime.isBefore(currentTime)) {
                return Duration.ZERO
            }

            return currentTime.durationBetween(_targetTime)
        }
}
