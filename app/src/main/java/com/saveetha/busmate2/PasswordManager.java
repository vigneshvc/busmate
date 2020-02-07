package com.saveetha.busmate2;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class PasswordManager {

    public static final String SHARED_PREFERENCE_ID = "BusmatePref";
    private static final String USERNAME_KEY = "username";
    private final String PASSWORD_KEY="password";

    SharedPreferences sp;
    SharedPreferences.Editor speditor;
    Context context;
    public PasswordManager(Context c){
        context = c;
        sp = c.getSharedPreferences(SHARED_PREFERENCE_ID,Context.MODE_PRIVATE);
        speditor = sp.edit();
        speditor.apply();
    }
    String getPassword(){
        return sp.getString(PASSWORD_KEY,"");
    }
    String getUserName(){
        return sp.getString(USERNAME_KEY,"");
    }
    Boolean updatePassword(String password){
        if(password.equals(""))return false;
        speditor.putString(PASSWORD_KEY,password);
        speditor.apply();
        return getPassword().equals(password);
    }
    Boolean updateUsername(String username){
        if(username.equals(""))return false;
        speditor.putString(USERNAME_KEY,username);
        speditor.apply();
        return getPassword().equals(username);
    }
    Boolean isLogged;
    Boolean verifyPassword(){

        if(!isLoggedIn()){
            resetAll();
            return false;
        }
        if(login(getUserName(),getPassword())){
            return true;
        }else{
            resetAll();
            return false;
        }
    }

    Boolean login(final String username, final String password){

        isLogged = false;
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = context.getString(R.string.server_address)+"/busmate.php?username="+username+"&password="+password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //TODO Implement the response verification

                        isLogged = response.equals("true"); //dummy for now
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLogged = false;
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };


        if(isLogged) {
            return updatePassword(password) &&
                    updateUsername(username);
        }else return false;
    }
    void resetAll(){
        speditor.putString(PASSWORD_KEY,"");
        speditor.putString(USERNAME_KEY,"");
        //return getUserName().equals("") && getPassword().equals("");
    }

    Boolean isLoggedIn(){
        return !(getUserName().equals(""));
    }


}
