package com.dpdelivery.android.api

import com.dpdelivery.android.model.*
import com.dpdelivery.android.model.input.AssignJobsIp
import com.dpdelivery.android.model.input.LoginIp
import com.dpdelivery.android.model.input.UpdateAppointmentIp
import com.dpdelivery.android.model.input.UpdateStatusIp
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.StartJobIP
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techinp.SubmitPidIp
import com.dpdelivery.android.model.techres.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST(ApiConstants.LOGIN)
    fun login(@Body loginIp: LoginIp): Observable<Response<Void>>

    // Delivery App

    @GET(ApiConstants.DELIVERY_JOBS_LIST)
    fun deliveryJobsList(@Header("Authorization") token: String): Observable<DeliveryJobsListRes>

    @GET(ApiConstants.DELIVERY_JOBS_LIST)
    fun moreDeliveryJobsList(@Header("Authorization") token: String, @Query("PageSize") pageSize: Int, @Query("page") page: Int): Observable<DeliveryJobsListRes>


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

    @Multipart
    @PUT(ApiConstants.UPLOAD_PHOTO)
    fun uploadPhoto(@Header("Authorization") token: String, @Part("jobId") jobId: RequestBody, @Part file: MultipartBody.Part): Observable<UploadPhotoRes>

    //Technician App

    @GET(ApiConstants.JOBS_LIST)
    fun getAssignedJobs(@Header("Authorization") token: String, @Query("orderDir") orderDir: String, @Query("orderBy") orderBy: String): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getAssignedJobs(@Header("Authorization") token: String,@Query("status") status: String, @Query("orderDir") orderDir: String, @Query("orderBy") orderBy: String , @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getMoreJobsList(@Header("Authorization") token: String, @Query("status") status: String, @Query("orderDir") orderDir: String, @Query("orderBy") orderBy: String ,@Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getFilterJobs(@Header("Authorization") token: String, @Query("status") status: String, @Query("orderDir") orderDir: String, @Query("orderBy") orderBy: String): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun searchTechJobsList(@Header("Authorization") token: String, @Query("search") search: String, @Query("orderDir") orderDir: String, @Query("orderBy") orderBy: String): Observable<ASGListRes>

    @GET(ApiConstants.JOB_BY_ID + "{jobId}")
    fun getAssignedJobById(@Header("Authorization") token: String, @Path("jobId", encoded = true) jobId: Int): Observable<Job>

    @PUT(ApiConstants.JOB_BY_ID + "{jobId}")
    fun startJob(@Header("Authorization") token: String, @Path("jobId", encoded = true) jobId: Int, @Body startJobIP: StartJobIP): Observable<StartJobRes>

    @POST(ApiConstants.SUBMIT_PID)
    fun submitPid(@Header("Authorization") token: String, @Body submitPidIp: SubmitPidIp): Observable<SubmiPidRes>

    @GET(ApiConstants.PURIFIER_STATUS + "{deviceId}" + "/status")
    fun refreshPidStatus(@Header("Authorization") token: String, @Path("deviceId", encoded = true) deviceId: String): Observable<PIdStatusRes>

    @PUT(ApiConstants.JOB_BY_ID + "{jobId}")
    fun addNote(@Header("Authorization") token: String, @Path("jobId", encoded = true) jobId: Int, @Body updateJobIp: UpdateJobIp): Observable<StartJobRes>

    @Multipart
    @POST(ApiConstants.JOB_BY_ID + "{jobId}" + "/uploadDevicePhoto")
    fun uploadDevicePhoto(@Header("Authorization") token: String, @Path("jobId", encoded = true) jobId: Int, @Part file: MultipartBody.Part): Observable<UploadPhotoRes>

    @GET(ApiConstants.SPARE_PARTS)
    fun getSpareParts(@Header("Authorization") token: String): Observable<ArrayList<SparePartsData>>

    @PUT(ApiConstants.FINISH_JOB + "{jobId}")
    fun finishJob(@Header("Authorization") token: String, @Path("jobId", encoded = true) jobId: Int, @Body finishJobIp: FinishJobIp): Observable<SubmiPidRes>
}