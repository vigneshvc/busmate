package com.saveetha.busmate2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;

public class PasswordManager {

    public static final String SHARED_PREFERENCE_ID = "BusmatePref";
    private static final String USERNAME_KEY = "username";
    private final String PASSWORD_KEY="password";
    private final String STOPID_KEY="stop_id";
    private final String PREFERED_BUS_ID_KEY="pref_bus_id";
    private final String TAG = "PasswordManager";
    SharedPreferences sp;
    SharedPreferences.Editor speditor;
    Context context;
    public PasswordManager(Context c){
        context = c;
        try {
            sp = c.getSharedPreferences(SHARED_PREFERENCE_ID, Context.MODE_PRIVATE);
        }catch (Exception e){
            Log.e(TAG,"Error! "+e.toString());
        }
        speditor = sp.edit();
        speditor.apply();
    }
    int getStopId(){return sp.getInt(STOPID_KEY,-1);}
    int getPreferedBusId(){return sp.getInt(PREFERED_BUS_ID_KEY,-1);}
    void setStopId(int stopId){
        speditor.putInt(STOPID_KEY,stopId);
        speditor.apply();
    }
    void setPreferedBusId(int busid){
        speditor.putInt(PREFERED_BUS_ID_KEY,busid);
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
    Boolean isLogged=false;
    Boolean verifyPassword(){

        if(!isLoggedIn()){
            resetAll();
            return false;
        }
        if(login(getUserName(),getPassword())){
            return true;
        }else{
            return false;
        }
    }



    public static JSONObject volleySyncRequest(Context c, String url) {

        // configurazione della webRequest
        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(), future, future);
        RequestQueue requestQueue = Volley.newRequestQueue(c);

        request.setRetryPolicy(new DefaultRetryPolicy( 5000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

        // esecuzione sincrona della webRequest
        try {
            // limita la richiesta bloccante a un massimo di 10 secondi, quindi restituisci
            // la risposta.
            return future.get(3000,TimeUnit.SECONDS);

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }



    Boolean login(final String username, final String password){

        if(isLogged)return true;

        RequestFuture<JSONObject> requestFuture= RequestFuture.newFuture();


       /*
        String url = context.getString(R.string.server_address)+"login.php?username="+username+"&password="+password;
        Log.d(TAG,url);
        JSONObject jsonObject = volleySyncRequest(context,url);
        if(jsonObject==null){
            return false;
        }
        */

        isLogged=true;

        //final String url = context.getString(R.string.server_address)+"/login.php?username="+username+"&password="+password;
        final String url = context.getString(R.string.server_address)+"/login.php";
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        isLogged = response.equals("true"); //dummy for now
                        Log.i(TAG,"RESPONSE:"+response);
                        try {
                            JSONObject jb = new JSONObject(response);
                            String status = jb.getString("status");
                            Log.i(TAG,"Status returned - "+status);
                            if(status.equals("success")){
                                isLogged = true;
                                String user = jb.getString("username");
                                int stop_id = Integer.parseInt(jb.getString("stop_id"));
                                int pref_bus_id = Integer.parseInt(jb.getString("pref_bus_id"));
                                boolean res = updateUsername(user) && updatePassword(password);
                                setPreferedBusId(pref_bus_id);
                                setStopId(stop_id);
                            }else{
                                //failed login
                                isLogged = false;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"error-"+error.toString());
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


        try{sleep(2000);}catch (InterruptedException e){Log.e(TAG, e.toString());}
        queue.add(stringRequest);
        //try{this.wait(5000); Log.i(TAG,"returning "+isLogged);return isLogged;}catch (Exception e){Log.e(TAG,"Error here! -"+e.toString());}


        return isLogged;
    }
    void resetAll(){
        Log.i(TAG,"Resetted");
        speditor.putString(PASSWORD_KEY,"");
        speditor.putString(USERNAME_KEY,"");
        speditor.putInt(STOPID_KEY,-1);
        speditor.putInt(PREFERED_BUS_ID_KEY,-1);
        speditor.apply();
        //return getUserName().equals("") && getPassword().equals("");
    }

    Boolean isLoggedIn(){
        return !(getUserName().equals(""));
    }


}
