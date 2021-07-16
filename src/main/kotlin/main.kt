import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PageMode
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

fun extractPdfImg(fp: String, baseFp: String) {
    val document = PDDocument.load(File(fp))
    val pages = document.documentCatalog.pages.iterator()
    while (pages.hasNext()) {
        val page = pages.next()
        val cosNames = page.resources.xObjectNames.iterator()
        while (cosNames.hasNext()) {
            val cosname = cosNames.next()
            if (page.resources.isImageXObject(cosname)) {
                val imgObj = page.resources.getXObject(cosname)
                if (imgObj is PDImageXObject) {
                    val image = imgObj.image
                    val metadata = imgObj.metadata
                    FileOutputStream("${baseFp}/haskhell/${System.currentTimeMillis()}.png").use {
                        ImageIO.write(image, "png", it)
                    }
                }
            }
        }
    }
}

fun mergeToPdf(baseFp: String) {
    println("开始pdf文件转换")
    PDDocument().use { doc ->
        Files.list(File("${baseFp}/hk").toPath())
            .forEach { filePath ->
                val page = PDPage()
                doc.addPage(page)
                val drawImage = PDImageXObject.createFromFile(filePath.toFile().absolutePath, doc)
                val contents = PDPageContentStream(doc, page).use {
                    it.drawImage(drawImage, 9f, 10f, 566f, 735f)
                }
            }
        doc.save("${baseFp}/haskhell3.pdf")
        doc.close()
    }
}

data class Outline(val name: String, val page: Int, var outlines: List<Outline>?)


fun transformOutline(outline: Outline, parent: PDOutlineItem, doc: PDDocument) {
    val pdPage = doc.getPage(outline.page)
    val bookmark = PDOutlineItem()
    bookmark.title = outline.name
    val dest = PDPageFitWidthDestination()
    dest.page = pdPage
    bookmark.destination = dest
    parent.addLast(bookmark)
    outline.outlines?.forEach { e -> transformOutline(e, bookmark, doc) }
}

/**
 * 提取目录结构
 * */
fun copyOutlineStruct(copyFp: String, targetFp: String) {
    val outlines = analyzePDFOutline(copyFp)
    debuggerOutlines(outlines)
    PDDocument.load(File(targetFp)).use { doc ->
        val outline = PDDocumentOutline()
        doc.documentCatalog.pageMode = PageMode.USE_OUTLINES
        doc.documentCatalog.documentOutline = outline
        val root = PDOutlineItem()
        root.title = "HaskHell教程"
        outline.addFirst(root)
        outlines.forEach { transformOutline(it, root, doc) }
        outline.openNode()
        doc.save("/media/lame/0DD80F300DD80F30/code/ok-pdf/src/main/resources/haskhell4.pdf")
    }

}

private fun debuggerOutlines(outlines: ArrayList<Outline>) {
    for (outline in outlines) {
        println("${outline.name}\t- - - - ${outline.page}")
        outline.outlines?.forEach { println("\t${it.name}\t- - - - ${it.page}") }
    }
}

private fun analyzePDFOutline(copyFp: String): ArrayList<Outline> {
    val outlines = ArrayList<Outline>()
    //二级目录提取算法
    //只能呢个按顺序读取因为firstChild永远有值
    PDDocument.load(File(copyFp))
        .use { doc -> //文档目录对象
            //文档刚要对象
            val root = doc.documentCatalog?.documentOutline
            var degree1 = root?.firstChild
            if (root != null) {
                while (degree1 != null) {
                    val degree1Dest = degree1.destination
                    if (degree1Dest is PDPageDestination) {
                        val outline = Outline(degree1.title, degree1Dest.retrievePageNumber(), null)
                        var degree2 = degree1.firstChild
                        val degres2Outlines = ArrayList<Outline>()
                        while (degree2 != null) {
                            val degree2Dest = degree2.destination
                            if (degree2Dest is PDPageDestination) {
                                val degree2Outline = Outline(degree2.title, degree2Dest.retrievePageNumber(), null)
                                degres2Outlines.add(degree2Outline)
                            }
                            degree2 = degree2.nextSibling
                        }
                        outline.outlines = degres2Outlines
                        outlines.add(outline)
                    }
                    degree1 = degree1.nextSibling
                }
            }
        }
    return outlines
}


fun planetTy(Q: Double, q: Double, b: Int, num: Int = 0) {
    if (Q <= 0) {
        println("逃逸成功,逃逸次数${num + 1},逃逸动量${b}")
        return
    }
    val mm = q * b / 100.0
//    println("大数${Q};逃逸动量${b},逃逸数${q};逃逸资本${mm}")
    planetTy(Q - mm, q + mm, b, num = num + 1)
}

fun main(args: Array<String>) {
//    extractPdfImg(Paths.get("").toAbsolutePath().toString()+"/src/main/resources/haskhell.pdf",Paths.get("").toAbsolutePath().toString()+"/src/main/resources")
//    mergeToPdf("${Paths.get("").toAbsolutePath().toString()}/src/main/resources")
    println(Paths.get("").toAbsolutePath().toString())
    val analyze =
        analyzePDFOutline(Paths.get("").toAbsolutePath().toString() + "/src/main/resources/haskhell.pdf")
    debuggerOutlines(analyze)
//    extractOutline(
//        Paths.get("").toAbsolutePath().toString() + "/src/main/resources/haskhell.pdf",
//        Paths.get("").toAbsolutePath().toString() + "/src/main/resources/haskhell3.pdf"
//    )
}