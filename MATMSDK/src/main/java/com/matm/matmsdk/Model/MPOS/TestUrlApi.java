package com.matm.matmsdk.Model.MPOS;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TestUrlApi {
    @GET("generate/v110")
    Call<BankSearchUserResponseModel> getBankSearchUserResponse();

    @POST("completetransaction")
    Call<CompleteTranResponseModel> getCompleteTranResponseModel(@Body CompleteTranRequestModel completeTranRequestModel);
}
