package com.mobile.dev.quantity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import com.mobile.dev.quantity.view.ItemFragment;
import com.mobile.dev.quantity.view.ProductItemFragment;
import com.mobile.dev.quantity.view.dataStorage.SelectedItems;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity implements ItemFragment.OnFragmentInteractionListener,
        ProductItemFragment.OnProductItemFragmentInteraction,
        CashConfirmationDialog.CashConfirmationListener {


    private TextView display;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .acceptCreditCards(true)
            .languageOrLocale("es_MX")
            .rememberUser(true)
            .merchantName("Quantity")
            .clientId("AR5BKRVgoI5ZwOqWbss_IhusbzgzmbStai51H9sRjcl4FPFmX-a-b_0zOwel3oW2m1bG582yeI91wFO_");
    private double numCuenta = 0;


    //session object helper
    SessionManager mSessionManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (TextView) findViewById(R.id.textViewDisplay);

        // Create session manager object
        mSessionManager = new SessionManager(getApplicationContext());

        // Paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
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
        numCuenta = Double.parseDouble(id);
        display.setText(String.format( "Total: $ %.2f", numCuenta));
    }

    //messages send from ProducItemFragment
    @Override
    public void itemSelected(Producto product) {

        ItemFragment mItemFragment = (ItemFragment)getFragmentManager().findFragmentById(R.id.fragment);
        if(mItemFragment!=null){
            Double res =  mItemFragment.addItemTolist(product);
            display.setText(String.format( "Total: $ %.2f", res ));
            numCuenta = res;
        }
    }

    //Handles click on Credit Card Button
    public void creditCardPayment(View view) {

        BigDecimal pay = new BigDecimal(numCuenta);

        if (pay.compareTo(BigDecimal.ZERO) != 0) {
            PayPalPayment payment = new PayPalPayment(pay, "MXN", "Cuenta Quantity",
                    PayPalPayment.PAYMENT_INTENT_SALE);

            Intent intent = new Intent(this, PaymentActivity.class);

            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

            startActivityForResult(intent, 0);
        } else {
            Toast.makeText(getApplicationContext(), "Agrega por lo menos un producto a la compra.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    Toast.makeText(
                            getApplicationContext(),
                            "Pago realizado satisfactoriamente", Toast.LENGTH_LONG)
                            .show();

                    limpiarLista();

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    //Handles clicks on Cash Button
    public void cashPayment(View view) {

        String displayText = (String) display.getText();
        String total = displayText.substring(8);
        QuantityDictionay.debugLog(total);

        if (Double.valueOf(total) != 0) {
            DialogFragment dialogFragment = CashConfirmationDialog.newInstance(total);
            dialogFragment.show(getFragmentManager(),"CashFragmentDialog");
        } else {
            Toast.makeText(getApplicationContext(), "Agrega por lo menos un producto a la compra.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCashDialogPositiveClick(String email) {
        QuantityDictionay.debugLog(email);
        QuantityDictionay.debugLog("COMPRADOS--->"+String.valueOf(SelectedItems.ITEMS.size()));
        QuantityDictionay.debugLog(generateReceiptUrl(SelectedItems.ITEMS,email));

        if (email == "") {
            limpiarLista();
        } else {
            //generate url appending the items selected
            String url = generateReceiptUrl(SelectedItems.ITEMS, email);
            new SendReceiptTask().execute(url);
        }
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


        String host = "https://quantitydgticfinal.appspot.com";
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
    private class SendReceiptTask extends AsyncTask<String, Void, String> {
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

            limpiarLista();

            //manually parsing to productos
            JsonParser parser = new JsonParser();
            JsonObject rootObject = parser.parse(result).getAsJsonObject();
            JsonElement projectElement = rootObject.get("respuestaRecibo");

            if(projectElement.getAsBoolean()) {
                Toast.makeText(getApplicationContext(), R.string.receipt_success, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "Recibo no enviado, por favor intente de nuevo", Toast.LENGTH_LONG).show();
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

    private void limpiarLista() {
        ItemFragment itemFragment = (ItemFragment)getFragmentManager().findFragmentById(R.id.fragment);
        itemFragment.clearList();

        numCuenta = 0;
        display.setText(String.format( "Total: $ %.2f", numCuenta));
    }

}
