package com.example.patientsportal.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import androidx.core.content.ContextCompat
import com.example.patientsportal.R
import com.example.patientsportal.entities.dbEntities.PregnancyContractions
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

object CreatorPDF {

    fun createPdf(context: Context, pregnancyContractionsList: ArrayList<PregnancyContractions>, fileName: String) {
        val pdfWriter = PdfWriter(FileOutputStream(File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)))
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Agregar el encabezado con el logo y el título
        val logoDrawable = ContextCompat.getDrawable(context, R.drawable.icon_main_full)
        if (logoDrawable != null) {
            val logoBitmap = drawableToBitmap(logoDrawable)
            val logoByteArray = bitmapToByteArray(logoBitmap)

            val logo = Image(ImageDataFactory.create(logoByteArray))
            document.add(logo)
        }
        document.add(Paragraph(context.getString(R.string.hist_rico_de_contracciones_en_mi_embarazo)))

        // Iterar sobre cada conjunto de contracciones
        for (pc in pregnancyContractionsList) {
            // Agregar fecha encima de la tabla
            val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("'Día' d MMM yyyy 'a las' HH:mm 'hs'", Locale.getDefault())
            document.add(Paragraph(outputFormat.format(inputFormat.parse(pc.date)!!)).setTextAlignment(TextAlignment.CENTER))

            // Crear tabla para contracciones
            val table = Table(floatArrayOf(1f, 1f, 1f)) // 3 columnas con igual ancho
            table.useAllAvailableWidth()

            // Encabezados de la tabla
            table.addCell(Paragraph(context.getString(R.string.duraci_n)))
            table.addCell(Paragraph(context.getString(R.string.intervalo)))
            table.addCell(Paragraph(context.getString(R.string.inicio_y_fin)))

            // Agregar el contenido de cada contracción a la tabla
            for (contraction in pc.contractions) {
                table.addCell(Paragraph(contraction.duration))
                table.addCell(Paragraph(contraction.interval))
                table.addCell(Paragraph(contraction.startAndFinish))
            }

            // Agregar la tabla al documento
            document.add(table)

            // Salto de página entre cada conjunto de contracciones
            document.add(Paragraph("\n\n"))
        }

        document.close()
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

}