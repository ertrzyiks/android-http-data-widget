package com.example.httpdatawidget

class AutoUpdateInterval {

    data class AutoUpdateIntervalValue(val interval_in_minutes: Int, val label: String) {}

    companion object {
        val values = arrayOf(
            AutoUpdateIntervalValue(0, "Manually only"),
            AutoUpdateIntervalValue(1, "Every minute"),
            AutoUpdateIntervalValue(5, "Every 5 minutes"),
            AutoUpdateIntervalValue(10, "Every 10 minutes"),
            AutoUpdateIntervalValue(15, "Every 15 minutes"),
            AutoUpdateIntervalValue(30, "Every 30 minutes"),
            AutoUpdateIntervalValue(60, "Every hour"),
            AutoUpdateIntervalValue(120, "Every 2 hours"),
            AutoUpdateIntervalValue(360, "Every 6 hours")
        )

        fun indexForInterval(interval_in_minutes: Int): Int {
            for ((index, value) in values.withIndex()) {
                if (value.interval_in_minutes >= interval_in_minutes) {
                    return index
                }
            }

            return values.size - 1
        }

        fun valueForIndex(index: Int): Int {
            return values[index].interval_in_minutes

        }
        fun labelForIndex(index: Int): String {
            return values[index].label
        }

        fun totalOptions(): Int {
            return values.size
        }
    }
}