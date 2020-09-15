package com.saveetha.busmate2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.nearby.messages.internal.Update;

import java.util.Calendar;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /*
    Button loginButton;
    EditText username, password ;
    TextView result;

*/
    PasswordManager pm ;
    Button notInBus,inBus,logout,outside,missed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getPermissions();

        notInBus = findViewById(R.id.notInBus);
        inBus = findViewById(R.id.inBus);
        outside = findViewById(R.id.outsideButton);
        logout = findViewById(R.id.logoutButton);
        missed = findViewById(R.id.missedButton);
        notInBus.setOnClickListener(this);
        inBus.setOnClickListener(this);
        outside.setOnClickListener(this);
        missed.setOnClickListener(this);
        logout.setOnClickListener(this);
        pm = new PasswordManager(this);
        if(!pm.verifyPassword()){
            //start login activity
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }

        /*
        else {
            startActivity(new Intent(this, MapsActivity.class));
            finish();
        }
        */
        /*
        username = findViewById(R.id.usernameEditText);
        password = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        result = findViewById(R.id.resultTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final String uname,pwd;
                uname = username.getText().toString();
                pwd = password.getText().toString();

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String url = "http://192.168.0.200:8080/aish.php?username="+uname+"&password="+pwd;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                result.setText("Response is: "+ response);
                                //result.setText(url);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        result.setText("That didn't work!:"+error.toString());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("username", uname);
                        params.put("password", pwd);

                        return params;
                    }
                };

// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        Calendar timenow = Calendar.getInstance();
        Calendar fiveforty = Calendar.getInstance();
        Calendar eightfifteen = Calendar.getInstance();
        fiveforty.set(Calendar.HOUR,5);
        fiveforty.set(Calendar.MINUTE,40);
        fiveforty.set(Calendar.SECOND,0);
        fiveforty.set(Calendar.AM_PM,Calendar.AM);
        eightfifteen.set(Calendar.HOUR,8);
        eightfifteen.set(Calendar.MINUTE,15);
        eightfifteen.set(Calendar.SECOND,0);
        eightfifteen.set(Calendar.AM_PM,Calendar.AM);
        if(timenow.compareTo(fiveforty)>0 &&timenow.compareTo(eightfifteen)<0){
            //ok
        }
        else{
            stopService(new Intent(this,LocationUpdater.class));
            setContentView(R.layout.timeout);
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
                    , 100);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.inBus){
            startService(new Intent(this, LocationUpdater.class));
            finish();
        }
        if(v.getId() == R.id.notInBus){
            stopService(new Intent(this,LocationUpdater.class));
            stopService(new Intent(this,BusmateNetClient.class));
            startActivity(new Intent(this, MapsActivity.class));
            finish();
        }
        if(v.getId() == R.id.missedButton){
            startActivity(new Intent(this,Main2Activity.class));
        }
        if(v.getId() == R.id.outsideButton){
stopService(new Intent(this, LocationUpdater.class));
finish();
        }
        if(v.getId() == R.id.logoutButton){
new PasswordManager(this).resetAll();
finish();
        }
    }
}
