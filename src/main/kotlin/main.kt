import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import javax.imageio.ImageIO

fun extractPdfImg() {
    val document = PDDocument.load(File("/media/lame/0DD80F300DD80F30/code/ok-pdf/src/main/resources/haskhell.pdf"))
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
                    FileOutputStream("/media/lame/0DD80F300DD80F30/code/ok-pdf/src/main/resources/hashhell/${System.currentTimeMillis()}.png").use {
                        ImageIO.write(image, "png", it)
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    println("开始pdf文件转换")
    PDDocument().use { doc ->
        Files.list(File("/media/lame/0DD80F300DD80F30/code/ok-pdf/src/main/resources/hk").toPath())
            .forEach { filePath ->
                val page = PDPage()
                doc.addPage(page)
                val drawImage = PDImageXObject.createFromFile(filePath.toFile().absolutePath, doc)
                val contents = PDPageContentStream(doc, page).use {
                    it.drawImage(drawImage, 9f, 10f, 566f, 735f)
                }
            }
        doc.save("/media/lame/0DD80F300DD80F30/code/ok-pdf/src/main/resources/haskhell3.pdf")
        doc.close()
    }
}