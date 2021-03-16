package com.ykous.collections.dataStructure.binaryTree.bbst

/**
 * @author ykous
 * @Date - 2020/9/14 - 9:46
 */
class SplayTree<T : Comparable<T>> : AbstractBBST<T>() {

    private fun splay(node: BinaryNode<T>) {
        while (node.parent != null) {
            doubleRotateAt(node)
        }
        _root = node
    }

    override fun doubleRotateAt(node: BinaryNode<T>): BinaryNode<T> {
        val parent = node.parent as BinaryNode? ?: return node
        return if (node.isLeftChild xor parent.isLeftChild) {
            super.doubleRotateAt(node)
        } else {
            rotateAt(parent)
            rotateAt(node)
        }
    }

    override fun search(value: T): T? {
        val search = super.search(BinaryNode(value))
        if (search == null) {
            hot?.let { splay(it) }
        } else {
            splay(search)
        }
        return search?.data
    }

    override fun insert(node: BinaryNode<T>): BinaryNode<T> {
        val insert = super.insert(node)
        splay(insert)
        return insert
    }

    override fun remove(node: BinaryNode<T>): BinaryNode<T>? {
        val remove = super.remove(node)

        hot?.let { splay(it) }

        return remove
    }
}