package com.ykous.collections.dataStructure.binaryTree

import com.ykous.collections.dataStructure.Tree
import com.ykous.collections.dataStructure.TreeNode
import java.util.*

/**
 * @author ykous
 * @Date - 2020/9/13 - 15:05
 */
abstract class BinaryTree<T : Comparable<T>> : Tree<T> {
    protected open var _root: BinaryNode<T>? = null
    override val root: TreeNode<T>?
        get() = _root


    protected open class BinaryNode<T : Comparable<T>>(value: T) : TreeNode<T>(value) {
        var left: BinaryNode<T>? = null
        var right: BinaryNode<T>? = null

        val isLeftChild
            get() = (parent as BinaryNode?)?.left == this
        val isRightChild
            get() = (parent as BinaryNode?)?.right == this

        val hasLeftChild
            get() = left != null
        val hasRightChild
            get() = right != null

        override fun setChild(node: TreeNode<T>): TreeNode<T>? {
            if (node !is BinaryNode<T> || node.data == data) {
                return null
            }
            if (node.data > data) {
                right = node
            } else {
                left = node
            }
            node.parent = this
            node.updateHeight()
            return node
        }

        override fun traversalChildren(function: (TreeNode<T>) -> Unit) {
            left?.let { function(it) }
            right?.let { function(it) }
        }

        override fun toString(): String {
            return "$data"
        }
    }

    protected open fun traversalSubTree(node: BinaryNode<T>, mode: Int, function: (node: BinaryNode<T>) -> Unit) {
        when (mode) {
            // 层序遍历
            0 -> {
                val queue = LinkedList<BinaryNode<T>>()
                queue += node
                while (queue.isNotEmpty()) {
                    val poll = queue.poll()
                    function(poll)
                    poll.left?.let { queue += it }
                    poll.right?.let { queue += it }
                }
            }
            // 先序
            1 -> {
                function(node)
                node.left?.let { traversalSubTree(it, mode, function) }
                node.right?.let { traversalSubTree(it, mode, function) }
            }
            // 中序
            2 -> {
                node.left?.let { traversalSubTree(it, mode, function) }
                function(node)
                node.right?.let { traversalSubTree(it, mode, function) }
            }
            // 后续
            3 -> {
                node.left?.let { traversalSubTree(it, mode, function) }
                node.right?.let { traversalSubTree(it, mode, function) }
                function(node)
            }
        }
    }

    override fun sequenceTraversal(function: (value: T) -> Unit) {
        root?.let {
            traversalSubTree(it as BinaryNode<T>, 0) {
                function(it.data)
            }
        }
    }

    override fun preOrderTraversal(function: (value: T) -> Unit) {
        root?.let {
            traversalSubTree(it as BinaryNode<T>, 1) {
                function(it.data)
            }
        }
    }

    override fun postOrderTraversal(function: (value: T) -> Unit) {
        root?.let {
            traversalSubTree(it as BinaryNode<T>, 3) {
                function(it.data)
            }
        }
    }

    override fun inOrderTraversal(function: (value: T) -> Unit) {
        root?.let {
            traversalSubTree(it as BinaryNode<T>, 2) {
                function(it.data)
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        root?.let {
            sb.append("$root\n")
            traversalSubTree(root as BinaryNode<T>, 0) {
                it.parent?.let {parent ->
                    sb.append("$parent --> $it\n")
                }
            }
        }
        return sb.toString()
    }
}
