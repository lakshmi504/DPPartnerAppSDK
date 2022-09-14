package com.dpdelivery.android.screens.workflow

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.SyncCommandsRes
import com.dpdelivery.android.model.techinp.*
import com.dpdelivery.android.model.techres.*
import java.io.File

interface WorkFlowContract {

    interface View : BaseView {
        fun showWorFlowDataRes(res: WorkFlowDataRes)
        fun showAddTextRes(res: AddTextRes)
        fun showWorkFlowDataRes(res: AddTextRes)
        fun showWorkFlowDataSubmitRes(res: AddTextRes)
        fun showWorkFlowFinishDataRes(res: AddTextRes)
        fun showFinishJobRes(res: SubmiPidRes)
        fun showSubmittedPidRes(submiPidRes: SubmiPidRes)
        fun showRefreshPidRes(res: PIdStatusRes)
        fun showSparePartsRes(res: InventoryRes)
        fun showPidDetailsRes(res: BLEDetailsRes)
        fun showBleCmdDetailsRes(res: SyncCommandsRes)
        fun showJobRes(res: Job)
        fun showBidStatus(res: BIDStatusRes)
        fun showApiInputRes(res: ApiInputRes)
        fun showPartnerDetails(res: PartnerDetailsRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getWorkFlowData(jobId: Int)
        fun addWorkFlow(workFlow: AddWorkFlowData)
        fun addWorkFlowSubmit(workFlow: AddWorkFlowData)
        fun addFinishWorkFlow(workFlow: AddWorkFlowData)
        fun addImage(jobId: Int, elementId: Int, file: File)
        fun finishJob(jobId: Int, finishJobIp: FinishJobIp)
        fun submitPid(submitPidIp: SubmitPidIp)
        fun refreshPidStatus(purifierId: String)
        fun getSparePartsList(functionName: String?)
        fun getPidDetails(homeIP: HomeIP)
        fun getBleCmdDetails(deviceCode: String, onlyPending: Boolean)
        fun getJob(jobId: Int)
        fun getBidStatus(data: BIDStatusIp)
        fun getApiDataList(functionName: String?)
        fun getPartnerDetails()
    }
}