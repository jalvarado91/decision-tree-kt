package me.juanalvarado.decisiontree

import java.util.HashMap

class SimpleSampleItem private constructor(override val labelName: String, columnHeaders: List<String>, values: List<Any>) : ISampleItem {

    private val values = HashMap<String, Any>()

    init {
        columnHeaders.indices.forEach { i -> this.values.put(columnHeaders[i], values[i]) }
    }

    override fun getValue(column: String): Any? {
        return this.values[column]
    }

    override val label: String
        get() = values[labelName] as String

    override fun toString(): String {
        return "[" + this.values.toString() + "]"
    }

    companion object {
        fun newSimpleSampleItem(labelColumn: String, columnHeaders: List<String>, values: List<Any>): SimpleSampleItem {
            return SimpleSampleItem(labelColumn, columnHeaders, values)
        }
    }
}
