package com.matm.matmsdk.callbacks;

/*Driver Listenere to call the drver activity and get the rd service data
* SUbhashree
* 16/06/2022
* */

public interface OnDriverDataListener {


    void onFingerClick(String aadharNo, String mobileNumber, String bankName,String driverFlag, String freashnessFactor,OnDriverDataListener listener);
    void onPidUpdate(String pidData );


}
