package DeviceController;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import Database.Garden_Database_Control;
import IOT_Server.IOT_Server_Access;

public class Device_Control {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void turnDeviceOn(String device_id, String device_name, Context context){
        String control_message = "[{ \"device_id\": \"" + device_id  +
                "\", \"values\" : [\"1\", \"255\"] } ]";
        IOT_Server_Access.Publish(device_name + "/" + device_id,
                control_message, context);
        Garden_Database_Control.updateOutputStatus(device_id, "On-255", context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void turnDeviceOff(String device_id, String device_name, Context context){
        String control_message = "[{ \"device_id\": \"" + device_id  +
                "\", \"values\" : [\"1\", \"0\"] } ]";
        IOT_Server_Access.Publish(device_name + "/" + device_id,
                control_message, context);
        Garden_Database_Control.updateOutputStatus(device_id, "On-0", context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void putDeviceToOff(String device_id, String device_name, Context context){
        String control_message = "[{ \"device_id\": \"" + device_id +
                "\", \"values\" : [\"0\", \"0\"] } ]";
        IOT_Server_Access.Publish(device_name + "/" + device_id,
                control_message, context);
        Garden_Database_Control.updateOutputStatus(device_id, "Off", context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void turnDeviceLightIntensity(String device_id, String device_name, Context context, String intensity){
        String control_message = "[{ \"device_id\": \"" + device_id  +
                "\", \"values\" : [\"1\", \"" + intensity + "\"] } ]";
        IOT_Server_Access.Publish(device_name + "/" + device_id,
                control_message, context);
        Garden_Database_Control.updateOutputStatus(device_id, "On-" + intensity, context);
    }
}
