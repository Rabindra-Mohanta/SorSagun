package alkusi.mahato.sarsogune.NotificationByFcm

import alkusi.mahato.sarsogune.AllActivity.ActivityNotificationClick
import alkusi.mahato.sarsogune.AllActivity.HomeActivity
import alkusi.mahato.sarsogune.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFcmService: FirebaseMessagingService() {
    private val channelId = "sorSagune"
    val TAG = "MyFcmService"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(this, ActivityNotificationClick::class.java);
        intent.putExtra("email",message.data["email"])
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        createNotificationChannel(manager)

        val intent1 = PendingIntent.getActivities(this,0, arrayOf(intent),PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this,channelId);
            notification.setContentTitle(message.data["title"])
          notification.setContentText(message.data["body"])

        notification.setSmallIcon(R.drawable.logo)
        notification .setAutoCancel(true)
        notification.setPriority(NotificationCompat.PRIORITY_MAX)
        notification.setContentIntent(intent1)

        manager.notify(Random.nextInt(),notification.build())
    }

    private fun createNotificationChannel(manager: NotificationManager)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(channelId,"sorSagune",NotificationManager.IMPORTANCE_HIGH)
            channel.description = "sorSagune"
            channel.enableLights(true);
            manager.createNotificationChannel(channel)
        }
        else
        {

        }
    }
}



