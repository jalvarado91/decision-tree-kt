package me.juanalvarado.decisiontree

interface ISampleItem {
    fun getValue(column: String): Any?
    val label: String
    val labelName: String
}