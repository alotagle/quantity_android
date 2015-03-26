package com.mobile.dev.quantity.view;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.mobile.dev.quantity.model.Producto;
import com.mobile.dev.quantity.util.QuantityDictionay;
import com.mobile.dev.quantity.view.dataStorage.SelectedItems;


/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        /*setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));*/
        setListAdapter(new ArrayAdapter<Producto>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, SelectedItems.ITEMS));

    }

    @Override
    public void onResume() {
        super.onResume();
        //Set long click listener to list in order to remove items on Long press
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                QuantityDictionay.debugLog(String.valueOf(position));
                //get item form collection
                //DummyContent.DummyItem item  = DummyContent.ITEMS.get(position);
                Producto item = SelectedItems.ITEMS.get(position);
                //remove item from list
                Double total = removeItemFromList(item);
                //send total result after delete to paren Activity
                mListener.onFragmentInteraction(String.valueOf(total));
                return false;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /*@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }


    /**
     * this method allow parent activity to add elements to main list for this fragment
     * @param producto added to list
     * @return the sum of elements within adapter
     */
    public Double addItemTolist(Producto producto) {
        Double total = 0d;

        //ArrayAdapter<DummyContent.DummyItem> adapter =( ArrayAdapter<DummyContent.DummyItem>)getListAdapter();
        ArrayAdapter<Producto> adapter = (ArrayAdapter<Producto>)getListAdapter();
        //DummyContent.DummyItem dummyItem = new DummyContent.DummyItem(String.valueOf(adapter.getCount()+1),value);
        itemExist(producto);
        adapter.add(producto);
        adapter.notifyDataSetChanged();

        //get the total sum of elements within adapter
        for(int i = 0; i < adapter.getCount(); i++){
            total += Double.valueOf(adapter.getItem(i).getPrecio());
        }

        return total;
    }


    public Double removeItemFromList(Producto item){
        Double total = 0d;

        //ArrayAdapter<DummyContent.DummyItem> adapter =( ArrayAdapter<DummyContent.DummyItem>)getListAdapter();
        ArrayAdapter<Producto> adapter = (ArrayAdapter<Producto>)getListAdapter();
        adapter.remove(item);
        adapter.notifyDataSetChanged();

        //get the total sum of elements within adapter
        for(int i = 0; i < adapter.getCount(); i++){
            total += Double.valueOf(adapter.getItem(i).getPrecio());
        }

        return total;
    }


    public void clearList(){
        SelectedItems.ITEMS.clear();
        ArrayAdapter<Producto> adapter = (ArrayAdapter<Producto>)getListAdapter();
        adapter.notifyDataSetChanged();
    }

    private boolean itemExist(Producto item) {
        for (int i=0; i<getListView().getAdapter().getCount(); i++) {
            if ((getListView().getAdapter().getItem(i)).equals(item)){
                return true;
            }
        }
        return false;
    }

}
