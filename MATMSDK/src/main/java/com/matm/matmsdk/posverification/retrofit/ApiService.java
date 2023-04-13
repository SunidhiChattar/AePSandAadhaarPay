package com.matm.matmsdk.posverification.retrofit;




import com.matm.matmsdk.Model.MPOS.GethAuthRequestModel;
import com.matm.matmsdk.Model.MPOS.GethAuthResponseModel;
import com.matm.matmsdk.Model.otpmodel.GetOtpServiceRequestModel;
import com.matm.matmsdk.Model.otpmodel.GetOtpServiceResponseModel;
import com.matm.matmsdk.posverification.response.CheckKycStatusRequestModel;
import com.matm.matmsdk.posverification.response.CheckKycStatusResponseModel;
import com.matm.matmsdk.posverification.responsemodel.PosFetchAccountResponseModel;
import com.matm.matmsdk.posverification.responsemodel.MerchantOnboardRequestModel;
import com.matm.matmsdk.posverification.responsemodel.MerchantOnboardResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface ApiService {


    @GET("Fetch/Account")
    Call<PosFetchAccountResponseModel> getFetchAccountResponse(@Header("Authorization") String token);

    @POST("fetch_merchant_onboard_status")
    Call<CheckKycStatusResponseModel> getCheckKycStatusResponse(@Body CheckKycStatusRequestModel getOtpServiceRequestModel);

 @POST("initiate_merchant_onboard")
    Call<MerchantOnboardResponseModel> getMerchantOnboardResponse(@Body MerchantOnboardRequestModel merchantOnboardRequestModel,@Header("Authorization") String token);

    @POST("requestOPTService")
    Call<GetOtpServiceResponseModel> getOtpServiceResponse(@Body GetOtpServiceRequestModel getOtpServiceRequestModel);

    @POST("resend/resendOTP")
    Call<GetOtpServiceResponseModel> getResendOtpServiceResponse(@Body GetOtpServiceRequestModel getOtpServiceRequestModel);

    @POST("api/getAuthenticateData")
    Call<GethAuthResponseModel> getUserAuthTokenResponse(@Body GethAuthRequestModel gethAuthRequestModel);

}
