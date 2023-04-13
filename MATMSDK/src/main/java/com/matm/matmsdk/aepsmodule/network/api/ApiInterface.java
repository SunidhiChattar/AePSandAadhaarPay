package com.matm.matmsdk.aepsmodule.network.api;

import com.matm.matmsdk.aepsmodule.utils.aadharpay.Model.AadharpayRequest;
import com.matm.matmsdk.aepsmodule.utils.aadharpay.Model.AadharpayResponse;
import com.matm.matmsdk.aepsmodule.utils.aadharpay.Model.GetAuthenticationRequest;
import com.matm.matmsdk.aepsmodule.utils.aadharpay.Model.VPNStatusResponse;
import com.matm.matmsdk.aepsmodule.network.model.AadharVerificationResponse;
import com.matm.matmsdk.aepsmodule.network.model.BankListsSetGet;
import com.matm.matmsdk.aepsmodule.network.model.Base64Response;
import com.matm.matmsdk.aepsmodule.network.model.BioAuthRequest;
import com.matm.matmsdk.aepsmodule.network.model.BioAuthResponse;
import com.matm.matmsdk.aepsmodule.network.model.CashwithdrawalResponse;
import com.matm.matmsdk.aepsmodule.network.model.addressupdate.AadhaaarUpdateRequest;
import com.matm.matmsdk.aepsmodule.network.model.addressupdate.AddressUpdateResponse;
import com.matm.matmsdk.aepsmodule.network.model.pincode.PinCodeRequest;
import com.matm.matmsdk.aepsmodule.network.model.pincode.PinCodeResponse;
import com.matm.matmsdk.aepsmodule.unifiedaeps.UnifiedAepsRequestModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET("v80")
    Call<Base64Response> setdata();

    @POST
    Call<BioAuthResponse> getdata(@Url String encodedUrl, @Header("Authorization") String token, @Body BioAuthRequest bioAuthRequest);

    @GET("getIIN")
    Call<BankListsSetGet> getUsers();
    @GET("v118")
    Call<Base64Response> getViewUserPropAddress();

    @GET
    Call<AadharVerificationResponse> getdecodedViewUserPropAddress(@Url String url, @Header("Authorization") String token);

    @POST("v1/getCitystateAndroid")
    Call<PinCodeResponse> getPincodeData(@Body PinCodeRequest pinCodeRequest);

    @GET("v82")
    Call<Base64Response> setAddressEncodedUrl();

    @POST
    Call<AddressUpdateResponse> getAadhaarUpdate(@Url String url, @Header("Authorization") String token, @Body AadhaaarUpdateRequest aadhaaarUpdateRequest);

    @POST("aadhaarPay")
    Call<AadharpayResponse> getAadhaarPayUpdate(@Header("Authorization") String token, @Body AadharpayRequest aadharpayRequest);

    @GET("telnet_checkVpn")
    Call<VPNStatusResponse> getVPNStatusResponse();

    @GET("v22")
    Call<CashwithdrawalResponse> getcashWithdrawalResponse();

    @GET
    Call<Base64Response> getVPNStatusforall(@Url String url);

    @POST
    Call<ResponseBody> getencryptedBalanceEnquiry(@Url String url, @Header("Authorization") String token, @Body UnifiedAepsRequestModel unifiedAepsRequestModel);

    @POST
    Call<ResponseBody> getUsertoken(@Url String url, @Body GetAuthenticationRequest getAuthenticationRequest);

    @POST
    Call<ResponseBody> getFetchedData(@Url String url);



}
