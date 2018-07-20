package me.juanalvarado.decisiontree

interface IFeature {
    val attrName: String

    val attrValues: List<String>

    fun split(data: List<ISampleItem>): List<List<ISampleItem>>
}

