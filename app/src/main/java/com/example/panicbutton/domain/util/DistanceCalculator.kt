package com.example.panicbutton.domain.util

import kotlin.math.*

object DistanceCalculator {

    private const val EARTH_RADIUS_KM = 6371.0

    fun calculateDistanceKm(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    fun calculateEtaMinutes(
        distanceKm: Double,
        avgSpeedKmh: Double = 30.0
    ): Int {
        if (avgSpeedKmh <= 0) return 0
        return max(1, ceil((distanceKm / avgSpeedKmh) * 60.0).toInt())
    }
}
