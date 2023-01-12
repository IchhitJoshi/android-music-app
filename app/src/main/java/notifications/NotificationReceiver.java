package notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import services.MusicService;

import static notifications.ApplicationClass.ACTION_CLOSE;
import static notifications.ApplicationClass.ACTION_NEXT;
import static notifications.ApplicationClass.ACTION_PLAY;
import static notifications.ApplicationClass.ACTION_PREVIOUS;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);
        if (actionName != null){
            switch (actionName){
                case ACTION_PLAY:
                    serviceIntent.putExtra("ActionName", "playPause");
                    context.startService(serviceIntent);
                    break;

                case ACTION_NEXT:
                    serviceIntent.putExtra("ActionName", "Next");
                    context.startService(serviceIntent);
                    break;

                case ACTION_PREVIOUS:
                    serviceIntent.putExtra("ActionName", "Previous");
                    context.startService(serviceIntent);
                    break;

                case ACTION_CLOSE:
                    serviceIntent.putExtra("ActionName", "Close");
                    context.startService(serviceIntent);

            }
        }
    }
}
