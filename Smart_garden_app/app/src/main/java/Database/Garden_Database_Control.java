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
import com.example.smartgarden.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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
                                        jsonObject.getString("username"), jsonObject.getString("email"));
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
                                   final String amount, final String linked_sensor_id,
                                   final Context context, final VolleyCallBack callBack){
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
                params.put("linked_sensor_id", linked_sensor_id);
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
    public static void recordMeasurement(final DeviceInformation device, final Context context){
        String database_ip = Helper.getConfigValue(context, "database_server");
        //final String user_id = SharedPrefManager.getInstance(context).getUserId()+"";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.RECORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("{")) {
                            int start = response.indexOf('{');
                            int end = response.indexOf('}');
                            try {
                                JSONObject jsonObject = new JSONObject(response.substring(start, end+1));
                                Log.d("JSON", String.valueOf(jsonObject));
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
                params.put("topic", device.getDevice_name() + "/" + device.getDevice_id());
                params.put("type", device.getDevice_type().replace(" Sensor", ""));
                params.put("device_id", device.getDevice_id());
                params.put("user_id", UserLoginManagement.getInstance(context).getUserId()+"");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                1,
                2));
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }


    //Record measurement
    public static void recordMeasurement_v2(final Vector<DeviceInformation> devices, final Vector<Integer> positions,
                                            final Context context, final VolleyCallBack callBack){
        String database_ip = Helper.getConfigValue(context, "database_server");
        //final String user_id = SharedPrefManager.getInstance(context).getUserId()+"";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.RECORD_URL,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(String response) {
                        //Log.i("response", response);
                        callBack.onSuccessResponse(response);
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
}
