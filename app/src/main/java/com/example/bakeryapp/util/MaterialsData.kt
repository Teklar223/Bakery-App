package com.example.bakeryapp.util

data class MaterialsData(
    var materialId: String = "", //auto generated by firebase!
    var name: String = "",
    var description: String = "",
    var cost: Int = 0,
    var currency: String = "shekel", //default is shekel
    var unit: String = "kg" //cost is calculated per unit, for example 5 per 1 kg
)