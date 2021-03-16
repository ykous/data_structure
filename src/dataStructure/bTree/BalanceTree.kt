package com.ykous.collections.dataStructure.bTree

import java.lang.Exception
import java.util.*
import javax.swing.KeyStroke
import kotlin.collections.ArrayList

/**
 * B树
 * @param order B树的阶数
 *
 * @author ykous
 * @Date - 2020/9/27 - 10:38
 */
class BalanceTree<K : Comparable<K>, V>(val order: Int) {
    private class BalanceTreeNode<K : Comparable<K>, V>(val order: Int) {
        val floorOrder: Int = order / 2 + (order and 1)
        val keys = ArrayList<K>(order)
        val values = ArrayList<V>(order)
        val children = ArrayList<BalanceTreeNode<K, V>?>(order + 1)
        var parent: BalanceTreeNode<K, V>? = null

        // 关键码数量
        val size
            get() = keys.size

        // 是否是叶子节点
        val isLeaf
            get() = children.isNotEmpty() && children[0] == null

        /**
         * 提供关键码生成节点，子节点全部默认为null
         * 如果关键码数量达到阶数则抛出异常
         */
        constructor(keysAndValues: List<Pair<K, V>>, order: Int) : this(order) {
            if (keysAndValues.size >= order) {
                throw Exception("在生成平衡树节点时发现提供的关键码个数超出上限")
            }
            for (kv in keysAndValues) {
                keys.add(kv.first)
                values.add(kv.second)
            }
            for (i in 0..values.size) {
                children.add(null)
            }
        }

        /**
         * 同时提供关键码与子节点生成节点，如果关键码与子节点间不满足 关键码个数+1=子节点个数 则抛出异常
         */
        constructor(keysAndValues: List<Pair<K, V>>, children: List<BalanceTreeNode<K, V>?>, order: Int) : this(
            keysAndValues,
            order
        ) {
            if (values.size + 1 != children.size) {
                throw Exception("在生成平衡树节点时发生错误，平衡树的节点的关键码个数与子节点个数有异常")
            }
            this.children.clear()
            this.children.addAll(children)
        }

        /**
         * 根据关键码赋值，返回关键码所在索引，未找到则返回-1
         */
        fun setValue(key: K, value: V): Int {
            for (i in keys.indices) {
                if (keys[i] == key) {
                    values[i] = value
                    return i
                }
            }
            return -1
        }

        /**
         * 直接根据索引值赋值
         */
        fun setValueAt(index: Int, value: V) {
            values[index] = value
        }

        /**
         * 为该节点添加键值对和默认为 null 的子节点
         */
        fun addKeyAndValue(key: K, value: V) {
            var i = 0
            while (i in keys.indices) {
                if (keys[i] > key) {
                    break
                }
                i++
            }
            keys.add(i, key)
            values.add(i, value)
            children.add(null)
        }

        /**
         * 删除指定键，限于对叶子节点使用
         */
        fun removeKey(key: K): Int {
            for (i in keys.indices) {
                if (keys[i] == key) {
                    keys.removeAt(i)
                    values.removeAt(i)
                    children.removeAt(size - 1)
                    return i
                }
            }
            return -1
        }

        /**
         * 删除指定索引位置键值对，限于对叶子节点使用
         */
        fun removeAt(index: Int) {
            keys.removeAt(index)
            values.removeAt(index)
            children.removeAt(size)
        }


        override fun toString(): String {
            val sb = StringBuilder()
            keys.forEach {
                sb.append("$it,")
            }
            sb.deleteCharAt(sb.length - 1)
            return sb.toString()
        }
    }

    init {
        if (order < 3) {
            throw Exception("B树的阶数至少为3")
        }
    }

    val floorOrder = order / 2 + (order and 1)

    private var root: BalanceTreeNode<K, V>? = null
    private var _size = 0
    val size
        get() = _size

    // 辅助用于 search 接口的动态标志
    private var hot: BalanceTreeNode<K, V>? = null

