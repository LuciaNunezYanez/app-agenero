package com.c5durango.alertagenero;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import com.c5durango.alertagenero.Servicios.ServicioWidget;
import com.c5durango.alertagenero.Utilidades.Utilidades;

import java.lang.ref.WeakReference;

public class PanicoWidget extends AppWidgetProvider {

    static WeakReference<Context> contextoGlobal;
    static String TAG = "ServicioWidget";
    static Button btnWidget;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        contextoGlobal = new WeakReference<>(context);
        Intent intentN = new Intent(context, ServicioWidget.class);
        PendingIntent pendingIntent;
        // btnWidget = (Button)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            pendingIntent = PendingIntent.getForegroundService(context, 0, intentN, 0);
        else
            pendingIntent = PendingIntent.getService(context, 0, intentN, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.panico_widget);
        views.setOnClickPendingIntent(R.id.btnAlertarWidget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

