package am.classroom.mybackloc;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Utils
{
    private static final String TAG = "UtilsClass";
    final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";
    public static float accuracy;
    static String addressFragments = "";
    static List<Address> addresses = null;
    public static final long UPDATE_INTERVAL = 5 * 1000;
    public static final float SMALLEST_DISPLACEMENT = 1.0F;
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    public static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 2;
    final static String CHANNEL_ID = "channel_01";

    static void setLocationUpdatesResult(Context context, String value)
    {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, value)
                .apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("MissingPermission")
    public static void getLocationUpdates(final Context context, final Intent intent, String broadcastevent)
    {
        LocationResult result = LocationResult.extractResult(intent);
        if (result != null)
        {
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            String nowDate = formatter.format(today);

            List<Location> locations = result.getLocations();
            Location firstLocation = locations.get(0);
            //getAddress(firstLocation,context);
            //firstLocation.getAccuracy();
            //firstLocation.getLatitude();
            //firstLocation.getLongitude();
            //firstLocation.getAccuracy();
            //firstLocation.getSpeed();
            //firstLocation.getBearing();
            LocationRequestHelper.getInstance(context).setValue("locationTextInApp","You are at "+" Latitude:"+firstLocation.getLatitude()+" Longitude:"+firstLocation.getLongitude());
            // LocationRequestHelper.getInstance(context).setValue("locationTextInApp","You are at "+getAddress(firstLocation,context)+"("+nowDate+") with accuracy "+firstLocation.getAccuracy()+" Latitude:"+firstLocation.getLatitude()+" Longitude:"+firstLocation.getLongitude()+" Speed:"+firstLocation.getSpeed()+" Bearing:"+firstLocation.getBearing());
            showNotificationOngoing(context, broadcastevent,"");
        }
    }

    public static String getAddress(Location location, Context context)
    {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        // Address found using the Geocoder.
        addresses = null;
        Address address = null;
        addressFragments="";
        try
        {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            address = addresses.get(0);
        }
        catch (IOException ioException)
        {
            Log.e(TAG, "error", ioException);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            Log.e(TAG, "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }
        if (addresses == null || addresses.size()  == 0)
        {
            Log.i(TAG, "ERORR");
            addressFragments = "NO ADDRESS FOUND";
        }
        else
            {
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++)
            {
                addressFragments = addressFragments+String.valueOf(address.getAddressLine(i));
            }
        }
        LocationRequestHelper.getInstance(context).setValue("addressFragments",addressFragments);
        return addressFragments;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotificationOngoing(Context context, String broadcastevent, String title)
    {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        String nowDate = formatter.format(today);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            String id = "id_product";

            // The user-visible name of the channel.
            CharSequence name = "Product";
            // The user-visible description of the channel.
            String description = "Notifications regarding our products";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(mChannel);

            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 123, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher) //your app icon
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle("Location update")
                    .setContentText(nowDate+"\n"+"AM-GPS is Running...")
                    .setAutoCancel(true).setContentIntent(pendingIntent)
                    .setNumber(1)
                    .setColor(255)
                    .setOngoing(true)
                    .setWhen(System.currentTimeMillis());
            notificationManager.notify(1, notificationBuilder.build());
        }
        else
        {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder notificationBuilder = new Notification.Builder(context)
                    .setContentTitle(title + DateFormat.getDateTimeInstance().format(new Date()) + ":" + accuracy)
                    .setContentText(addressFragments.toString())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .setOngoing(true)
                    .setStyle(new Notification.BigTextStyle().bigText(addressFragments.toString()))
                    .setAutoCancel(true)
                    .setOngoing(true);
            notificationManager.notify(3, notificationBuilder.build());
        }
    }

    public static void removeNotification(Context context)
    {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
