package com.ykous.collections.dataStructure.binaryTree.bbst

/**
 * @author ykous
 * @Date - 2020/9/17 - 19:40
 */
class AVLTree<T : Comparable<T>> : AbstractBBST<T>() {
    // 为 BinaryNode 临时添加扩展只读属性平衡因子
    private val BinaryNode<T>.balanceFactor
        get() = (this.left?.height ?: -1) - (this.right?.height ?: -1)

    override fun insert(node: BinaryNode<T>): BinaryNode<T> {
        val result = super.insert(node)
        var insert = result
        var parent = insert.parent as BinaryNode?
        while (parent != null) {
            val fac = parent.balanceFactor
            if (fac in -1..1) {
                insert = parent
                parent = parent.parent as BinaryNode<T>?
            } else {
                val rotateAt = rotateAt(insert)
                if (parent == root) {
                    _root = rotateAt
                }
                break
            }
        }
        return result
    }


    override fun remove(node: BinaryNode<T>): BinaryNode<T>? {
        val result = super.remove(node)
        var cursor = hot
        while (cursor != null) {
            if (cursor.balanceFactor !in -1..1) {
                // 找到失衡位置的高度较大的儿子节点的高度较大的儿子节点
                cursor = if (cursor.left?.height ?: -1 > cursor.right?.height ?: -1) cursor.left else cursor.right
                val child =
                    if (cursor?.left?.height ?: -1 > cursor?.right?.height ?: -1) cursor?.left else cursor?.right

                if (child != null && (child.isLeftChild xor cursor!!.isLeftChild)) {
                    cursor = child
                    cursor.let {
                        val rotate = doubleRotateAt(it)
                        if (rotate.parent == null) {
                            _root = rotate
                        }
                    }
                } else {
                    cursor?.let {
                        val rotate = rotateAt(it)
                        if (rotate.parent == null) {
                            _root = rotate
                        }
                    }
                }


            }
            // 无论如何都必须向上检查是否存在父节点失衡，直到到达根节点
            cursor = cursor?.parent as BinaryNode<T>?
        }
        return result
    }
}