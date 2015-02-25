package com.mobile.dev.quantity.controller;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mobile.dev.quantity.R;
import com.mobile.dev.quantity.model.Producto;
import com.mobile.dev.quantity.util.QuantityDictionay;

/**
 * This class is used to handle how information is shown on Recycler View
 * Created by Luis.Cariño on 20/01/2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public static Producto[] mDataset;

    //interface variable reference from Fragment/Activity implementing this adapter
    private static OnItemClickedInteractionListener mListener;

    //parent conext
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder
   implements View.OnClickListener{

        public CardView mCardView;
        public TextView mTextView;
        public TextView mTextViewDescription;
        public ImageView imageView;
        public ViewHolder(View v){
            super(v);
            v.setOnClickListener(this);
            mCardView = (CardView) v.findViewById(R.id.card_view_items);
            mTextView = (TextView)v.findViewById(R.id.textViewPrice);
            imageView = (ImageView) v.findViewById(R.id.imageViewProduct);
            mTextViewDescription = (TextView) v.findViewById(R.id.textViewDescription);
        }


        @Override
        public void onClick(View v) {
            mListener.onItemClicked(mDataset[getPosition()]);

        }
    }

    public MyAdapter(Producto[] myDataset){
       mDataset = myDataset;
    }

    public MyAdapter(Producto[] myDataset, OnItemClickedInteractionListener listener, Context context){
        mDataset = myDataset;
        mListener = listener;
        mContext = context;
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(mDataset != null){
            viewHolder.mTextView.setText("$"+(CharSequence) mDataset[position].getPrecio());
            viewHolder.mTextViewDescription.setText(mDataset[position].getDescripcion());

            int resId = mContext.getResources().getIdentifier(mDataset[position].getImagen(),"drawable",mContext.getPackageName());
            QuantityDictionay.debugLog(mDataset[position].getImagen());
            QuantityDictionay.debugLog(String.valueOf(resId));
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.imageView.setImageResource(resId);

        }else{

        }



    }


    @Override
    public int getItemCount() {
        if(mDataset != null)
            return mDataset.length;
        else
            return 0;
    }



    /**
     * This interface must be implemented by fragments that use this class as
     * custom adapter to allow an interaction in this class to be communicated
     * to the fragment and potentially other fragments contained in that
     * activity.
     *
     */
    public interface OnItemClickedInteractionListener {

        public void onItemClicked(Producto producto);

    }

}
