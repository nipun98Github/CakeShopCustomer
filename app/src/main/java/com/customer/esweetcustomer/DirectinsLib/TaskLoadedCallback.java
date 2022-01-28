package com.customer.esweetcustomer.DirectinsLib;


import com.customer.esweetcustomer.pojo.mapDistanceObj;
import com.customer.esweetcustomer.pojo.mapTimeObj;

public interface TaskLoadedCallback {
    void onTaskDone(Object... values);
    void onDistanceTaskDone(mapDistanceObj distance);
    void onTimeTaskDone(mapTimeObj time);
}
