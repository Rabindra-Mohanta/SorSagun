package alkusi.mahato.sarsogune.NotificationByFcm.Api

import alkusi.mahato.sarsogune.NotificationByFcm.Model.PushNotification
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    //server key for send message notification
    //AAAAOZD7cO4:APA91bEgi6_ZPDNyOXk68CVvfDKgJirlEIVlCp8R_L9gDg2192L_sy2EuWK3GumhkTJWbwTZqrJTyyKDOQctlhpDmKW8q1gfjzsrr5ADK-wrs0rg3HmcxGpRH6PC1hi5-aEhjqqT5fif
    @Headers("Content-Type:application/json","Authorization:key=AAAAOZD7cO4:APA91bEgi6_ZPDNyOXk68CVvfDKgJirlEIVlCp8R_L9gDg2192L_sy2EuWK3GumhkTJWbwTZqrJTyyKDOQctlhpDmKW8q1gfjzsrr5ADK-wrs0rg3HmcxGpRH6PC1hi5-aEhjqqT5fif")
    @POST("fcm/send")
    fun sendNotification(@Body notification:PushNotification):retrofit2.Call<PushNotification>
}