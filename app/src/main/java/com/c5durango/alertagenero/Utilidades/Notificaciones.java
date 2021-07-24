package com.c5durango.alertagenero.Utilidades;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.c5durango.alertagenero.Constantes;

public class Notificaciones {

    public static void crearNotificacionNormal(Context context, String CHANNEL, int icon, String titulo, String contenido, int ID_SERVICIO){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL);
            builder.setSmallIcon(icon);
            if(titulo.length() > 0)
                builder.setContentTitle(titulo);
            builder.setContentText(contenido);
            builder.setColor(Color.GRAY);
            builder.setPriority(NotificationCompat.PRIORITY_LOW);
            //builder.setLights(Color.MAGENTA, 1000, 1000);
            //builder.setDefaults(Notification.DEFAULT_SOUND);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(ID_SERVICIO, builder.build());

        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            final NotificationManager mNotific = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int importancia = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL, Constantes.NOMBRE_APP, importancia);
            notificationChannel.setDescription(contenido);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

            if(titulo.length() > 0 ){
                Notification notification =
                    new Notification.Builder(context, CHANNEL)
                            .setColor(Color.WHITE)
                            .setContentTitle(titulo)
                            .setContentText(contenido)
                            .setSmallIcon(icon)
                            .build();
                mNotific.notify(ID_SERVICIO, notification);
            } else {
                Notification notification =
                        new Notification.Builder(context, CHANNEL)
                                .setColor(Color.WHITE)
                                .setContentText(contenido)
                                .setSmallIcon(icon)
                                .build();
                mNotific.notify(ID_SERVICIO, notification);
            }
        }
    }


    /*public void crearNotificacionPersistente(Context context, Class clase, String CHANNEL, int icon, String titulo, String contenido, String descripc){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(context, clase);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, notificationIntent, 0);

            // Crear notificación de servicio activo
            Notification notification =
                    new Notification.Builder(context, CHANNEL)
                            .setColor(Color.WHITE)
                            .setContentTitle(titulo)
                            .setContentText(contenido)
                            .setSmallIcon(icon)
                            .setContentIntent(pendingIntent)
                            .build();

            // Crear al canal de notificación, pero solo en API 26+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence nombre = "Botón de pánico para comercios"; //2
                int importancia = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, nombre, importancia);
                notificationChannel.setDescription(descripc);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            clase.startForeground((int) Math.random()*10+1, notification);
            //Log.d(TAG,"Se inicio servicio para API 26+ - Desde servicio prueba!");

        } else{
            // Mostrar el otro tipo de notificación
        }
    }*/
}
