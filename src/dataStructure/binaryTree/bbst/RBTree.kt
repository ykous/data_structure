package com.ykous.collections.dataStructure.binaryTree.bbst

import com.ykous.collections.dataStructure.bTree.BalanceTree
import java.lang.StringBuilder
import kotlin.math.max

/**
 * @author ykous
 * @Date - 2020/9/29 - 14:14
 */
open class RBTree<T : Comparable<T>> : AbstractBBST<T>() {
    protected enum class RBTreeColor {
        RED, BLACK
    }

    protected open class RBTreeNode<T : Comparable<T>>(value: T, var color: RBTreeColor = RBTreeColor.RED) :
        BinaryNode<T>(value) {
        // 当前节点到其任意叶子节点的路径中所含的黑色节点的个数(不包括自身)，等效于其所在 (2,4)树 中超级节点的高度
        var blackHeight: Int = 0

        // 只有当该节点所处的超级节点的拖布结构发生改变时，才需要调用此方法
        // 例如当前对应的关键码在上溢状态中被迁移到了父超级节点
        fun updateBlackHeight() {
            blackHeight = max(
                ((left as RBTreeNode<T>?)?.blackHeight ?: -1)
                        + if ((left as RBTreeNode<T>?)?.color == RBTreeColor.BLACK) 1 else 0,
                ((right as RBTreeNode<T>?)?.blackHeight ?: -1)
                        + if ((right as RBTreeNode<T>?)?.color == RBTreeColor.BLACK) 1 else 0
            )
            // 它不需要向上传播，因为每一次拓扑结构的改变都应该对相应关键码执行该方法，因此向上传播是多余的
        }
    }

    override fun insert(value: T): T {
        return insert(RBTreeNode<T>(value)).data
    }

    override fun insert(node: BinaryNode<T>): BinaryNode<T> {
        // 先插入节点
        val inserted = super.insert(node) as RBTreeNode
        // 检测双红缺陷
        solveDoubleRed(inserted)
        return inserted
    }

    private fun solveDoubleRed(node: RBTreeNode<T>) {
        if (node.parent == null) {
            node.color = RBTreeColor.BLACK
            return
        }
        val parent = node.parent as RBTreeNode<T>
        if (parent.color == RBTreeColor.BLACK) {
            return
        }
        // 如果父节点为红色必有祖父节点，且为黑色
        val grandParent = parent.parent!! as RBTreeNode
        val uncleNode = if (parent.isLeftChild) {
            grandParent.right
        } else {
            grandParent.left
        } as RBTreeNode?
        if (uncleNode == null || uncleNode.color == RBTreeColor.BLACK) {
            val center = connect34(node) as RBTreeNode
            // 重构后需要重新染色，中黑侧红
            center.color = RBTreeColor.BLACK
            (center.left as RBTreeNode).color = RBTreeColor.RED
            (center.right as RBTreeNode).color = RBTreeColor.RED
        } else {
            uncleNode.color = RBTreeColor.BLACK
            parent.color = RBTreeColor.BLACK
            grandParent.color = RBTreeColor.RED
            // 这种操作后 grandParent 代表的关键码在解决上溢的过程中被提升了，因此要更新黑高度
            grandParent.updateBlackHeight()
            solveDoubleRed(grandParent)
        }

    }

    override fun toString(): String {
        val sb = StringBuilder()
        if (root == null) {
            return ""
        }
        traversalSubTree(root as BinaryNode<T>, 0) {
            it as RBTreeNode
            sb.append("$it\n")
            sb.append("style $it fill:${if (it.color == RBTreeColor.RED) "#ff6f6f" else "#6f6f6f"}\n")
            if (it.parent != null) {
                sb.append("${it.parent} --> $it\n")
            }
        }
        return sb.toString()
    }
}