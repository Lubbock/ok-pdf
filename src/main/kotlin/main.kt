import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
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

//目录提取
fun extractOutline(fp: String) {
    PDDocument.load(File(fp))
        .use { doc -> //文档目录对象
            val catalog = doc.documentCatalog
            //文档刚要对象
            val outline = catalog?.documentOutline
            var item = outline?.firstChild
            if (outline != null) {
                while (item != null) {
                    println("Item:${item.title}")
                    var secondChild = item.firstChild
                    while (secondChild != null) {
                        println("Item:${secondChild.title}")
                        secondChild = secondChild.nextSibling
                    }
                    item = item.nextSibling
                }
            }
        }
}

fun main(args: Array<String>) {
//    extractPdfImg(Paths.get("").toAbsolutePath().toString()+"/src/main/resources/haskhell.pdf",Paths.get("").toAbsolutePath().toString()+"/src/main/resources")
    mergeToPdf("${Paths.get("").toAbsolutePath().toString()}/src/main/resources")
    println(Paths.get("").toAbsolutePath().toString())

}