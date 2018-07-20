package me.juanalvarado.decisiontree

import java.util.HashMap
import java.util.LinkedHashSet
import java.util.stream.Collectors


class DecisionTree {
    var root: Node? = null
    private var labelName: String? = null
    private val maxLevel = 4

    fun train(trainingTuples: List<ISampleItem>, features: List<IFeature>) {
        root = makeTree(trainingTuples, features, 1)
    }

    fun classify(testItem: ISampleItem): String {
        var node = root
        while (!node!!.isLeaf) {
            val relevantAttribute = node.feature?.attrName
            var edges = node.children.keys

            val targetEdge: String? = edges.firstOrNull { testItem.getValue(relevantAttribute!!) === it }

            node = node.children[targetEdge]
        }
        return node.label!!
    }

    protected fun makeTree(trainingTuples: List<ISampleItem>, features: List<IFeature>, currLevel: Int): Node {
        labelName = trainingTuples[0].labelName
        var currentNodeLabel: String?

        val potentialLabel = getLabelIfSameClass(trainingTuples)
        if (potentialLabel != null) {
            currentNodeLabel = potentialLabel
            return Node.newLeafNode(currentNodeLabel)
        }

        if (features.isEmpty() || currLevel >= maxLevel) {
            currentNodeLabel = getLabelFromMajorityVoting(trainingTuples)
            return Node.newLeafNode(currentNodeLabel)
        }

        val bestSplit = getBestSplitAttr(trainingTuples, features)
        val splitAttrs = bestSplit!!.attrValues
        val splitData = bestSplit.split(trainingTuples)

        val cleanedFeatures = features.stream()
                .filter { f -> !f.equals(bestSplit) }
                .collect(Collectors.toList<IFeature>())

        val n = Node.newNode(bestSplit)
        val attrsIt = splitAttrs.iterator()
        val splitDataIt = splitData.iterator()

        while (attrsIt.hasNext() && splitDataIt.hasNext()) {
            val trainingSubset = splitDataIt.next()
            val attributeVal = attrsIt.next()
            if (trainingSubset.isEmpty()) {
                n.addChild(attributeVal, Node.newLeafNode(getLabelFromMajorityVoting(trainingTuples)))
            } else {
                n.addChild(attributeVal, makeTree(trainingSubset, cleanedFeatures, currLevel + 1))
            }
        }

        return n
    }

    protected fun getBestSplitAttr(data: List<ISampleItem>, features: List<IFeature>): IFeature {
        val informationGain = 0.0
        var candidateFeature: IFeature? = null

        val gains = HashMap<IFeature, Double>()
        var maxGain = 0.0
        for (feature in features) {
            val newGain = calculateGain(data, feature)
            if (newGain > maxGain) {
                candidateFeature = feature
                maxGain = newGain
            }
            gains.put(feature, newGain)
        }

        candidateFeature = gains.maxBy { it.value }?.key

        return candidateFeature!!
    }

    protected fun getLabelIfSameClass(data: List<ISampleItem>): String? {
        var labelCount = data.groupingBy { it.label }.eachCount()

        return if (labelCount.keys.size == 1)
            labelCount.keys.toTypedArray()[0]
        else
            null
    }

    protected fun getLabelFromMajorityVoting(data: List<ISampleItem>): String {
        // Make a map of <Label, count> and return the one one with the most counts
        return data.groupingBy { it.label }.eachCount()
                .entries.maxBy { it.value }?.key!!
    }

    fun printTree() {
        printSubTree(root, 0)
    }

    fun printSubTree(node: Node?, level: Int) {
        var spacer = ""
        for (i in 1..level) spacer += "\t"
        printNode(node, spacer)
        if (!node!!.children.isEmpty()) {
            spacer += "\t"
            val children = node.children.values
            val attrs = node.children.keys
            val chIt = children.iterator()
            val attIt = attrs.iterator()
            while (chIt.hasNext() && attIt.hasNext()) {
                println(spacer + "-> " + attIt.next())
                print(spacer + "    ")
                printSubTree(chIt.next(), level + 1)
            }
        }
    }

    private fun printNode(node: Node?, spacer: String) {
        if (node!!.isLeaf) {
            print(spacer)
            print(labelName!! + ": ")
            System.out.print(node.label)
        } else {
            System.out.print(node.feature?.attrName?.toUpperCase())
        }
        println()
    }

    companion object {

        fun calculateProbRatio(yes_count: Double, no_count: Double, setSize: Double): Double {
            val probability: Double = if (yes_count / setSize != 0.0) yes_count / setSize else 1.0
            val probability2: Double = if (no_count / setSize != 0.0) no_count / setSize else 1.0
            return -(probability * (Math.log(probability) / Math.log(2.0))) - probability2 * (Math.log(probability2) / Math.log(2.0))
        }

        fun calculateGain(D: List<ISampleItem>, feature: IFeature): Double {
            var yes_count = 0.0
            var no_count = 0.0
            val setSize: Double
            val split = HashMap<String, LinkedHashSet<ISampleItem>>()
            for (t in D) {
                if (t.getValue("buys_computer")!!.equals("yes"))
                    yes_count++
                else
                    no_count++
                val curValue = t.getValue(feature.attrName).toString()
                if (split.containsKey(curValue)) {
                    split[curValue]!!.add(t)
                } else {
                    val ls = LinkedHashSet<ISampleItem>()
                    ls.add(t)
                    split.put(curValue, ls)
                }
            }

            setSize = D.size.toDouble() //Size of data set
            val infoD = calculateProbRatio(yes_count, no_count, setSize)


            var info_attr_D = 0.0
            var sub_yes_count = 0.0
            var sub_no_count = 0.0
            var subList_size = 0.0
            var prob = 0.0

            for (hs in split.values) {
                sub_yes_count = 0.0
                sub_no_count = 0.0
                for (t in hs) {
                    if (t.getValue("buys_computer")!!.equals("yes"))
                        sub_yes_count++
                    else
                        sub_no_count++
                }
                subList_size = hs.size.toDouble()
                prob = subList_size / setSize
                info_attr_D += prob * calculateProbRatio(sub_yes_count, sub_no_count, subList_size)
            }
            return infoD - info_attr_D
        }
    }
}