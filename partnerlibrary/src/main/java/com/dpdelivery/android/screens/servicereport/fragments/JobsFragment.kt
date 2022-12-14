package com.dpdelivery.android.screens.servicereport.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.model.techres.LastJobsRes
import com.dpdelivery.android.screens.servicereport.adapter.JobsAdapter
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.toast
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_jobs.*
import javax.inject.Inject

class JobsFragment : DaggerFragment(), JobsContract.View {

    lateinit var mContext: Context
    lateinit var mActivity: FragmentActivity
    private var jobId: Int? = 0
    private var jobsAdapter: JobsAdapter? = null
    private var jobsList: ArrayList<LastJobsRes.LastJobsResItem>? = null
    lateinit var dialog: Dialog

    @Inject
    lateinit var presenter: JobsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            jobId = getInt("jobId", 0)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = requireActivity()
        mContext = requireContext()
        presenter.takeView(this)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_jobs.layoutManager = LinearLayoutManager(mContext)
        jobsAdapter = JobsAdapter(mContext)
        rv_jobs.adapter = jobsAdapter
        getJobsHistory()
    }

    override fun init() {
        dialog = CommonUtils.progressDialog(mContext)
    }

    private fun getJobsHistory() {
        if (!(mContext as Activity).isFinishing) {
            dialog.show()
        }
        presenter.getJobsList(jobId!!)
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        dialog.dismiss()
        toast(throwable.message!!, Toast.LENGTH_SHORT)
    }

    override fun showJobsRes(res: LastJobsRes) {
        dialog.dismiss()
        jobsList = res
        if (res.isNotEmpty()) {
            jobsAdapter!!.addList(jobsList!!)
        } else {
            tv_no_data.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(jobId: Int) =
            JobsFragment().apply {
                val bundle = Bundle()
                bundle.putInt("jobId", jobId)
                val fragment = JobsFragment()
                fragment.arguments = bundle
                return fragment
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dropView()
    }
}