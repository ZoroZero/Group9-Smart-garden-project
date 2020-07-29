package Helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.smartgarden.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Login_RegisterUser.UserLoginManagement;

public class Helper {
    private static final String TAG = "Helper";

    public static String getConfigValue(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

        return null;
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items)
    {
        for (String item : items) {
            if (inputStr.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public static DeviceInformation findDeviceWithDeviceId(String deviceId, Vector<DeviceInformation> devices){
        for(DeviceInformation deviceInformation: devices){
            if(deviceInformation.getDevice_id().equals(deviceId)){
                return deviceInformation;
            }
        }
        return null;
    }

    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
    }

    // Check if device has existed on user
    public static boolean checkUserHasDevice(String device_id, Context context){
        DeviceInformation[] user_device_information = UserLoginManagement.getInstance(context).getDevice_list();
        if(user_device_information == null){
            return false;
        }
        for (DeviceInformation deviceInformation : user_device_information) {
            if (device_id.equals(deviceInformation.getDevice_id())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidUsername(String name)
    {

        // Regex to check valid username.
        String regex = "^[aA-zZ]\\w{5,29}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the username is empty
        // return false
        if (name == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given username
        // and regular expression.
        Matcher m = p.matcher(name);

        // Return if the username
        // matched the ReGex
        return m.matches();
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}
