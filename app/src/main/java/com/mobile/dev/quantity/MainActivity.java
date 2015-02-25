package com.mobile.dev.quantity;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.mobile.dev.quantity.view.CashConfirmationDialog;
import com.mobile.dev.quantity.view.CreditCardConfirmationDialog;
import com.mobile.dev.quantity.view.ItemFragment;
import com.mobile.dev.quantity.view.ProductItemFragment;
import com.mobile.dev.quantity.view.ReceiptConfirmationDialog;
import com.mobile.dev.quantity.view.dataStorage.SelectedItems;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity implements ItemFragment.OnFragmentInteractionListener,
        ProductItemFragment.OnProductItemFragmentInteraction,
        CashConfirmationDialog.CashConfirmationListener,
        ReceiptConfirmationDialog.ReceiptConfirmationListener{


    private TextView display;


    //session object helper
    SessionManager mSessionManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (TextView) findViewById(R.id.textViewDisplay);

        //create session manager object
        mSessionManager = new SessionManager(getApplicationContext());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if(id == R.id.action_logout){
            mSessionManager.logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }

    //messages send from ItemFragment
    @Override
    public void onFragmentInteraction(String id) {
        display.setText("TOTAL: $"+id);
        
    }

    //messages send from ProducItemFragment
    @Override
    public void itemSelected(Producto product) {

        ItemFragment mItemFragment = (ItemFragment)getFragmentManager().findFragmentById(R.id.fragment);
        if(mItemFragment!=null){
           Double res =  mItemFragment.addItemTolist(product);
            display.setText("TOTAL: $"+String.valueOf(res));
        }

    }

    //Handles click on Credit Card Button
    public void creditCardPayment(View view){
        DialogFragment dialogFragment = new  CreditCardConfirmationDialog();
        dialogFragment.show(getFragmentManager(),"CreditCardDialogFragment");
    }

    //Handles clicks on Cash Button
    public void cashPayment(View view){

        String displayText = (String) display.getText();

            String total = displayText.substring(8);
            QuantityDictionay.debugLog(total);
            DialogFragment dialogFragment = CashConfirmationDialog.newInstance(total);
            dialogFragment.show(getFragmentManager(),"CashFragmentDialog");


    }

    @Override
    public void onCashDialogPositiveClick(String message) {
        DialogFragment dialogFragment = new ReceiptConfirmationDialog();
        dialogFragment.show(getFragmentManager(),"ReceiptFragmentDialog");
    }

    @Override
    public void onReceiptDialogPositiveClick(String email) {

        QuantityDictionay.debugLog(email);
        QuantityDictionay.debugLog("COMPRADOS--->"+String.valueOf(SelectedItems.ITEMS.size()));
        QuantityDictionay.debugLog(generateReceiptUrl(SelectedItems.ITEMS,email));

        //generate url appending the items selected
        String url = generateReceiptUrl(SelectedItems.ITEMS,email);
        new SendReceiptTask().execute(url);

    }

    public String generateReceiptUrl(List<Producto> items,String email){

        //create string with products selected
        StringBuilder cadenaProductos = new StringBuilder();

        Iterator iterator = items.iterator();

        while(iterator.hasNext()){
            Producto p = (Producto)iterator.next();
            cadenaProductos.append(p.getDescripcion()).append(":").append(p.getPrecio());
            if(iterator.hasNext())
                cadenaProductos.append(";");
        }


        String host = "https://quantitydgtic.appspot.com";
        String path = "_ah/api/recibos/v1/enviarRecibo";
        Uri.Builder mUBuilder = null;
        mUBuilder = Uri.parse(host).buildUpon();
        mUBuilder.path(path);
        mUBuilder.appendQueryParameter("asunto","prueba");
        mUBuilder.appendQueryParameter("cadenaProductos",cadenaProductos.toString());
        mUBuilder.appendQueryParameter("correo",email);
        mUBuilder.appendQueryParameter("mensaje","prueba");
        mUBuilder.appendQueryParameter("nombre","luis");


       return mUBuilder.build().toString();
    }



    /**
     * Private Async Task to send purchased products to server
     * and generate email confirmation
     */
    private class SendReceiptTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                response =  sendReceipt(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            QuantityDictionay.debugLog(result);
            ItemFragment itemFragment = (ItemFragment)getFragmentManager().findFragmentById(R.id.fragment);
            itemFragment.clearList();

            display.setText("TOTAL: $0.0");

            Toast.makeText(getApplicationContext(),R.string.receipt_success,Toast.LENGTH_LONG).show();

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
        private String sendReceipt(String myURl) throws IOException{
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
