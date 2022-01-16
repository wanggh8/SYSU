package com.example.wang.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class StaticReceiver extends BroadcastReceiver {
    private static final String STATICACTION = "com.example.wang.myapplication.MyStaticFilter";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(STATICACTION)){
            Collection c = (Collection)intent.getSerializableExtra("Adgoods");
            String goodname =c.getName();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            Intent DetailsIntent = new Intent(context, DetailsActivity.class);
            DetailsIntent.putExtra("Collection",c);
            PendingIntent mPendingIntent=PendingIntent.getActivity(context,0,DetailsIntent,FLAG_UPDATE_CURRENT);

            //对Builder进行配置，此处仅选取了几个
            builder.setContentTitle("今日推荐")   //设置通知栏标题：发件人
                    .setContentText(goodname)   //设置通知栏显示内容：短信内容
                    .setTicker("您有一条新消息")   //通知首次出现在通知栏，带上升动画效果的
                    .setSmallIcon(R.mipmap.empty_star)   //设置通知小ICON（通知栏），可以用以前的素材，例如空星
                    .setContentIntent(mPendingIntent)   //传递内容
                    .setAutoCancel(true);   //设置这个标志当用户单击面板就可以让通知将自动取消
            Notification notify = builder.build();
            manager.notify(0,notify);
        }
    }
}