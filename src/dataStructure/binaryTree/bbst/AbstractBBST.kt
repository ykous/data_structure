package com.ykous.collections.dataStructure.binaryTree.bbst

import com.ykous.collections.dataStructure.binaryTree.BinarySearchTree

/**
 * @author ykous
 * @Date - 2020/9/14 - 9:37
 */
abstract class AbstractBBST<T : Comparable<T>> : BinarySearchTree<T>() {

    protected fun rotateAt(node: BinaryNode<T>): BinaryNode<T> {
        val parent = node.parent as BinaryNode? ?: return node
        val grandParent = parent.parent as BinaryNode?
        val isLeftChild = node.isLeftChild

        if (parent.isLeftChild) {
            grandParent?.left = node
            node.parent = grandParent
        } else {
            grandParent?.right = node
            node.parent = grandParent
        }

        if (isLeftChild) {
            parent.left = node.right
            node.right?.parent = parent

            node.right = parent
            parent.parent = node
        } else {
            parent.right = node.left
            node.left?.parent = parent

            node.left = parent
            parent.parent = node
        }

        // 二者都需要更新高度
        parent.updateHeight()
        node.updateHeight()
        return node
    }

    protected open fun doubleRotateAt(node: BinaryNode<T>): BinaryNode<T> {
        rotateAt(node)
        return rotateAt(node)
    }

    // 以指定节点为孙子节点执行 3+4 重构，返回构造后局部子树的根
    // 注意不存在父节点或祖父节点都会导致异常
    protected fun connect34(node: BinaryNode<T>): BinaryNode<T> {
        val parent = node.parent as BinaryNode? ?: throw Exception("3+4重构失败，缺少父节点")
        val granParent = parent.parent as BinaryNode? ?: throw Exception("3+4重构失败，缺少祖父节点")
        val haven = granParent.parent as BinaryNode?
        val isLeft = granParent.isLeftChild

        val center: BinaryNode<T>
        val left: BinaryNode<T>
        val right: BinaryNode<T>
        val tree1: BinaryNode<T>?
        val tree2: BinaryNode<T>?
        val tree3: BinaryNode<T>?
        val tree4: BinaryNode<T>?
        if (node.isLeftChild) {
            if (parent.isLeftChild) {
                center = parent
                left = node
                right = granParent
                tree1 = node.left
                tree2 = node.right
                tree3 = parent.right
                tree4 = granParent.right
            } else {
                center = node
                left = granParent
                right = parent
                tree1 = granParent.left
                tree2 = node.left
                tree3 = node.right
                tree4 = parent.right
            }
        } else {
            if (parent.isLeftChild) {
                center = node
                left = parent
                right = granParent
                tree1 = parent.left
                tree2 = node.left
                tree3 = node.right
                tree4 = granParent.right
            } else {
                center = parent
                left = granParent
                right = node
                tree1 = granParent.left
                tree2 = parent.left
                tree3 = node.left
                tree4 = node.right
            }
        }
        center.left = left
        left.parent = center
        center.right = right
        right.parent = center
        left.left = tree1
        tree1?.parent = left
        left.right = tree2
        tree2?.parent = left
        right.left = tree3
        tree3?.parent = right
        right.right = tree4
        tree4?.parent = right

        if (isLeft) {
            haven?.left = center
        } else {
            haven?.right = center
        }
        center.parent = haven

        left.updateHeight()
        right.updateHeight()
        center.updateHeight()

        // 注意，如果 haven 为空表示 center 成为了新的树根
        if (haven == null) {
            _root = center
        }
        return center
    }

}