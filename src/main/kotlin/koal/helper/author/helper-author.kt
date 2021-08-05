package koal.helper.author

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File

data class ClassInfo(val name: String, val hasAuthorComment: Boolean, val classHeadLine: Int)

fun addAuthor(fp: String): ClassInfo {
    val claz = File(fp).name.substringBefore(".")
    println(claz)
    var lines = FileUtils.readLines(File(fp))
    var classInfo = ClassInfo(claz, true, -1)
    val count = lines.filter { it.contains("@author") }.count()
    if (count <= 0) {
        lines.forEachIndexed { index, s ->
            if (s.contains("class ${claz}") || s.contains("interface ${claz}")  || s.contains("enum ${claz}")) {
                classInfo = ClassInfo(claz, false, index)
            }
        }
        val newLines = ArrayList(lines)
        newLines.add(
            classInfo.classHeadLine, "/**\n" +
                    " * @author lame\n" +
                    " */"
        )
        FileUtils.writeLines(File(fp), "utf-8", newLines)
    }
    return classInfo
}

fun main(args: Array<String>) {
    val listFiles = FileUtils.listFiles(
        File("/media/lame/0DD80F300DD80F30/koal/kcsp-admin/kcsp-boot/kcsp-boot-module-system/src/main/java/kl/kcsp/modules"),
        FileFilterUtils.suffixFileFilter("java"), TrueFileFilter.INSTANCE
    )
    listFiles.forEach {
        val addAuthor = addAuthor(it.absolutePath)
        if (addAuthor.hasAuthorComment) {
            println("已经添加了备注${addAuthor.name},无视之")
        } else {
            println("增加备注${addAuthor.name},欢喜之")
        }
    }
}