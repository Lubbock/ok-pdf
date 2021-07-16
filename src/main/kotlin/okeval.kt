/**
 * 有限自动机？
 * */

interface State {
    fun accept(c: Char): State?
}

class A() : State {
    var collectChar: ArrayList<Char> = ArrayList<Char>()

    var tempCollectChar: ArrayList<Char> = ArrayList()

    var nextCheck: Int = 0


    override fun accept(c: Char): State {
//        print(c)
        var ret: State = this

        if (c == '@') {
            if (nextCheck == 0) {
                nextCheck++
            } else {
                collectChar.addAll(tempCollectChar)
                tempCollectChar.clear()
                tempCollectChar.add(c)
                nextCheck = 1
            }
        } else if (c == ':') {
            when (nextCheck) {
                1 -> {
                    nextCheck++
                    tempCollectChar.add(c)
                }
                0 -> {
                    collectChar.addAll(tempCollectChar)
                    tempCollectChar.clear()
                    tempCollectChar.add(c)
                }
                2 -> {
                    println()
                    println(String(collectChar.toCharArray()))
                    ret = B()
                }
            }
        } else {
            collectChar.add(c)
        }
        return ret
    }
}

class B() : State {
    var innerChar: ArrayList<Char> = ArrayList()
    override fun accept(c: Char): State {
        if (c == ';') {
            return A()
        }
        print(c)
        return this
    }
}

class Close() : State {
    override fun accept(c: Char): State? {
        println("\n------------\n结束")
        return null
    }
}

fun main(args: Array<String>) {
    val s = "yaml@::kube.nfs->ip;form@::NFSIP,false,nfsIp"
    var a: State? = A()
    s.forEach {
        a = a?.accept(it)
    }
}