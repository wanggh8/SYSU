package com.example.wang.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class DynamicReceiver extends BroadcastReceiver {
    private static final String DYNAMICACTION = "com.example.wang.myapplication.MyDynamicFilter";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DYNAMICACTION)) {    //动作检测
            Bundle bundle = intent.getExtras();
            String name = (String)bundle.get("collect");
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            Intent DetailsIntent = new Intent(context, MainActivity.class);
            DetailsIntent.putExtra("Collect","true");
            PendingIntent mPendingIntent=PendingIntent.getActivity(context,0,DetailsIntent,FLAG_UPDATE_CURRENT);

            //对Builder进行配置，此处仅选取了几个
            builder.setContentTitle("已收藏")   //设置通知栏标题：发件人
                    .setContentText(name)   //设置通知栏显示内容：短信内容
                    .setTicker("您有一条新消息")   //通知首次出现在通知栏，带上升动画效果的
                    .setSmallIcon(R.mipmap.empty_star)   //设置通知小ICON（通知栏），可以用以前的素材，例如空星
                    //.setFullScreenIntent(mPendingIntent, true)
                    .setContentIntent(mPendingIntent)   //传递内容
                    .setAutoCancel(true);   //设置这个标志当用户单击面板就可以让通知将自动取消
            Notification notify = builder.build();
            manager.notify(0,notify);
        }

        if (intent.getAction().equals("com.example.wang.myapplication.MyWidgetStaticFilter")) {    //动作检测
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName me = new ComponentName(context, NewAppWidget.class);
            Bundle bundle = intent.getExtras();
            String name = (String)bundle.get("collect");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.appwidget_text, "已收藏 "+ name);
            Intent DetailsIntent = new Intent(context, MainActivity.class);
            DetailsIntent.putExtra("Collect","true");
            PendingIntent mPendingIntent=PendingIntent.getActivity(context,0,DetailsIntent,FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.new_app_widget, mPendingIntent);
            appWidgetManager.updateAppWidget(me, views);

        }

    }

}