    /**
     * 解决上溢问题
     */
    private fun solveOverflow(node: BalanceTreeNode<K, V>) {
        // 分裂的位置
        val index = order / 2

        val parent = node.parent
        // 准备分裂后的节点
        // 键值对制备
        val list = ArrayList<Pair<K, V>>(index)
        for (i in 0 until index) {
            list.add(node.keys[i] to node.values[i])
        }
        val list2 = ArrayList<Pair<K, V>>(index)
        for (i in index + 1 until order) {
            list2.add(node.keys[i] to node.values[i])
        }
        // 子节点制备
        val children1 = node.children.subList(0, index + 1)
        val children2 = node.children.subList(index + 1, order + 1)
        val left = BalanceTreeNode(list, children1, order)
        val right = BalanceTreeNode(list2, children2, order)
        // 注意子节点的父节点指针要更新
        children1.forEach{
            it?.parent = left
        }
        children2.forEach{
            it?.parent = right
        }

        // 如果父节点指针存在，说明该节点非根节点
        parent?.let {
            // 找到父节点中应该插入值的位置
            var i = 0
            while(i in it.keys.indices) {
                if (it.keys[i] > node.keys[index]) {
                    break
                }
                i++
            }
            // 分别插入键，值，左右子节点
            it.keys.add(i, node.keys[index])
            it.values.add(i, node.values[index])
            it.children[i] = left
            left.parent = parent
            it.children.add(i + 1, right)
            right.parent = parent
            // 父节点由于插入了新值，也有上溢的风险
            if (parent.size >= order) {
                solveOverflow(parent)
            }
            return
        }
        // 当父节点为空时，说明此时分裂的结果会产生新的根节点
        val newRoot =
            BalanceTreeNode(listOf(node.keys[index] to node.values[index]), listOf(left, right), order)
        left.parent = newRoot
        right.parent = newRoot
        root = newRoot
    }

    /**
     * 解决下溢问题
     */
    private fun solveUnderflow(node: BalanceTreeNode<K, V>) {
        val parent = node.parent
        if (parent == null) {
            // 父节点为空则表明该节点是根节点，根节点发生下溢无需处理，除非根节点成为空值节点，此时其有唯一子节点，令子节点为新根节点
            if (node.size == 0) {
                val newRoot = node.children[0]
                newRoot?.parent = null
                root = newRoot
            }
            return
        }
        // 找到当前节点的左右兄弟，以及当前节点所在的索引
        var left: BalanceTreeNode<K, V>? = null
        var right: BalanceTreeNode<K, V>? = null
        var index = 0
        for (i in parent.children.indices) {
            if (parent.children[i] == node) {
                if (i > 0) {
                    left = parent.children[i - 1]
                }
                if (i < parent.size) {
                    right = parent.children[i + 1]
                }
                index = i
                break
            }
        }
        if (left != null && left.size >= floorOrder) {
            val tempChild = left.children.removeAt(left.size)
            val leftKey = left.keys.removeAt(left.size - 1)
            val leftVal = left.values.removeAt(left.size - 1)
            node.keys.add(0, parent.keys[index - 1])
            node.values.add(0, parent.values[index - 1])
            node.children.add(0, tempChild)
            parent.keys[index - 1] = leftKey
            parent.values[index - 1] = leftVal
        } else if (right != null && right.size >= floorOrder) {
            val tempChild = right.children.removeAt(0)
            val rightKey = right.keys.removeAt(0)
            val rightVal = right.values.removeAt(0)
            node.keys.add(parent.keys[index])
            node.values.add(parent.values[index])
            node.children.add(tempChild)
            parent.keys[index] = rightKey
            parent.values[index] = rightVal
        } else {
            // 左右都不能借则和其中一个合并成新节点，不妨让左节点存在时就选择左节点
            if (left != null) {
                // 准备顶替的节点
                val newNode = BalanceTreeNode<K, V>(order)
                newNode.keys.addAll(left.keys)
                newNode.values.addAll(left.values)
                newNode.children.addAll(left.children)
                newNode.keys.add(parent.keys[index - 1])
                newNode.values.add(parent.values[index - 1])
                newNode.keys.addAll(node.keys)
                newNode.values.addAll(node.values)
                newNode.children.addAll(node.children)
                // 注意更新合并节点子节点的父节点，否则将指向原本的节点
                left.children.forEach {
                    it?.parent = newNode
                }
                node.children.forEach {
                    it?.parent = newNode
                }
                // 删除父节点中原有的关键码
                parent.keys.removeAt(index - 1)
                parent.values.removeAt(index - 1)
                parent.children.removeAt(index)
                // 最后将准备好的新节点插入,当然还需要设置一下父子关系
                parent.children[index - 1] = newNode
                newNode.parent = parent
            } else {
                // 准备顶替的节点
                val newNode = BalanceTreeNode<K, V>(order)
                newNode.keys.addAll(node.keys)
                newNode.values.addAll(node.values)
                newNode.children.addAll(node.children)
                newNode.keys.add(parent.keys[index])
                newNode.values.add(parent.values[index])
                newNode.keys.addAll(right!!.keys)
                newNode.values.addAll(right.values)
                newNode.children.addAll(right.children)
                // 注意更新合并节点子节点的父节点，否则将指向原本的节点
                right.children.forEach {
                    it?.parent = newNode
                }
                node.children.forEach {
                    it?.parent = newNode
                }
                // 删除父节点中原有的关键码
                parent.keys.removeAt(index)
                parent.values.removeAt(index)
                parent.children.removeAt(index)
                // 最后将准备好的新节点插入,当然还需要设置一下父子关系
                parent.children[index] = newNode
                newNode.parent = parent
            }
            // 无论如何 由于父节点的关键码减少，因此父节点也将面临下溢风险
            solveUnderflow(parent)
        }
    }

