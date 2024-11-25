package org.example

sealed class PersistentList<A> {
    abstract fun isEmpty(): Boolean
    abstract fun drop(n: Int): PersistentList<A>
    fun cons(a: A): PersistentList<A> = Cons(a, this)
    fun setHead(a: A): PersistentList<A> = when (this) {
        is Nil -> throw IllegalStateException("")
        is Cons -> this.tail.cons(a)
    }

    fun dropWhile(p: (A) -> Boolean): PersistentList<A> = dropWhile(this, p)
    fun concat(list: PersistentList<A>) = concat(this, list)
    fun reverse() = reverse(invoke(), this)
    // 마지막 원소 제거
    fun init(): PersistentList<A> = reverse().drop(1).reverse()

    private data object Nil : PersistentList<Nothing>() {
        override fun isEmpty() = false
        override fun drop(n: Int) = this
        override fun toString() = "[NIL]"

        @Suppress("UNCHECKED_CAST")
        fun <T> ref(): PersistentList<T> = Nil as PersistentList<T>
    }

    private class Cons<A>(
        val head: A,
        val tail: PersistentList<A>
    ) : PersistentList<A>() {
        override fun isEmpty() = false
        override fun drop(n: Int) = drop(this, n)
        override fun toString() = "[${toString(this, "")}NIL]"
    }

    companion object {
        operator fun <A> invoke(
            vararg az: A
        ): PersistentList<A> = az.foldRight(Nil.ref()) { a: A, list: PersistentList<A> ->
            Cons(a, list)
        }

        tailrec fun <A> drop(
            list: PersistentList<A>,
            n: Int
        ): PersistentList<A> = if (n <= 0) list else when (list) {
            is Cons -> drop(list.tail, n - 1)
            is Nil -> list
        }

        tailrec fun <A> toString(
            list: PersistentList<A>,
            acc: String
        ): String = when (list) {
            is Nil -> acc
            is Cons -> toString(list.tail, "$acc${list.head}, ")
        }

        tailrec fun <A> dropWhile(
            list: PersistentList<A>,
            p: (A) -> Boolean
        ): PersistentList<A> = when (list) {
            Nil -> list
            is Cons -> if (p(list.head)) {
                dropWhile(list.tail, p)
            } else list
        }

        tailrec fun <A> reverse(
            acc: PersistentList<A>,
            list: PersistentList<A>
        ): PersistentList<A> = when (list) {
            Nil -> acc
            is Cons -> reverse(acc.cons(list.head), list.tail)
        }

        fun <A> concat(
            list1: PersistentList<A>, // warning: stack overflow
            list2: PersistentList<A>
        ): PersistentList<A> = when (list1) {
            Nil -> list2
            is Cons -> concat(list1.tail, list2).cons(list1.head)
        }
    }
}
