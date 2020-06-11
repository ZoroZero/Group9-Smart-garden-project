package Login_RegisterUser;

import android.content.Context;
import android.content.SharedPreferences;

import Helper.DeviceInformation;

public class UserLoginManagement {
    private static UserLoginManagement instance;
    private static Context ctx;
    private DeviceInformation[] user_device_information;

    private static final String SHARED_PREF_NAME = "mysharedpref12";
    private static final String KEY_USER_ID = "user_ID";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_EMAIL = "user_email";
    private UserLoginManagement(Context context) {
        ctx = context;
    }

    public static synchronized UserLoginManagement getInstance(Context context) {
        if (instance == null) {
            instance = new UserLoginManagement(context);
        }
        return instance;
    }

    public void userLogin(int user_id, String username, String email){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, user_id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();

    }

    // Check if user is logged in
    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    // Logout
    public boolean logOut(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        return true;
    }

    // Store user device information
    public void storeUserDevices(String[] device_id_list, String[] device_name_list,
                                 String[] linked_device_id_list, String[] linked_device_name_list,
                                 String[] device_type_list){
        user_device_information = new DeviceInformation[device_id_list.length];
        for(int i =0; i < device_id_list.length; i++){
            user_device_information[i] = new DeviceInformation(device_id_list[i], device_name_list[i],
                    linked_device_id_list[i], linked_device_name_list[i], device_type_list[i]);

            //device_list_Str.append(user_device_information[i].toString()).append(";");
        }
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public int getUserId(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }

    public String getUserEmail(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public DeviceInformation[] getDevice_list(){
        return user_device_information;
    }
}
