package me.juanalvarado.decisiontree

import java.util.HashMap

class Node {

    var feature: IFeature? = null
        private set

    var label: String? = null
    var children = HashMap<String, Node>()
    private var data: List<ISampleItem>? = null


    private constructor(feature: IFeature) {
        this.feature = feature
    }

    private constructor(feature: IFeature?, label: String) {
        this.feature = feature
        this.label = label
    }

    private constructor(label: String, data: List<ISampleItem>) {
        this.label = label;
        this.data = data
    }

    fun addChild(attr: String, child: Node) {
        children.put(attr, child)
    }

    val isLeaf: Boolean
        get() = label != null

    val isLeafWithData: Boolean
        get() = label != null && data != null

    override fun toString(): String {
        return "[Node: label:$label, feature: $feature]"
    }

    companion object {

        fun newNode(feature: IFeature): Node {
            return Node(feature)
        }

        fun newLeafNode(label: String): Node {
            return Node(null, label)
        }

        fun newLeafWithData(label: String, data: List<ISampleItem>): Node {
            return Node(label, data)
        }
    }
}