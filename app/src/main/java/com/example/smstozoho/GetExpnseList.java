package com.example.smstozoho;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GetExpnseList {

    @GET("chartofaccounts")
    Call<ExpenseList> getExpenseLists(
            @Header("Authorization") String code,
            @Query("organization_id") int org,
            @Query("filter_by") String type);

}
