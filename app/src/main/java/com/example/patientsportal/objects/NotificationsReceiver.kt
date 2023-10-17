package com.example.patientsportal.objects

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.patientsportal.MainActivity
import com.example.patientsportal.R
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.Notification
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationsReceiver: BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra(context?.getString(R.string.title_tag))
        val message = intent?.getStringExtra(context?.getString(R.string.message_tag))
        val idPatient = intent?.getIntExtra(context?.getString(R.string.idpatient_tag), 0)

        val style = NotificationCompat.BigTextStyle()
            .bigText(message)

        // Crear una notificación utilizando NotificationCompat.Builder
        val notificationBuilder = context?.let {
            NotificationCompat.Builder(it, "1")
                .setSmallIcon(R.drawable.icon_main)
                .setContentTitle(title)
                .setStyle(style)
                .setContentText(message)
                .setAutoCancel(true)
        } // Cierra la notificación cuando se toca

        if (context != null) {

            val dbPatientsPortal = DbPatientsPortal(context)

            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val dateFormatted = sdf.format(System.currentTimeMillis())

            if (idPatient != null && title != null && message != null) {
                val notification = Notification()
                notification.patient = dbPatientsPortal.readPatient(idPatient)
                notification.date = dateFormatted
                notification.title = title
                notification.description = message
                notification.readed = "No"

                val idNotification = dbPatientsPortal.createPatientNotification(notification)

                if (idNotification > 0) {

                    // Crear un intent para abrir una actividad cuando se toca la notificación
                    val resultIntent = Intent(context, MainActivity::class.java)
                    resultIntent.putExtra(context.getString(R.string.title_tag), title)
                    resultIntent.putExtra(context.getString(R.string.idnotification_tag), idNotification.toInt())
                    val pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    notificationBuilder?.setContentIntent(pendingIntent)

                    // Obtener el servicio de notificaciones y mostrar la notificación
                    if (context.let {
                            ActivityCompat.checkSelfPermission(
                                it,
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        } != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }


                    if (notificationBuilder != null) {
                        context.let { NotificationManagerCompat.from(it) }.notify(1, notificationBuilder.build())
                    }
                }
            }
        }
    }
}