package com.matm.matmsdk.callbacks;
/*
 * @Auther Subhashree
 *
 * This Callback is mostly used
 * to send value and wakeup the caller app
 * with required data.
 *
 * */

public interface OnFinishListener {


    /*@Params Status, Param A
     *
     * */
    void onSDKFinish(String statusTxt, String paramA, String statusDesc );
}

