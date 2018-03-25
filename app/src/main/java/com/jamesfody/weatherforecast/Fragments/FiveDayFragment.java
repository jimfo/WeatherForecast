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
 * Creation of the Five Day Fragment
 */

public class FiveDayFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.five_day_fragment, container, false);

        // Gather data from the bundle
        String highOne = getArguments().getString("highOne");
        String iconOne = getArguments().getString("iconOne");
        long dateOne = getArguments().getLong("dateOne");

        String highTwo = getArguments().getString("highTwo");
        String iconTwo = getArguments().getString("iconTwo");
        long dateTwo = getArguments().getLong("dateTwo");

        String highThree = getArguments().getString("highThree");
        String iconThree = getArguments().getString("iconThree");
        long dateThree = getArguments().getLong("dateThree");

        String highFour = getArguments().getString("highFour");
        String iconFour = getArguments().getString("iconFour");
        long dateFour = getArguments().getLong("dateFour");

        String highFive = getArguments().getString("highFive");
        String iconFive = getArguments().getString("iconFive");
        long dateFive = getArguments().getLong("dateFive");

        String fiveDayColor = getArguments().getString("fiveDayTheme");
        boolean sunUp = getArguments().getBoolean("sunUp");

        // Create array for image retrieval
        String[] resID = {iconOne, iconTwo, iconThree, iconFour, iconFive};
        int[] imgID = FormatUtils.getWeatherIconName(getActivity(), resID, sunUp);

        // Set color theme
        LinearLayout fiveDayLL = rootView.findViewById(R.id.five_day_ll);
        fiveDayLL.setBackgroundColor(Color.parseColor(fiveDayColor));

        // Find widgets and give them values
        TextView dayOneHighTV = rootView.findViewById(R.id.day_one_high);
        TextView dayOne = rootView.findViewById(R.id.day_one);
        ImageView imgView1 = rootView.findViewById(R.id.day_one_icon);
        imgView1.setImageResource(imgID[0]);
        dayOne.setText(FormatUtils.formatDay(dateOne));
        dayOneHighTV.setText(FormatUtils.shrinkUnit(highOne));

        TextView dayTwoHighTV = rootView.findViewById(R.id.day_two_high);
        TextView dayTwo = rootView.findViewById(R.id.day_two);
        ImageView imgView2 = rootView.findViewById(R.id.day_two_icon);
        imgView2.setImageResource(imgID[1]);
        dayTwo.setText(FormatUtils.formatDay(dateTwo));
        dayTwoHighTV.setText(FormatUtils.shrinkUnit(highTwo));

        TextView dayThreeHighTV = rootView.findViewById(R.id.day_three_high);
        TextView dayThree = rootView.findViewById(R.id.day_three);
        ImageView imgView3 = rootView.findViewById(R.id.day_three_icon);
        imgView3.setImageResource(imgID[2]);
        dayThree.setText(FormatUtils.formatDay(dateThree));
        dayThreeHighTV.setText(FormatUtils.shrinkUnit(highThree));

        TextView dayFourHighTV = rootView.findViewById(R.id.day_four_high);
        TextView dayFour = rootView.findViewById(R.id.day_four);
        ImageView imgView4 = rootView.findViewById(R.id.day_four_icon);
        imgView4.setImageResource(imgID[3]);
        dayFour.setText(FormatUtils.formatDay(dateFour));
        dayFourHighTV.setText(FormatUtils.shrinkUnit(highFour));

        TextView dayFiveHighTV = rootView.findViewById(R.id.day_five_high);
        TextView dayFive = rootView.findViewById(R.id.day_five);
        ImageView imgView5 = rootView.findViewById(R.id.day_five_icon);
        imgView5.setImageResource(imgID[4]);
        dayFive.setText(FormatUtils.formatDay(dateFive));
        dayFiveHighTV.setText(FormatUtils.shrinkUnit(highFive));

        return rootView;
    }
}
