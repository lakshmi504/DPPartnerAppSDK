package com.dpdelivery.android.api

import com.dpdelivery.android.model.SyncCommandsRes
import com.dpdelivery.android.model.techres.SyncCommandsUpdatedRes
import com.dpdelivery.android.model.techinp.*
import com.dpdelivery.android.model.techres.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface ApiService {

    @POST(ApiConstants.LOGIN)
    fun login(@Body loginIp: LoginIp): Observable<Response<Void>>

    @GET(ApiConstants.JOBS_LIST)
    fun getAssignedJobs(
        @Header("Authorization") token: String,
        @Query("status") status: String,
        @Query("appointmentDate") appointmentDate: String,
        @Query("orderBy") orderBy: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getMoreAsgJobsList(
        @Header("Authorization") token: String,
        @Query("status") status: String,
        @Query("appointmentDate") appointmentDate: String,
        @Query("orderBy") orderBy: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getJobsList(
        @Header("Authorization") token: String,
        @Query("status") status: String,
        @Query("orderDir") orderDir: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun getMoreJobsList(
        @Header("Authorization") token: String,
        @Query("status") status: String,
        @Query("orderDir") orderDir: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): Observable<ASGListRes>

    @GET(ApiConstants.JOBS_LIST)
    fun searchTechJobsList(
        @Header("Authorization") token: String,
        @Query("search") search: String,
        @Query("orderBy") orderBy: String
    ): Observable<ASGListRes>

    @GET(ApiConstants.JOB_BY_ID + "{jobId}")
    fun getAssignedJobById(
        @Header("Authorization") token: String,
        @Path("jobId", encoded = true) jobId: Int
    ): Observable<Job>

    @PUT(ApiConstants.JOB_BY_ID + "{jobId}")
    fun startJob(
        @Header("Authorization") token: String,
        @Path("jobId", encoded = true) jobId: Int,
        @Body startJobIP: StartJobIP
    ): Observable<StartJobRes>

    @POST(ApiConstants.SUBMIT_PID)
    fun submitPid(
        @Header("Authorization") token: String,
        @Body submitPidIp: SubmitPidIp
    ): Observable<SubmiPidRes>

    @GET(ApiConstants.PURIFIER_STATUS + "{deviceId}" + "/status")
    fun refreshPidStatus(
        @Header("Authorization") token: String,
        @Path("deviceId", encoded = true) deviceId: String
    ): Observable<PIdStatusRes>

    @PUT(ApiConstants.JOB_BY_ID + "{jobId}")
    fun addNote(
        @Header("Authorization") token: String,
        @Path("jobId", encoded = true) jobId: Int,
        @Body updateJobIp: UpdateJobIp
    ): Observable<StartJobRes>

    @Multipart
    @POST(ApiConstants.JOB_BY_ID + "{jobId}" + "/uploadDevicePhoto")
    fun uploadDevicePhoto(
        @Header("Authorization") token: String,
        @Path("jobId", encoded = true) jobId: Int,
        @Part file: MultipartBody.Part
    ): Observable<UploadPhotoRes>

    @GET(ApiConstants.SPARE_PARTS)
    fun getSpareParts(@Header("Authorization") token: String): Observable<ArrayList<SparePartsData>>

    @GET
    fun getSpareParts(
        @Header("Authorization") token: String,
        @Url url: String
    ): Observable<InventoryRes>

    @GET
    fun getApiInputData(
        @Header("Authorization") token: String,
        @Url url: String
    ): Observable<ApiInputRes>

    @PUT(ApiConstants.FINISH_JOB + "{jobId}")
    fun finishJob(
        @Header("Authorization") token: String,
        @Path("jobId", encoded = true) jobId: Int,
        @Body finishJobIp: FinishJobIp
    ): Observable<SubmiPidRes>

    @GET(ApiConstants.SUMMARY)
    fun getSummary(@Header("Authorization") token: String): Observable<SummaryRes>

    @POST(ApiConstants.GET_BLE_DETAILS)
    fun getBLEDetails(@Body homeIP: HomeIP): Observable<BLEDetailsRes>


    @GET(ApiConstants.GET_WORK_FLOW_DATA)
    fun getWorkFlowData(
        @Header("Authorization") token: String,
        @Query("jobId") jobId: Int
    ): Observable<WorkFlowDataRes>

    @POST(ApiConstants.ADD_WORK_FLOW)
    fun addWorkFlow(
        @Header("Authorization") token: String,
        @Body workFlow: AddWorkFlowData
    ): Observable<AddTextRes>

    @Multipart
    @POST(ApiConstants.ADD_IMAGE + "/{jobId}" + "/{elementId}")
    fun addImage(
        @Header("Authorization") token: String,
        @Path("jobId", encoded = true) jobId: Int,
        @Path("elementId", encoded = true) elementId: Int,
        @Part file: MultipartBody.Part
    ): Observable<AddTextRes>

    @GET(ApiConstants.VOIP_CALL)
    fun getVoipCall(
        @Query("api_key") api_key: String,
        @Query("method") method: String,
        @Query("caller") caller: String,
        @Query("receiver") receiver: String
    ): Observable<Response<ResponseBody>>

    @POST(ApiConstants.UPDATE_TOKEN)
    fun updateDeviceToken(
        @Header("Authorization") token: String,
        @Body data: UpdateTokenIP
    ): Observable<CommonRes>

    @POST(ApiConstants.PARTNER_DETAILS)
    fun getPartnerDetails(
        @Header("Authorization") token: String
    ): Observable<PartnerDetailsRes>

    @GET(ApiConstants.EARNINGS)
    fun getPayoutDetails(
        @Header("Authorization") token: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Observable<EarningsRes>

    @GET(ApiConstants.INVENTORY_COUNT + "{id}")
    fun getInventoryCount(
        @Header("Authorization") token: String,
        @Path("id", encoded = true) id: Int
    ): Observable<InventoryRes>

    @POST(ApiConstants.INVENTORY_PICKED_UP)
    fun pickedUpInventoryDetails(
        @Header("Authorization") token: String, @Body inventoryIp: DetailInventoryIp
    ): Observable<InventoryDetailRes>

    @POST(ApiConstants.INVENTORY_TO_BE_PICKED_UP)
    fun toBePickedUpInventoryDetails(
        @Header("Authorization") token: String, @Body inventoryIp: DetailInventoryIp
    ): Observable<InventoryDetailRes>

    @POST(ApiConstants.INVENTORY_TO_BE_RETURNED)
    fun returnedInventoryDetails(
        @Header("Authorization") token: String, @Body inventoryIp: DetailInventoryIp
    ): Observable<InventoryDetailRes>

    @POST(ApiConstants.SUBMIT_INVENTORY)
    fun submitInventoryDetails(
        @Header("Authorization") token: String, @Body submitInventoryIp: SubmitInventoryIp
    ): Observable<CommonRes>

    @POST(ApiConstants.SUBMIT_TECH_INVENTORY)
    fun submitTechInventoryDetails(
        @Header("Authorization") token: String, @Body submitInventoryIp: SubmitInventoryIp
    ): Observable<CommonRes>

    @POST(ApiConstants.CANCEL_INVENTORY)
    fun cancelInventoryDetails(
        @Header("Authorization") token: String, @Body submitInventoryIp: SubmitInventoryIp
    ): Observable<CommonRes>

    @GET(ApiConstants.LAST_3_JOBS + "{jobId}")
    fun getLast3Jobs(
        @Header("Authorization") token: String,
        @Path("jobId", encoded = true) id: Int
    ): Observable<LastJobsRes>

    @POST(ApiConstants.CONN_CHECK)
    fun getBotStatus(
        @Header("Authorization") token: String,
        @Body bidStatusIp: BIDStatusIp
    ): Observable<BIDStatusRes>

    @GET(ApiConstants.GET_NEXT_JOB)
    fun getNextJobs(
        @Header("Authorization") token: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): Observable<GetNextJobRes>

    @GET(ApiConstants.SAVE_JOB_RESPONSE + "/{jobId}")
    fun saveJobResponse(
        @Header("Authorization") token: String,
        @Path("jobId", encoded = true) jobId: Int,
        @Query("response", encoded = true) response: String
    ): Observable<GetNextJobRes>

    @GET(ApiConstants.GET_COMMANDS)
    fun getCommands(
        @Query("deviceCode") deviceCode: String,
        @Query("onlyPending") onlyPending: Boolean
    ): Observable<SyncCommandsRes>

    @PUT(ApiConstants.UPDATE_SYNC_COMMANDS)
    fun updateSyncCommands(@Body data: SyncCommandIP): Observable<SyncCommandsUpdatedRes>

    @POST(ApiConstants.UPDATE_SYNC_DATA)
    fun updateSyncPurifierData(@Body data: SyncDataIP): Observable<SyncCommandsUpdatedRes>

}