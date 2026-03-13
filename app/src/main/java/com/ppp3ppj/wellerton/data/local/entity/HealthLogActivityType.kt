package com.ppp3ppj.wellerton.data.local.entity

enum class HealthLogActivityType(val emoji: String, val label: String) {
    WAKEUP("🌅", "Wakeup"),
    DRINK_WATER("💧", "Drink Water"),
    TOILET("🚽", "Toilet"),
    MEAL("🍽", "Meal"),
    MEDICINE("💊", "Medicine"),
    EXERCISE("🏃", "Exercise"),
    SLEEP("😴", "Sleep"),
    OTHER("📝", "Other")
}
