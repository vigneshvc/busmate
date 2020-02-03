package com.saveetha.busmate2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    /*
    Button loginButton;
    EditText username, password ;
    TextView result;

*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();

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

        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
                    , 100);
        }
    }
}
