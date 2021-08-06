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
            if (s.contains("class ${claz}") || s.contains("interface ${claz}") || s.contains("enum ${claz}")) {
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

data class LineEndComment(val modifyLine: String, val index: Int, val endComment: String)

/*
× 行尾注释转换
* */
fun transformEndLineComment(fp: String) {
    var lines = FileUtils.readLines(File(fp))
    val comments = ArrayList<LineEndComment>()
    val newLines = ArrayList<String>()
    lines.forEachIndexed { index, line ->
        var ap = false
        if (line.contains("//")) {
            if (!line.trim().startsWith("//")
                && !line.substringBefore("//").contains("*")
                && !line.substringAfter("//").contains("\"")
            ) {
                ap = true
                val sb = StringBuilder()
                for (c in line) {
                    if (c.isWhitespace()) {
                        sb.append(c)
                    } else {
                        break
                    }
                }
                newLines.add("${sb}/*\n${sb}*${line.substringAfter("//")}\n${sb}*/")
                newLines.add(line.substringBefore("//"))
                println("修改文件 fp:$fp\nindex:$index\t line:${line.substringBefore("//")}")
            }
        }
        if (!ap) {
            newLines.add(line)
        }
    }
    FileUtils.writeLines(File(fp), "utf-8", newLines)
}

fun modifyokToOk(fp: String) {
    val newLines = FileUtils.readLines(File(fp)).map {
        it.replace("Result.ok", "Result.OK")
    }
    FileUtils.writeLines(File(fp), "utf-8", newLines)
    println("修改Result.ok to OK ${fp.substringBeforeLast(".")}")
}

fun main(args: Array<String>) {
    val listFiles = FileUtils.listFiles(
        File("/media/lame/0DD80F300DD80F30/koal/kcsp-admin/kcsp-boot/kcsp-boot-module-system/src/main/java/kl/kcsp/modules"),
        FileFilterUtils.suffixFileFilter("java"), TrueFileFilter.INSTANCE
    )
    listFiles.forEach {
        modifyokToOk(it.absolutePath)
    }
}