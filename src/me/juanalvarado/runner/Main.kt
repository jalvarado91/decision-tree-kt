package me.juanalvarado.runner

import me.juanalvarado.decisiontree.DecisionTree
import me.juanalvarado.decisiontree.IFeature
import me.juanalvarado.decisiontree.ISampleItem
import me.juanalvarado.decisiontree.SimpleSampleItem
import java.util.*

fun main(args: Array<String>) {
    val features = getFeatures()
    val data = getData()

    val tree = DecisionTree()

    tree.train(data, features)
    tree.printTree()

    val pSample = SimpleSampleItem.newSimpleSampleItem(
            "buys_computer",
            listOf("age", "income", "student", "credit_rating"),
            listOf("senior", "high", "yes", "excellent")
    )

    val prediction = tree.classify(pSample)
    println("Sample: " + pSample + ", Prediction: " + pSample.labelName + ": " + prediction)

}

private fun getFeatures(): List<IFeature> {
    val ageFeature = AgeFeature("age", Arrays.asList("youth", "middle_aged", "senior"))
    val incomeFeature = IncomeFeature("income", Arrays.asList("low", "medium", "high"))
    val studentFeature = StudentFeature("student", Arrays.asList("yes", "no"))
    val creditFeature = CreditFeature("credit_rating", Arrays.asList("fair", "excellent"))

    return listOf(
            ageFeature,
            incomeFeature,
            studentFeature,
            creditFeature)
}

private fun getData(): List<ISampleItem> {
    val attrs = listOf("age", "income", "student", "credit_rating", "buys_computer")
    val data = rawData()

    return data.map { row ->
        SimpleSampleItem.newSimpleSampleItem("buys_computer", attrs, row)
    }
}

private fun rawData(): List<List<String>> {
    return Arrays.asList(
            Arrays.asList("youth", "high", "no", "fair", "no"),
            Arrays.asList("youth", "high", "no", "excellent", "no"),
            Arrays.asList("middle_aged", "high", "no", "fair", "yes"),
            Arrays.asList("senior", "medium", "no", "fair", "yes"),
            Arrays.asList("senior", "low", "yes", "fair", "yes"),
            Arrays.asList("senior", "low", "yes", "excellent", "no"),
            Arrays.asList("middle_aged", "low", "yes", "excellent", "yes"),
            Arrays.asList("youth", "medium", "no", "fair", "no"),
            Arrays.asList("youth", "low", "yes", "fair", "yes"),
            Arrays.asList("senior", "medium", "yes", "fair", "yes"),
            Arrays.asList("youth", "medium", "yes", "excellent", "yes"),
            Arrays.asList("middle_aged", "medium", "no", "excellent", "yes"),
            Arrays.asList("senior", "medium", "no", "excellent", "no"),
            Arrays.asList("middle_aged", "high", "yes", "fair", "yes")
    )
}

internal class AgeFeature(attrName: String, attrValues: List<String>) : BaseFeature(attrName, attrValues)

internal class IncomeFeature(attrName: String, attrValues: List<String>) : BaseFeature(attrName, attrValues)

internal class StudentFeature(attrName: String, attrValues: List<String>) : BaseFeature(attrName, attrValues)

internal class CreditFeature(attrName: String, attrValues: List<String>) : BaseFeature(attrName, attrValues)

internal class BuysPCFeature(attrName: String, attrValues: List<String>) : BaseFeature(attrName, attrValues)

internal abstract class BaseFeature(override var attrName: String, override var attrValues: List<String>) : IFeature {

    override fun split(data: List<ISampleItem>): List<List<ISampleItem>> {
        return attrValues
                .map { data.filter { sample -> sample.getValue(attrName) == it } }
                .filterNot { it.isEmpty() }
    }

    override fun toString(): String {
        return javaClass.simpleName + "(" + attrName + ", " + attrValues + ")"
    }
}
