package com.example.smstozoho;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Tokengen {
    @FormUrlEncoded
    @POST("token")
    Call<Accesstoken> getAccessToken(

            @Field("refresh_token") String refresh_token,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("redirect_uri") String redirect_uri,
            @Field("grant_type") String grant_type

    );
}
