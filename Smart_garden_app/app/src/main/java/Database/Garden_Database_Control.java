package Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import Helper.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import AppNotification.NotificationHelper;
import DeviceController.Device_Control;
import Helper.DeviceInformation;
import Helper.Helper;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;

public class Garden_Database_Control {
    // Login function
    public static void Login(final String username, final String password, final Context context){
        String database_ip = Helper.getConfigValue(context, "database_server");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                Toast.makeText(context, "Login successful",
                                        Toast.LENGTH_LONG).show();
                                UserLoginManagement.getInstance(context).userLogin(jsonObject.getInt("user_ID"),
                                        jsonObject.getString("username"), jsonObject.getString("password"), jsonObject.getString("email"));
                            } else {
                                Toast.makeText(context, jsonObject.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }


    // Register function
    public static void RegisterUser(final String username, final String password, final String email,
                                    final Context context, final VolleyCallBack callback){
        String database_ip = Helper.getConfigValue(context, "database_server");
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Processing request");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            callback.onSuccessResponse(response);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("email", email);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Register device
    public static void registerDevice(final String device_id, final String device_name,
                                      final String linked_device_id, final String linked_device_name,
                                      final String threshold, final Context context, final VolleyCallBack callBack){
        String database_ip = Helper.getConfigValue(context, "database_server");
        // final ProgressDialog progressDialog = new ProgressDialog(context);
        final String user_id = UserLoginManagement.getInstance(context).getUserId()+"";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.REGISTER_DEVICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            callBack.onSuccessResponse(response);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", device_id);
                params.put("user_id", user_id);
                params.put("device_name", device_name);
                params.put("linked_device_id", linked_device_id);
                params.put("linked_device_name", linked_device_name);
                params.put("threshold", threshold);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }


    // Add new plant
    public static void addNewPlant(final String plant_name, final String buy_date, final String buy_location,
                                   final String amount, final Context context, final VolleyCallBack callBack){
        String database_ip = Helper.getConfigValue(context, "database_server");
        final String user_id = UserLoginManagement.getInstance(context).getUserId()+"";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.ADD_PLANT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callBack.onSuccessResponse(response);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("plant_name", plant_name);
                params.put("user_id", user_id);
                params.put("buy_date", buy_date);
                params.put("buy_location", buy_location);
                params.put("amount", amount);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }


    // Fetch devices info
    public static void FetchDevicesInfo(final Context context, final VolleyCallBack callBack){
        final String user_id = UserLoginManagement.getInstance(context).getUserId()+"";
        String database_ip = Helper.getConfigValue(context, "database_server");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callBack.onSuccessResponse(response);

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Fetch plants info
    public static void FetchPlantsInfo(final Context context, final VolleyCallBack callBack){
        final String user_id = UserLoginManagement.getInstance(context).getUserId()+"";
        String database_ip = Helper.getConfigValue(context, "database_server");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.FETCH_PLANT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")) {
                                callBack.onSuccessResponse(response);
                            }else{
                                Toast.makeText(context, jsonObject.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }


    //Record measurement
    public static void recordMeasurement_v2(final Vector<DeviceInformation> devices, final Vector<Integer> positions,
                                            final Context context){
        String database_ip = Helper.getConfigValue(context, "database_server");
        //final String user_id = SharedPrefManager.getInstance(context).getUserId()+"";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.RECORD_URL,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Failed")){
                            return;
                        }
                        //Log.i("response", response);
                        Vector<DeviceInformation> sensors = UserLoginManagement.getInstance(context).getSensor();
                        int index = response.indexOf("<br");
                        String change_response = response.substring(0, index);
                        if (change_response.equals(""))
                            return;
                        String[] responses = change_response.split("\n");
                        for (String res : responses) {
                            Log.i("Message", res);
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                String message = jsonObject.getString("message");
                                if (message.equals("Need change")) {
                                    int position = jsonObject.getInt("position");
                                    int new_Intensity = jsonObject.getInt("new_intensity");
                                    Device_Control.turnDeviceLightIntensity(sensors.get(position).getLinked_device_id(),
                                            sensors.get(position).getLinked_device_name(), context, new_Intensity + "");

                                    // Send notification
                                    NotificationHelper.displayDeviceNotification(sensors.get(position), "Warning",
                                            "Device " + sensors.get(position).getDevice_id() + " change output" + sensors.get(position).getLinked_device_id() + " power to " + new_Intensity*100/255,
                                            context);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                for(int i = 0; i < devices.size(); i++) {
                    params.put("topic["+i+"]", devices.get(i).getDevice_name() + "/" + devices.get(i).getDevice_id());
                    params.put("type["+i+"]", devices.get(i).getDevice_type().replace(" Sensor", ""));
                    params.put("device_id[" + i+"]", devices.get(i).getDevice_id());
                    params.put("linked_device_id[" + i+"]", devices.get(i).getLinked_device_id());
                    params.put("position[" + i+"]", positions.get(i) + "");
                }
                params.put("user_id", UserLoginManagement.getInstance(context).getUserId() + "");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                1,
                2));
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Update output status
    public static void updateOutputStatus(final String device_id, final String status, final Context context){
        String database_ip = Helper.getConfigValue(context, "database_server");
        //final String user_id = SharedPrefManager.getInstance(context).getUserId()+"";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.UPDATE_OUTPUT_STATUS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Status", status);
                        Log.i("Update response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", device_id);
                params.put("status", status);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Get device last reading
    public static void getDeviceLastReading(final String device_id, final Context context, final VolleyCallBack callBack){
        String database_ip = Helper.getConfigValue(context, "database_server");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.GET_MEASUREMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")) {
                                callBack.onSuccessResponse(response);
                            }else{
                                Toast.makeText(context, jsonObject.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", device_id);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Get device last reading
    public static void getOutputStatus(final String device_id, final Context context, final VolleyCallBack callBack){
        String database_ip = Helper.getConfigValue(context, "database_server");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.GET_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")) {
                                callBack.onSuccessResponse(response);
                            }else{
                                Toast.makeText(context, jsonObject.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", device_id);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Change device threshold
    public static void changeDeviceThreshold(final String device_id, final String threshold, final Context context, final VolleyCallBack callBack){
        String database_ip = Helper.getConfigValue(context, "database_server");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.CHANGE_THRESHOLD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")) {
                                callBack.onSuccessResponse(response);
                            }else{
                                Toast.makeText(context, jsonObject.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", device_id);
                params.put("new_threshold", threshold);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }


    // Change device threshold
    public static void changeDeviceSettings(final String input_id, final String new_input_name,
                                           final String new_output_id, final String new_output_name, final String new_threshold,
                                           final Context context, final VolleyCallBack callBack){
        String database_ip = Helper.getConfigValue(context, "database_server");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.CHANGE_DEVICE_SETTING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")) {
                                callBack.onSuccessResponse(response);
                            }else{
                                Toast.makeText(context, jsonObject.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("input_id", input_id);
                params.put("new_input_name", new_input_name);
                params.put("new_output_id", new_output_id);
                params.put("new_output_name", new_output_name);
                params.put("new_threshold", new_threshold);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Change device threshold
    public static void removePlant(final String plant_name, final String buy_date, final String buy_location,
                                   final Context context){
        String database_ip = Helper.getConfigValue(context, "database_server");
        final String user_id = UserLoginManagement.getInstance(context).getUserId() + "";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.REMOVE_PLANT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("plant_name", plant_name);
                params.put("buy_date", buy_date);
                params.put("buy_location", buy_location);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Change plant setting
    public static void changePlantSetting(final String plant_name, final String buy_date, final String new_buy_location,
                                          final String new_amount, final Context context, final VolleyCallBack callBack){
        String database_ip = Helper.getConfigValue(context, "database_server");
        final String user_id = UserLoginManagement.getInstance(context).getUserId() + "";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.CHANGE_PLANT_SETTING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callBack.onSuccessResponse(response);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("plant_name", plant_name);
                params.put("buy_date", buy_date);
                params.put("new_buy_location", new_buy_location);
                params.put("new_amount", new_amount);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }
}
