package com.dpdelivery.android.technicianui.workflow

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.AddTextIp
import com.dpdelivery.android.model.techinp.AddWorkFlowData
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techres.AddTextRes
import com.dpdelivery.android.model.techres.SubmiPidRes
import com.dpdelivery.android.model.techres.WorkFlowDataRes
import java.io.File

interface WorkFlowContract {

    interface View : BaseView {
        fun showWorFlowDataRes(res: WorkFlowDataRes)
        fun showAddTextRes(res: AddTextRes)
        fun showWorkFlowDataRes(res: AddTextRes)
        fun showWorkFlowDataSubmitRes(res: AddTextRes)
        fun showWorkFlowFinishDataRes(res: AddTextRes)
        fun showFinishJobRes(res: SubmiPidRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getWorkFlowData(jobId: Int)
        fun addText(addTextIp: AddTextIp)
        fun addWorkFlow(workFlow: AddWorkFlowData)
        fun addWorkFlowSubmit(workFlow: AddWorkFlowData)
        fun addFinishWorkFlow(workFlow: AddWorkFlowData)
        fun addImage(jobId: Int, elementId: Int, file: File)
        fun finishJob(jobId: Int, finishJobIp: FinishJobIp)
    }
}