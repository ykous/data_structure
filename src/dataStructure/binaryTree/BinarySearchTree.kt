package com.ykous.collections.dataStructure.binaryTree

/**
 * @author ykous
 * @Date - 2020/9/13 - 16:29
 */
open class BinarySearchTree<T : Comparable<T>>() : BinaryTree<T>() {
    protected open var _size = 0

    override val size: Int
        get() = _size

    /**
     * 这是提供个外界的接口，它只应该负责生成节点来查找，子类应该重写它的重载，来改变具体算法
     *
     * 本质原因是因为 value 作为参数无法实现多态
     */
    open fun search(value: T): T? {
        return search(BinaryNode(value))?.data
    }

    protected open var hot: BinaryNode<T>? = null

    /**
     * 会把找到的终点的父节点纪录在 hot 上
     */
    protected open fun search(node: BinaryNode<T>): BinaryNode<T>? {
        var cursor = root as BinaryNode?
        while (cursor != null) {
            when {
                cursor.data > node.data -> {
                    hot = cursor
                    cursor = cursor.left
                }
                cursor.data < node.data -> {
                    hot = cursor
                    cursor = cursor.right
                }
                else -> {
                    return cursor
                }
            }
        }
        return cursor
    }

    /**
     * 这是提供个外界的接口，它只应该负责生成节点来查找，子类应该重写它的重载，来改变具体算法
     *
     * 本质原因是因为 value 作为参数无法实现多态
     */
    open fun insert(value: T): T {
        return insert(BinaryNode(value)).data
    }

    protected open fun insert(node: BinaryNode<T>): BinaryNode<T> {
        if (root == null) {
            _size++
            this._root = node
        }
        val search = search(node)
        if (search != null) {
            search.data = node.data
            return search
        }
        hot!!.setChild(node)
        _size++
        return node
    }

    /**
     * 这是提供个外界的接口，它只应该负责生成节点来查找，子类应该重写它的重载，来改变具体算法
     *
     * 本质原因是因为 value 作为参数无法实现多态
     */
    open fun remove(value: T): T? {
        return remove(BinaryNode(value))?.data
    }

    /**
     * 返回指定节点在中序遍历意义下的直接后继
     */
    private fun succeed(node: BinaryNode<T>): BinaryNode<T>? {
        var right = node.right
        if (right == null) {
            hot = right
            return right
        }
        while (right!!.left != null) {
            hot = right
            right = right.left
        }
        return right
    }

    protected open fun remove(node: BinaryNode<T>): BinaryNode<T>? {
        // 查找是否存在同值节点，不存在直接返回null
        // 此时 hot 指向路径终点的父节点
        val search = search(node) ?: return null
        val data = search.data
        if (search == root) {
            // 如果删除的是根节点，则应该令接替者成为新的根节点
            this._root = removeAt(root as BinaryNode<T>)
        } else {
            removeAt(search)
        }
        _size--
        return BinaryNode(data)
    }

    /**
     * 删除指定节点的在全局树上，返回其接替者
     */
    private fun removeAt(node: BinaryNode<T>): BinaryNode<T>? {
        var parent: BinaryNode<T>? = node.parent as BinaryNode<T>?
        // 替换节点是否是左孩子
        var isLeftChild: Boolean = node.isLeftChild
        // 后继节点
        val succeed: BinaryNode<T>?
        val result: BinaryNode<T>?
        when {
            !node.hasLeftChild -> {
                succeed = node.right
                result = succeed
                isLeftChild = node.isLeftChild
            }
            !node.hasRightChild -> {
                succeed = node.left
                result = succeed
                isLeftChild = node.isLeftChild
            }
            else -> {
                val t = succeed(node)!!

                val temp = t.data
                t.data = node.data
                node.data = temp

                succeed = t.right
                parent = t.parent as BinaryNode<T>?
                isLeftChild = t.isLeftChild
                result = node
            }
        }
        if (isLeftChild) {
            parent?.left = succeed
        } else {
            parent?.right = succeed
        }
        succeed?.parent = parent

        parent?.updateHeight()
        return result
    }
}
