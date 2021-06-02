package com.dpdelivery.android.screens.workflow

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.AddWorkFlowData
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.SubmitPidIp
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
        fun showSparePartsRes(res: ArrayList<SparePartsData>)
        fun showPidDetailsRes(res: BLEDetailsRes)
        fun showSyncRes(res: BLEDetailsRes)
        fun showJobRes(res: Job)
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
        fun getPidDetails(hashMap: HashMap<String, String>)
        fun updateServerCmds(hashMap: HashMap<String, String>)
        fun getJob(jobId: Int)
    }
}