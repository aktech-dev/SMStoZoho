package com.example.smstozoho;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetTrans {

    @POST("banktransactions")
    Call<Transaction> postTrans(
            @Header("Authorization") String code,
            @Query("organization_id") int org,
            @Body Transaction body);
}
