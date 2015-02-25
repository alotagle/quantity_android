package com.mobile.dev.quantity.view;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mobile.dev.quantity.R;
import com.mobile.dev.quantity.controller.MyAdapter;
import com.mobile.dev.quantity.model.Producto;
import com.mobile.dev.quantity.util.QuantityDictionay;
import com.mobile.dev.quantity.util.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * Created by Luis.Cariño on 20/01/2015.
 */
public class ProductItemFragment extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    //parameters from constructor
    private String mParam1;

    //view elements
    private View rootView;
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //listener of interface declared on adapter,
    //Mandatory for receive click callback on item click listener from Recycler View Custom Adapter
    MyAdapter.OnItemClickedInteractionListener mListenerOnClick;

    //Instance of interface for communication with paren Activity
    private OnProductItemFragmentInteraction fragmentInteraction;


    //helper object for session management
    SessionManager mSessionManager = null;


    public static ProductItemFragment newInstance(String param1, String param2) {
        ProductItemFragment fragment = new ProductItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    /*Mandatory empty constructor*/
    public ProductItemFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.product_item_fragment,container,false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(),5);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mListenerOnClick = new MyAdapter.OnItemClickedInteractionListener() {
            @Override
            public void onItemClicked(Producto producto) {

                QuantityDictionay.debugLog("-------------->>>>>>"+producto.getDescripcion());
                //send product item selected to parent Activity
                fragmentInteraction.itemSelected(producto);
            }
        };
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        if(QuantityDictionay.checkNetworkConnection(getActivity())){
            //There's an active internet connection
            //Create url for service
            String host = "https://quantitydgtic.appspot.com";
            String path = "_ah/api/productos/v1/listaProductos";
            Uri.Builder mUBuilder = null;
            mUBuilder = Uri.parse(host).buildUpon();
            mUBuilder.path(path);

            //create session object
            mSessionManager = new SessionManager(getActivity());
            //get user details
            Map<String,String> dataSession = mSessionManager.getUserDetails();

            //append value for user name stored on session manager shared prederences
            mUBuilder.appendQueryParameter("usuario", dataSession.get(mSessionManager.KEY_CLAVE));

            //build string url
            String serviceUrl = mUBuilder.build().toString();

            //execute async request
            new GetProductCatalogTask().execute(serviceUrl);

        }else{
            Toast.makeText(getActivity(),
                    getString(R.string.network_connection_error),
                    Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Private Async Task to get product catalog and load  response to view
     */
    private class GetProductCatalogTask extends AsyncTask<String,Void,String>{
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

            //parse the string response to Java Object
            Producto [] p = parseResponse(result);

            //set the parsed collection to adapter dataset
            mAdapter = new MyAdapter(p,mListenerOnClick,getActivity());
            mRecyclerView.setAdapter(mAdapter);

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

            Producto [] productos = null;

            if(projectElement != null){

                QuantityDictionay.debugLog("LOS PRODUCTOS--->"+projectElement.toString());

                //Use Gson to map response
                Gson gson = new Gson();
                //set type of response
                Type collectionType = new TypeToken<Producto[]>(){}.getType();
                //get java objects from json string
                productos = gson.fromJson(projectElement.toString(),collectionType);

                QuantityDictionay.debugLog("PARSING SIZE---->"+productos.length);

            }


            return productos;

        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentInteraction = (OnProductItemFragmentInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Interface to communicate with any Activity that implement an instance of this fragment*/

    public interface OnProductItemFragmentInteraction{
        public void itemSelected(Producto product);
    }


}
