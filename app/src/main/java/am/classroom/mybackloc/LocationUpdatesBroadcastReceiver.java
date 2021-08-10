package am.classroom.mybackloc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.util.Date;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = "LUBroadcastReceiver";
    public static final String ACTION_PROCESS_UPDATES = "com.freakyjolly.demobackgroundlocation.action" + ".PROCESS_UPDATES";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action))
            {
                Utils.setLocationUpdatesResult(context, DateFormat.getDateTimeInstance().format(new Date()));
                Utils.getLocationUpdates(context,intent,"PROCESS_UPDATES");
                Log.d("Location ","is Started");
            }
        }
    }
}
