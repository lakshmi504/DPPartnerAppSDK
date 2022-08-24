package com.dpdelivery.android.screens.servicereport.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.model.techres.SpareConsumption
import com.dpdelivery.android.screens.servicereport.adapter.SparesAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_spares.*

class SparesFragment : DaggerFragment() {

    lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity
    private var sparesAdapter: SparesAdapter? = null
    private var sparesHistory: ArrayList<SpareConsumption?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            sparesHistory = getParcelableArrayList("sparesHistory")
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
        return inflater.inflate(R.layout.fragment_spares, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_spares.layoutManager = LinearLayoutManager(mContext)
        sparesAdapter = SparesAdapter(mContext)
        rv_spares.adapter = sparesAdapter
        if (sparesHistory!!.isNotEmpty()) {
            ll_static_data.visibility = View.VISIBLE
            sparesAdapter!!.addList(sparesHistory!!)
        } else {
            tv_no_data.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(sparesHistory: ArrayList<SpareConsumption>): SparesFragment {
            val bundle = Bundle()
            bundle.putParcelableArrayList("sparesHistory", sparesHistory)
            val fragment = SparesFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}