package Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.smartgarden.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
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
                                        jsonObject.getString("username"));
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
    public static void RegisterUser(final String username, final String password,
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
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Register device
    public static void registerDevice(final String device_id, final String device_name,
                                      final String linked_device_id, final String linked_device_name, final Context context){
        String database_ip = Helper.getConfigValue(context, "database_server");
        // final ProgressDialog progressDialog = new ProgressDialog(context);
        final String user_id = UserLoginManagement.getInstance(context).getUserId()+"";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://" + database_ip + Constants.REGISTER_DEVICE_URL,
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
                params.put("device_id", device_id);
                params.put("user_id", user_id);
                params.put("device_name", device_name);
                params.put("linked_device_id", linked_device_id);
                params.put("linked_device_name", linked_device_name);
                return params;
            }
        };
        Database_RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }


    // Add new plant
    public static void addNewPlant(final String plant_name, final String buy_date, final String buy_location, final String amount, final Context context, final VolleyCallBack callBack){
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
}
