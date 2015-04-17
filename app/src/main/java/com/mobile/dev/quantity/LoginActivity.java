package com.mobile.dev.quantity;


import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mobile.dev.quantity.model.Producto;
import com.mobile.dev.quantity.util.QuantityDictionay;
import com.mobile.dev.quantity.util.SessionManager;
import com.mobile.dev.quantity.util.Validations;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    //UI elements
    private TextView nameTextView;
    private EditText userNameEditText,
                     passwordEditText;
    private Button loginButton;


    //logic variables
    boolean result = false; //login flag

    //helper object to handle session management
    SessionManager mSessionManager = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getReferenceToUIElements();

        //check for current session
        mSessionManager = new SessionManager(getApplicationContext());
        if(mSessionManager.isLoggedIn()){
            //there's and active session, forward to main activity
            Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(mainIntent);
            finish();
        }





    }

    /**
     * Get reference to UI elements for instance fields
     *
     */
    private void getReferenceToUIElements(){
        //name textview
        nameTextView = (TextView) findViewById(R.id.textViewNameLogin);
        Typeface mTypeface = Typeface.createFromAsset(getAssets(),"fonts/Pacifico.ttf");
        nameTextView.setTypeface(mTypeface);


        userNameEditText = (EditText) findViewById(R.id.editTextUsername);
        passwordEditText = (EditText) findViewById(R.id.editTextPass);

        //sets default values for login
        //userNameEditText.setText("paraLlevar");
        //passwordEditText.setText("Z0Gw8G8Mki");

        loginButton = (Button) findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Handle user authorization for login
     * @param username
     * @param password
     * @return true or false for login attempts
     */
    public boolean authUsers(String username, String password){

        //create query for User class
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Users");
        //constrain in the query
        parseQuery.whereEqualTo("username",username);
        parseQuery.whereEqualTo("password",password);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {


            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    Log.d("TAG", "Retrived " + parseObjects.size());
                    if(parseObjects.size() > 0){
                        /*Create an Intent that will start the Main Activity*/
                        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }else{
                    QuantityDictionay.debugLog("Error :::"+e.getMessage());

                }
            }
        });

        return result;
    }


    /**
   * Checks for valid input on login fields and set error messages
   * **/
    public boolean attemptLogin(){
        boolean status = true;

        if(!Validations.validate(Validations.VALIDATION_NAME, Validations.getStringNullable(userNameEditText.getText().toString()))) {
            userNameEditText.setError(getResources().getString(R.string.error_login_username));
            status = false;
        }

        if(!Validations.validate(Validations.VALIDATION_PASSWORD, Validations.getStringNullable(passwordEditText.getText().toString()))){
            passwordEditText.setError(getResources().getString(R.string.error_login_password));
            status = false;
        }

        return status;
    }



    /**
   * Handles click events on view items
   * */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.buttonLogin :
                if(attemptLogin()){
                    //login success
                    //authUsers(userNameEditText.getText().toString(),passwordEditText.getText().toString());
                    String username = userNameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String loginServiceUrl = generateUrl(username,password);
                    new RequestLoginTask().execute(loginServiceUrl);

                }

                break;
            default:
                break;
        }

    }


    /**
     * Handles url generation for login service
     */

    public String generateUrl(String username, String password){
        String host = "https://quantitydgtic.appspot.com";
        String path = "_ah/api/login/v1/loginUser";
        Uri.Builder mUBuilder = null;
        mUBuilder = Uri.parse(host).buildUpon();
        mUBuilder.path(path);
        mUBuilder.appendQueryParameter("password", password);
        mUBuilder.appendQueryParameter("usuario", username);

        return mUBuilder.build().toString();
    }
//https://quantitydgtic.appspot.com/_ah/api/login/v1/loginUser?password=costecho&usuario=memo


    /**
     * Private Async Task to get product catalog and load  response to view
     */
    private class RequestLoginTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                response =  getCatalog(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            QuantityDictionay.debugLog(result);

            //manually parsing to result to get node respuesta
            JsonParser parser = new JsonParser();
            JsonObject rootObject = parser.parse(result).getAsJsonObject();
            JsonElement respuestaElement = rootObject.get("respuesta");
            JsonElement usuarioElement = rootObject.get("usuario");

            if(respuestaElement.getAsBoolean()){
                QuantityDictionay.debugLog("LOG ON");
                //creates a session using the Session Manager object
                mSessionManager.createLoginSession(usuarioElement.getAsString());
                //Create an Intent that will start the Main Activity
                Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainIntent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),R.string.error_login_auth,Toast.LENGTH_LONG).show();
                QuantityDictionay.debugLog("LOG OFF");
            }




        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }



        /**
         * Given a URL establishes an HttpUrlConnection and
         * retrieves the response content as a String
         * @param myURl
         * @return
         * @throws IOException
         */
        private String getCatalog(String myURl) throws IOException{
            InputStream inputStreamFromServer = null;
            String responseAsString = null;
            try {
                //Creates and configures the HttpURLConnection
                URL url = new URL(myURl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setDoInput(true);

                //Starts the query
                httpURLConnection.connect();

                //gets response code
                int responseCode = httpURLConnection.getResponseCode();
                QuantityDictionay.debugLog("The response code is: "+responseCode);

                //gets the connection Input Stream
                inputStreamFromServer = httpURLConnection.getInputStream();

                //Convert the input stream to String
                responseAsString= inputStreamToString(inputStreamFromServer);

            }catch (IOException e){

                e.printStackTrace();
                QuantityDictionay.debugLog("Exception thrown on GetProductCatalogTask.getCatalog");

            }
            return responseAsString;
        }

        //Reads an InputStream and coverts it to a String
        public String inputStreamToString(InputStream inputStreamFromServer) throws IOException{
            //create bufferedReader from inputStream
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStreamFromServer));

            //hold variables
            String inputLine;
            StringBuffer responseAsString = new StringBuffer();

            //reading with buffer
            while((inputLine = bufferedReader.readLine()) != null){
                responseAsString.append(inputLine);
            }

            return responseAsString.toString();

        }

        //Reads server response as String and parse it to a POJO array
        public Producto[] parseResponse(String jsonAsString){

            //manually parsing to productos
            JsonParser parser = new JsonParser();
            JsonObject rootObject = parser.parse(jsonAsString).getAsJsonObject();
            JsonElement projectElement = rootObject.get("productos");

            QuantityDictionay.debugLog("LOS PRODUCTOS--->"+projectElement.toString());

            //Use Gson to map response
            Gson gson = new Gson();
            //set type of response
            Type collectionType = new TypeToken<Producto[]>(){}.getType();
            //get java objects from json string
            Producto [] productos = gson.fromJson(projectElement.toString(),collectionType);

            QuantityDictionay.debugLog("PARSING SIZE---->"+productos.length);


            return productos;

        }
    }
}
