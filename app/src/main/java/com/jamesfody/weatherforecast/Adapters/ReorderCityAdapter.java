package com.jamesfody.weatherforecast.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jamesfody.weatherforecast.Activities.ReorderCityActivity;
import com.jamesfody.weatherforecast.Interfaces.ItemTouchHelperAdapter;
import com.jamesfody.weatherforecast.Interfaces.ItemTouchHelperViewHolder;
import com.jamesfody.weatherforecast.Interfaces.OnStartDragListener;
import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.GeoCoderUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jim on 1/4/2018.
 *
 */

public class ReorderCityAdapter extends RecyclerView.Adapter<ReorderCityAdapter.CityViewHolder>
implements ItemTouchHelperAdapter{

    private static final String TAG = ReorderCityAdapter.class.getSimpleName();
    private OnItemClickListener mItemClickListener;
    private final OnStartDragListener mDragStartListener;

//    private static int viewHolderCount;
    private ReorderCityActivity activity = new ReorderCityActivity();

//    private int mNumberItems;
    private ArrayList<Weather> mWeatherData = new ArrayList<>();
    private ArrayList<String> mCityNames = new ArrayList<>();
    private Context mContext;
    private static final int item_type = 0;

    public ReorderCityAdapter(Context context, ArrayList<Weather> weather, ArrayList<String> cityNames,
                               OnStartDragListener dragListener) {
//        mNumberItems = weather.size();
        mWeatherData.addAll(weather);
        mCityNames.addAll(cityNames);
        mDragStartListener = dragListener;
        mContext = context;
//        viewHolderCount = 0;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int reorderListItem = R.layout.reorder_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(reorderListItem, viewGroup, false);

        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CityViewHolder holder, int position) {

        holder.gripper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP){
                    mDragStartListener.onDragStarted(holder);
                }
                return false;
            }

        });
        holder.bind(position);
    }

    @Override
    public int getItemViewType(int position){
        return item_type;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < mWeatherData.size() && toPosition < mWeatherData.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mWeatherData, i, i + 1);
                    Collections.swap(mCityNames, i, i + 1);
                }
            }
            else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mWeatherData, i, i - 1);
                    Collections.swap(mCityNames, i, i - 1);
                }
            }
            activity.setmCityNames(mCityNames);
            activity.setmWeatherData(mWeatherData);
            GeoCoderUtils.saveCityListToFile(mCityNames);
            notifyItemMoved(fromPosition, toPosition);
        }

        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public void setmOnItemClickListener(final OnItemClickListener mOnClickListener){
        this.mItemClickListener = mOnClickListener;
    }

    public interface OnStartDragListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    @Override
    public int getItemCount() {
        return mWeatherData.size();
    }

    class CityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {

        TextView cityName;
        TextView countryName;
        ImageView gripper;

        CityViewHolder(View itemView) {
            super(itemView);

            cityName = itemView.findViewById(R.id.item_city_name);
            countryName = itemView.findViewById(R.id.item_state_name);
            gripper = itemView.findViewById(R.id.gripper_iv);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {

            Weather currentCity = mWeatherData.get(position);

            double latitude = currentCity.getmLatitude();
            double longitude = currentCity.getmLongitude();

            cityName.setText(currentCity.getmCityName());
            countryName.setText(GeoCoderUtils.getStateAndCountry(latitude,longitude));
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.RED);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.RED);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null){
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }


}
