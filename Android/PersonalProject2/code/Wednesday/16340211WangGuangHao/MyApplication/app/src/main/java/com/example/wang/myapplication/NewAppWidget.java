package com.example.wang.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);//实例化RemoteView,其对应相应的Widget布局
            Intent i = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, FLAG_UPDATE_CURRENT);
            updateView.setOnClickPendingIntent(R.id.new_app_widget, pi); //设置点击事件
            ComponentName me = new ComponentName(context, NewAppWidget.class);
            appWidgetManager.updateAppWidget(me, updateView);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent ){
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if(intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")){
            Bundle bundle = intent.getExtras();
            String name = "";
            Collection c = null;
            if (bundle != null && ((Collection)bundle.getSerializable("Adgoods")) !=null
                    ){
                //name = ((Collection)bundle.getSerializable("Adgoods")).getName();
                c =  ((Collection)bundle.getSerializable("Adgoods"));
                name = c.getName();
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                views.setTextViewText(R.id.appwidget_text, "今日推荐 "+ name);
                views.setImageViewResource(R.id.appwidget_image,R.mipmap.full_star);
                ComponentName me = new ComponentName(context, NewAppWidget.class);
                Intent DetailsIntent = new Intent(context, DetailsActivity.class);
                DetailsIntent.putExtra("Collection",c);
                PendingIntent mPendingIntent=PendingIntent.getActivity(context,0,DetailsIntent,FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.new_app_widget, mPendingIntent);
                appWidgetManager.updateAppWidget(me, views);
            }


        }

    }

}

