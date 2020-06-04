package Database;

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
}
