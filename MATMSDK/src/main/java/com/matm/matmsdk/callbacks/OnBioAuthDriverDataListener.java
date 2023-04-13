package com.matm.matmsdk.callbacks;

/*Driver Listenere to call the drver activity and get the rd service data
* SUbhashree
* 16/06/2022
* */

public interface OnBioAuthDriverDataListener {


    void onFingerClick(String aadharNo, String driverFlag, String freashnessFactor, OnBioAuthDriverDataListener listener);


}
