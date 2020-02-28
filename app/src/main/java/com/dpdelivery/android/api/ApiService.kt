package com.dpdelivery.android.api

import com.dpdelivery.android.model.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST(ApiConstants.LOGIN)
    fun login(@Body loginIp: LoginIp): Observable<Response<Void>>

    @GET(ApiConstants.DELIVERY_JOBS_LIST)
    fun deliveryJobsList(@Header("Authorization") token: String): Observable<DeliveryJobsListRes>

    @GET(ApiConstants.DELIVERY_JOB)
    fun deliveryJobData(@Header("Authorization") token: String, @Query("jobId") jobId: Int): Observable<DeliveryJobsRes>

    @PUT(ApiConstants.UPDATE_APPOINTMENT)
    fun updateAppointment(@Header("Authorization") token: String, @Body updateAppointmentIp: UpdateAppointmentIp): Observable<Response<ResponseBody>>

    @PUT(ApiConstants.UPDATE_STATUS)
    fun updateStatus(@Header("Authorization") token: String, @Body updateStatusIp: UpdateStatusIp): Observable<Response<ResponseBody>>

    @GET(ApiConstants.SEARCH)
    fun searchDeliveryList(@Header("Authorization") token: String, @Query("search") search: String): Observable<DeliveryJobsListRes>

    @GET(ApiConstants.SEARCH)
    fun filterDeliveryList(@Header("Authorization") token: String, @Query("status") status: String): Observable<DeliveryJobsListRes>

    @GET(ApiConstants.GET_AGENTS)
    fun getAgentsList(@Header("Authorization") token: String): Observable<List<GetAgentsRes>>

    @PUT(ApiConstants.ASSIGN_JOB)
    fun assignJob(@Header("Authorization") token: String, @Body assignJobsIp: AssignJobsIp): Observable<AssignJobRes>
}