package com.ykous.collections.dataStructure

import kotlin.math.max


/**
 * 树的顶级接口
 *
 * @author ykous
 * @Date - 2020/9/10 - 20:01
 */
interface Tree<T : Comparable<T>> {

    /**
     * 树的规模。即内部元素数量
     * */
    val size: Int

    val root: TreeNode<T>?

    val empty: Boolean
        get() {
            return size == 0
        }

    /**
     * 层序遍历
     * */
    fun sequenceTraversal(function: (value: T) -> Unit)

    /**
     * 先序遍历
     * */
    fun preOrderTraversal(function: (value: T) -> Unit)

    /**
     * 后序遍历
     * */
    fun postOrderTraversal(function: (value: T) -> Unit)

    /**
     * 中序遍历
     * */
    fun inOrderTraversal(function: (value: T) -> Unit)
}

/**
 * 树节点的顶级接口，任何一个节点都可以看作是一颗子树，因此它应该具有树的所有能力
 *
 * 此处中定义的是其作为单个节点应有的功能
 */
abstract class TreeNode<T>(value: T) {
    /**
     * 存储的数据
     * */
    var data: T = value

    private var _height = 0

    val height: Int
        get() = _height


    /**
     * 该节点所在的树的全局根
     */
    val root: TreeNode<T>
        get() {
            var cursor = this
            while (cursor.parent != null) {
                cursor = cursor.parent!!
            }
            return cursor
        }


    /**
     * 所有的树节点都应该必然有 parent 指针，因此他可以是不抽象的
     */
    var parent: TreeNode<T>? = null

    /**
     * 将指定节点定义为子节点
     *
     * 任何情况都应该使用该方法来设置子节点而不是直接通过子节点引用设置
     *
     * 由于树的每一种实现的数据结构本身不尽相同，因此我们定义一个抽象方法，实现类应该实现该方法来定义如何设置子节点
     *
     * 插入的节点必须是干净纯粹的，不能具有子节点的
     *
     * 无论如何不应该影响自身节点，只应该为子节点指针赋值
     */
    abstract fun setChild(node: TreeNode<T>): TreeNode<T>?

    /**
     * 遍历所有子节点，而不是子树
     */
    abstract fun traversalChildren(function: (TreeNode<T>) -> Unit)

    /**
     * 根据子节点的最大高度+1 更新自身高度，同时更新其所有父节点高度
     *
     * 使用前提是子节点的高度都是正确的
     */
    open fun updateHeight() {
        var temp = -1
        traversalChildren {
            temp = max(temp, it.height)
        }
        temp++
        if (temp == this.height && height != 0) {
            return
        }
        this._height = temp
        parent?.updateHeight()
    }


}


