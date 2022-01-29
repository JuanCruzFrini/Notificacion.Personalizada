package com.example.notificacionpersonalizada

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {

    //onClick en los botones
    val ACCION_DEMO = "com.example.notificacionpersonalizada.ACCION_DEMO"
    val EXTRA_PARAM = "com.example.notificacionpersonalizada.EXTRA_PARAM"
    var contador = 0

    val ID_NOTIFICACION=1
    val ID_CANAL = "chanell_id"
    lateinit var notificationManager:NotificationManager
    lateinit var notificacion: NotificationCompat.Builder
    lateinit var remoteView:RemoteViews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Creamos una vista remota a partir del layout que hicimos,
        //e inicializamos los valores de cada view
        remoteView = RemoteViews(packageName, R.layout.custom_notification)
        remoteView.setImageViewResource(R.id.reproducir, android.R.drawable.ic_media_play)
        remoteView.setImageViewResource(R.id.imagen, R.mipmap.ic_launcher)
        remoteView.setTextViewText(R.id.titulo, "Notificacion personalizada")
        remoteView.setTextColor(R.id.titulo, Color.BLACK)
        remoteView.setTextViewText(R.id.texto, "Texto de la notificacion")
        remoteView.setTextColor(R.id.texto, Color.BLACK)

        //asociar activity a la notificacion
        val i = Intent(this, MainActivity::class.java)
        val pendingI = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)

        //onClick botones
        val intent = Intent()
            .setAction(ACCION_DEMO)
            .putExtra(EXTRA_PARAM, "Otro parametro")
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteView.setOnClickPendingIntent(R.id.reproducir, pendingIntent)


        //lanzamos la notificacion
        notificacion = NotificationCompat.Builder(this, ID_CANAL)
            .setContentIntent(pendingI)
            .setContent(remoteView)
            .setPriority(Notification.PRIORITY_MAX)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Notificacion personalizada")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26){
            val channel = NotificationChannel(ID_CANAL, "Nombre del canal", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Descripcion del canal"
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(ID_NOTIFICACION, notificacion.build())

        //onClick botones
        val filtro = IntentFilter(ACCION_DEMO)
        registerReceiver(ReceptorAnuncio(), filtro)
    }

    //onClick botones
    inner class  ReceptorAnuncio : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val param = p1!!.getStringExtra(EXTRA_PARAM)
            Toast.makeText(p0, "Parametro: $param", Toast.LENGTH_SHORT).show()
            contador++
            remoteView.setTextViewText(R.id.texto, "Contador: $contador")
            notificationManager.notify(ID_NOTIFICACION, notificacion.build())
        }

    }
}

