package com.example.patientsportal.objects

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.example.patientsportal.R
import com.example.patientsportal.entities.WeekDates
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateConverter {

    fun dateConverter(date: String): String {

        val parts = date.split("/")
        val year = parts[0]
        val month = parts[1]
        val day = parts[2]

        return "$day/$month/$year"
    }

    fun dateAppointmentConverter(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy - HH:mm'h'", Locale.getDefault())
        return outputFormat.format(inputFormat.parse(date) as Date)
    }

    fun dateSimpleConverter(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return outputFormat.format(inputFormat.parse(date) as Date)
    }

    fun dateSimplePatientTestConverter(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateObj = inputFormat.parse(date)
        return outputFormat.format(dateObj!!)
    }

    fun calculateDateDifference(originalDate: String): String {
        val formato = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val originalFormatDate = formato.parse(originalDate)

        val differenceMillis = Date().time - originalFormatDate!!.time
        val differenceHours = TimeUnit.MILLISECONDS.toHours(differenceMillis)
        val differenceDays = TimeUnit.MILLISECONDS.toDays(differenceMillis)
        val differenceMonths = differenceDays / 30
        val differenceYears = differenceDays / 365

        return when {
            differenceHours.toInt() in 0..1 -> "Hace $differenceHours hora"
            differenceHours in 2..24 -> "Hace $differenceHours horas"
            differenceDays.toInt() == 1 -> "Hace $differenceDays día"
            differenceDays in 2..30 -> "Hace $differenceDays días"
            differenceMonths.toInt() == 1 -> "Hace $differenceMonths mes"
            differenceMonths in 2..11 -> "Hace $differenceMonths meses"
            differenceYears.toInt() == 1 -> "Hace $differenceYears año"
            else -> "Hace $differenceYears años"
        }
    }

    fun checkPassedPrescriptionDate(eD: String): Boolean {
        val calendarStart = Calendar.getInstance()
        val endDate = calendarStart.time
        val splitDates = eD.split(" - ")
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val endPrescriptionDate = sdf.parse(splitDates[1])
        return endDate > endPrescriptionDate!!
    }

    fun setDifferenceBetweenDates(eD: String, context: Context): SpannableString {

        // Divide la cadena en dos fechas separadas por " - "
        val splitDates = eD.split(" - ")

        // Convierte las cadenas de texto en objetos Date
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val startDate = sdf.parse(splitDates[0])
        val endDate = sdf.parse(splitDates[1])

        // Calcula la diferencia en milisegundos
        val millieSecondsDifference = endDate!!.time - startDate!!.time

        // Convierte la diferencia de milisegundos a días
        val daysDifference = TimeUnit.MILLISECONDS.toDays(millieSecondsDifference)

        if (daysDifference < 180) {
            // Si es menor a 180 días, cambia el color de la segunda fecha a rojo
            val spannableString = SpannableString(eD)
            val startIndex = eD.indexOf(splitDates[1])
            val endIndex = startIndex + splitDates[1].length
            val colorRojo = context.resources.getColor(R.color.red, null)
            spannableString.setSpan(ForegroundColorSpan(colorRojo), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            return spannableString
        }

        return SpannableString(eD)
    }

    fun dateForBuyGoodDrugs(dateStr: String): String {
        return try {
            // Convertir la cadena de fecha a un objeto Date
            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val date = dateFormat.parse(dateStr)

            // Formatear la fecha en el formato deseado
            val transformedFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale.getDefault())
            transformedFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            "Fecha inválida"
        }
    }

    fun calculateFinishPregnancyDate(startPregnancyDate: String): String {
        // Formato de fecha
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Obtener la fecha de inicio del embarazo como objeto Date
        val startDate = dateFormat.parse(startPregnancyDate)

        // Calcular la fecha de parto sumando 280 días (40 semanas) a la fecha de inicio
        val calendar = Calendar.getInstance()
        calendar.time = startDate!!
        calendar.add(Calendar.DAY_OF_YEAR, 280)

        // Obtener la fecha de parto como objeto Date
        val finishDate = calendar.time

        // Formatear la fecha de parto como String en el mismo formato dd/MM/yyyy
        return dateFormat.format(finishDate)
    }

    fun generateWeekDates(startPregnancyDate: String): ArrayList<WeekDates> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val weekDatesList = ArrayList<WeekDates>()

        // Obtener la fecha de inicio del embarazo como objeto Date
        val startDate = dateFormat.parse(startPregnancyDate)

        // Calcular las fechas según el patrón y agregar a la lista de WeekDates
        var startDate2 = startDate

        for (week in 1..40) { // Limitar a 40 semanas
            calendar.time = startDate2!!

            // Obtener las fechas para cada campo de WeekDates con el formato personalizado
            val pregnancyTestDate = dateFormat.format(startDate2)
            calendar.add(Calendar.DAY_OF_YEAR, 2)
            val motherChangesDate = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 2)
            val babyDevelopmentDate = dateFormat.format(calendar.time)

            // Agregar WeekDates a la lista
            weekDatesList.add(
                WeekDates(
                    pregnancyTestDate,
                    motherChangesDate,
                    babyDevelopmentDate
                )
            )

            // Actualizar la fecha actual para el próximo ciclo
            calendar.time = startDate2
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            startDate2 = calendar.time
        }

        return weekDatesList
    }
}