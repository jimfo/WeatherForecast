package com.jamesfody.weatherforecast.Interfaces;

/**
 * Created by Jim on 1/4/2018.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
