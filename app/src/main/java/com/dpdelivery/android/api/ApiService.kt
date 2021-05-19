package com.dpdelivery.android.api

import com.dpdelivery.android.model.*
import com.dpdelivery.android.model.input.AssignJobsIp
import com.dpdelivery.android.model.input.LoginIp
import com.dpdelivery.android.model.input.UpdateAppointmentIp
import com.dpdelivery.android.model.input.UpdateStatusIp
import com.dpdelivery.android.model.techinp.*
import com.dpdelivery.android.model.techres.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface ApiService {

    @POST(ApiConstants.LOGIN)
    fun login(@Body loginIp: LoginIp): Observable<Response<Void>>

    // Delivery App

    @GET(ApiConstants.DELIVERY_JOBS_LIST)
    fun deliveryJobsList(@Header("Authorization") token: String): Observable<DeliveryJobsListRes>

    @GET(ApiConstants.DELIVERY_JOBS_LIST)
    fun deliveryJobsListByDate(@Header("Authorization") token: String, @Query("startDate") startDate: String, @Query("endDate") endDate: String): Observable<DeliveryJobsListRes>


    @GET(ApiConstants.DELIVERY_JOBS_LIST)
    fun getDeliveryJobs(@Header("Authorization") token: String, @Query("status") status: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<DeliveryJobsListRes>

    @GET(ApiConstants.DELIVERY_JOBS_LIST)
    fun moreDeliveryJobsList(@Header("Authorization") token: String, @Query("status") status: String, @Query("PageSize") pageSize: Int, @Query("page") page: Int): Observable<DeliveryJobsListRes>


    @GET(ApiConstants.DELIVERY_JOB)
    fun deliveryJobData(@Header("Authorization") token: String, @Query("jobId") jobId: Int): Observable<DeliveryJobsRes>

    @PUT(ApiConstants.UPDATE_APPOINTMENT)
    fun updateAppointment(@Header("Authorization") token: String, @Body updateAppointmentIp: UpdateAppointmentIp): Observable<Response<ResponseBody>>

    @PUT(ApiConstants.UPDATE_STATUS)
    fun updateStatus(@Header("Authorization") token: String, @Body updateStatusIp: UpdateStatusIp): Observable<Response<ResponseBody>>

    @GET(ApiConstants.SEARCH)
    fun searchDeliveryList(@Header("Authorization") token: String, @Query("search") search: String): Observable<DeliveryJobsListRes>

    @GET(ApiConstants.SEARCH)
    fun filterDeliveryList(@Header("Authorization") token: String, @Query("status") status: String, @Query("jobType") jobType: String): Observable<DeliveryJobsListRes>

    @GET(ApiConstants.GET_AGENTS)
    fun getAgentsList(@Header("Authorization") token: String): Observable<List<GetAgentsRes>>

    @PUT(ApiConstants.ASSIGN_JOB)
    fun assignJob(@Header("Authorization") token: String, @Body assignJobsIp: AssignJobsIp): Observable<AssignJobRes>

    @Multipart
    @PUT(ApiConstants.UPLOAD_PHOTO)
    fun uploadPhoto(@Header("Authorization") token: String, @Part("jobId") jobId: RequestBody, @Part file: MultipartBody.Part): Observable<UploadPhotoRes>

    //Technician App

    @GET(ApiConstants.JOBS_LIST)
    fun getAssignedJobs(@Header("Authorization") token: String, @Query("status") status: String, @Query("appointmentDate") appointmentDate: String, @Query("orderBy") orderBy: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getMoreJobsList(@Header("Authorization") token: String, @Query("status") status: String, @Query("appointmentDate") appointmentDate: String, @Query("orderBy") orderBy: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getAssignedJobs(@Header("Authorization") token: String, @Query("status") status: String, @Query("orderDir") orderDir: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getMoreJobsList(@Header("Authorization") token: String, @Query("status") status: String, @Query("orderDir") orderDir: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getFilterJobs(@Header("Authorization") token: String, @Query("status") status: String, @Query("appointmentDate") appointmentDate: String, @Query("orderBy") orderBy: String, @Query("pageSize") pageSize: Int): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun searchTechJobsList(@Header("Authorization") token: String, @Query("search") search: String, @Query("orderBy") orderBy: String): Observable<ASGListRes>

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

    @GET
    fun getSpareParts(@Header("Authorization") token: String, @Url url: String): Observable<ArrayList<SparePartsData>>

    @PUT(ApiConstants.FINISH_JOB + "{jobId}")
    fun finishJob(@Header("Authorization") token: String, @Path("jobId", encoded = true) jobId: Int, @Body finishJobIp: FinishJobIp): Observable<SubmiPidRes>

    @GET(ApiConstants.SEND_HAPPY_CODE + "{jobId}")
    fun resendHappyCode(@Header("Authorization") token: String, @Path("jobId", encoded = true) jobId: Int): Observable<SubmiPidRes>

    @GET(ApiConstants.SUMMARY)
    fun getSummary(@Header("Authorization") token: String): Observable<SummaryRes>

    @FormUrlEncoded
    @POST(ApiConstants.GET_BLE_DETAILS)
    fun getBLEDetails(@FieldMap purifierid: HashMap<String, String>): Observable<BLEDetailsRes>

    @FormUrlEncoded
    @POST(ApiConstants.SYNC)
    fun updateServerCmds(@FieldMap params: HashMap<String, String>): Observable<BLEDetailsRes>

    @GET(ApiConstants.GET_WORK_FLOW_DATA)
    fun getWorkFlowData(@Header("Authorization") token: String, @Query("jobId") jobId: Int): Observable<WorkFlowDataRes>

    @POST(ApiConstants.ADD_TEXT)
    fun addText(@Header("Authorization") token: String, @Body addTextIp: AddTextIp): Observable<AddTextRes>

    @POST(ApiConstants.ADD_WORK_FLOW)
    fun addWorkFlow(@Header("Authorization") token: String, @Body workFlow: AddWorkFlowData): Observable<AddTextRes>

    @Multipart
    @POST(ApiConstants.ADD_IMAGE + "/{jobId}" + "/{elementId}")
    fun addImage(@Header("Authorization") token: String, @Path("jobId", encoded = true) jobId: Int, @Path("elementId", encoded = true) elementId: Int, @Part file: MultipartBody.Part): Observable<AddTextRes>

    @GET(ApiConstants.VOIP_CALL)
    fun getVoipCall(@Query("api_key") api_key: String, @Query("method") method: String, @Query("caller") caller: String, @Query("receiver") receiver: String): Observable<Response<ResponseBody>>

}