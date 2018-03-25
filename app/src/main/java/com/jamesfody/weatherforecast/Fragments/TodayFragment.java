package com.jamesfody.weatherforecast.Fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.FormatUtils;

/**
 * Created by Jim on 11/28/2017.
 *
 * Creation of the Today Fragment
 */

public class TodayFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.today_fragment, container, false);

        // Gather data from the bundle
        boolean sunUp = getArguments().getBoolean("sunUp");
        String dayIcon = getArguments().getString("dayDesc");
        String nightIcon = getArguments().getString("nightDesc");
        String rise = getArguments().getString("sunrise");
        String set = getArguments().getString("sunset");
        String llColor = getArguments().getString("todayTheme");

        // Set color theme
        LinearLayout  todayLL = rootView.findViewById(R.id.today_ll);
        todayLL.setBackgroundColor(Color.parseColor(llColor));

        // Find widgets and set values
        TextView daytimeTV = rootView.findViewById(R.id.daytime_tv);
        TextView eveningTV = rootView.findViewById(R.id.evening_tv);
        daytimeTV.setText(rise);
        eveningTV.setText(set);

        ImageView dayTimeIcon = rootView.findViewById(R.id.daytime_icon);
        ImageView nightTimeIcon = rootView.findViewById(R.id.evening_icon);

        String[] resID = {dayIcon, nightIcon};
        int[] imgID = FormatUtils.getWeatherIconName(getActivity(), resID, sunUp);

        dayTimeIcon.setImageResource(imgID[0]);
        nightTimeIcon.setImageResource(imgID[1]);

        return rootView;
    }
}
