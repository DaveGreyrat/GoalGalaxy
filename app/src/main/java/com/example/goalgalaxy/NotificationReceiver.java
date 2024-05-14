package com.example.goalgalaxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Создание и отправка уведомления
        NotificationHelper notificationHelper = new NotificationHelper();
        notificationHelper.createNotification(context,"Заголовок уведомления", "Текст уведомления");
    }
}