    fun search(key: K): V? {
        val baseSearch = baseSearch(key) ?: return null
        return baseSearch.second.values[baseSearch.first]
    }

    /**
     * 基础查找
     * @return 第一个数是键的索引，第二个是所在节点
     */
    private fun baseSearch(key: K): Pair<Int, BalanceTreeNode<K, V>>? {
        var cursor = root
        hot = null
        while (cursor != null) {
            val keys = cursor.keys
            hot = cursor
            var i = 0
            while (i in keys.indices) {
                if (keys[i] == key) {
                    return i to cursor
                }
                if (keys[i] > key) {
                    break
                }
                i++
            }
            cursor = cursor.children[i]
        }
        return null
    }

    fun insert(key: K, value: V): Boolean {
        val baseSearch = baseSearch(key)
        if (baseSearch != null) {
            baseSearch.second.setValueAt(baseSearch.first, value)
        } else {
            if (hot == null) {
                // 如果搜索结果为null 且 hot 也为null ，只可能是该树为空树
                root = BalanceTreeNode(listOf(key to value), order)
            } else {
                hot?.let {
                    it.addKeyAndValue(key, value)
                    if (it.size >= order) {
                        solveOverflow(it)
                    }
                }
            }
        }
        _size++
        return true
    }

    fun remove(key: K): V? {
        var (index, beRemoved) = baseSearch(key) ?: return null
        val result = beRemoved.values[index]
        if (!beRemoved.isLeaf) {
            var cursor = beRemoved.children[index + 1]!!
            while (cursor.children[0] != null) {
                cursor = cursor.children[0]!!
            }
            beRemoved = cursor
            index = 0
        }
        beRemoved.removeAt(index)
        if (beRemoved.size < floorOrder - 1) {
            solveUnderflow(beRemoved)
        }
        _size--
        return result
    }

    private fun sequenceTraversalForTreeStruct(function: (node: BalanceTreeNode<K, V>) -> Unit) {
        if (root == null) {
            return
        }
        val queue = LinkedList<BalanceTreeNode<K, V>>()
        queue += root!!
        while (queue.isNotEmpty()) {
            val poll = queue.poll()
            function(poll)
            poll.children.forEach {
                if (it != null) {
                    queue += it
                }
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sequenceTraversalForTreeStruct {
            it.children.forEach { child ->
                if (child == null) {
                    return@forEach
                }
                sb.append("$it --> $child\n")
            }
        }
        return sb.toString()
    }
}