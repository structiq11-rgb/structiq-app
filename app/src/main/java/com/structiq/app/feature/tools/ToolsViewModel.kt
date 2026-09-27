package com.structiq.app.feature.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.structiq.app.core.database.CalculationEntity
import com.structiq.app.core.model.CalculationCategory
import com.structiq.app.data.repository.CalculationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ConcreteResult(
    val wetVolumeM3: Double,
    val dryVolumeM3: Double,
    val cementBags: Double,
    val sandM3: Double,
    val aggregateM3: Double,
    val waterLiters: Double
)

data class RebarResult(
    val weightPerMeterKg: Double,
    val totalWeightKg: Double,
    val totalWeightTons: Double,
    val totalBars12m: Int
)

class ToolsViewModel(private val calculationRepository: CalculationRepository) : ViewModel() {

    val savedCalculations: StateFlow<List<CalculationEntity>> = calculationRepository.getAllSavedCalculations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun calculateConcrete(
        lengthM: Double,
        widthM: Double,
        thicknessM: Double,
        ratioCement: Double = 1.0,
        ratioSand: Double = 2.0,
        ratioAggregate: Double = 4.0
    ): ConcreteResult {
        val wetVol = lengthM * widthM * thicknessM
        val dryVol = wetVol * 1.54 // 54% dry volume factor
        val sumRatio = ratioCement + ratioSand + ratioAggregate
        
        val cementVol = (ratioCement / sumRatio) * dryVol
        val cementKg = cementVol * 1440.0 // cement density 1440 kg/m³
        val cementBags = cementKg / 50.0 // 50kg bag
        
        val sandM3 = (ratioSand / sumRatio) * dryVol
        val aggM3 = (ratioAggregate / sumRatio) * dryVol
        val waterLiters = cementBags * 25.0 // ~25L per 50kg bag (w/c = 0.5)

        return ConcreteResult(
            wetVolumeM3 = wetVol,
            dryVolumeM3 = dryVol,
            cementBags = cementBags,
            sandM3 = sandM3,
            aggregateM3 = aggM3,
            waterLiters = waterLiters
        )
    }

    fun calculateRebar(
        diameterMm: Double,
        totalLengthM: Double
    ): RebarResult {
        val weightPerM = (diameterMm * diameterMm) / 162.0
        val totalKg = weightPerM * totalLengthM
        val totalTons = totalKg / 1000.0
        val bars12m = (totalLengthM / 12.0).toInt() + 1

        return RebarResult(
            weightPerMeterKg = weightPerM,
            totalWeightKg = totalKg,
            totalWeightTons = totalTons,
            totalBars12m = bars12m
        )
    }

    fun saveCalculation(
        title: String,
        category: CalculationCategory,
        formula: String,
        result: String
    ) {
        viewModelScope.launch {
            val calc = CalculationEntity(
                id = "calc_${UUID.randomUUID().toString().take(8)}",
                title = title,
                category = category,
                inputParamsJson = "{}",
                formulaUsed = formula,
                resultFormatted = result,
                timestampEpochMs = System.currentTimeMillis()
            )
            calculationRepository.saveCalculation(calc)
        }
    }
}
