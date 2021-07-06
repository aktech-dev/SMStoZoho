package com.example.smstozoho;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostExpense {
    @POST("expenses")
    Call<PostExpenseBody> postExpense(
            @Header("Authorization") String code,
            @Query("organization_id") int org,
            @Body PostExpenseBody body);
}
