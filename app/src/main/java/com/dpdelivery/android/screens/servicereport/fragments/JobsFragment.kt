package com.dpdelivery.android.screens.servicereport.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.screens.servicereport.adapter.JobsAdapter
import kotlinx.android.synthetic.main.fragment_jobs.*

class JobsFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var mActivity: FragmentActivity
    private var purifierId: String? = null
    private var jobsAdapter: JobsAdapter? = null
    private var jobsList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            purifierId = getString("purifierId")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = requireActivity()
        mContext = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jobs, container, false)
    }

    override fun onResume() {
        super.onResume()
        getJobsHistory()
    }

    private fun getJobsHistory() {
        rv_jobs.layoutManager = LinearLayoutManager(mContext)
        jobsAdapter = JobsAdapter(mContext)
        rv_jobs.adapter = jobsAdapter
        jobsList = ArrayList<String>()
        jobsList!!.add("")
        jobsList!!.add("")
        jobsAdapter!!.addList(jobsList!!)
    }

    companion object {
        @JvmStatic
        fun newInstance(purifierId: String) =
            JobsFragment().apply {
                val bundle = Bundle()
                bundle.putString("purifierId", purifierId)
                val fragment = JobsFragment()
                fragment.arguments = bundle
                return fragment
            }
    }
}