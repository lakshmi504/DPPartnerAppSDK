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
import com.dpdelivery.android.screens.servicereport.adapter.SparesAdapter
import kotlinx.android.synthetic.main.fragment_spares.*

class SparesFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var mActivity: FragmentActivity
    private var purifierId: String? = null
    private var sparesAdapter: SparesAdapter? = null
    private var sparesList: ArrayList<String>? = null

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
        return inflater.inflate(R.layout.fragment_spares, container, false)
    }

    override fun onResume() {
        super.onResume()
        getSparesChangedHistory()
    }

    private fun getSparesChangedHistory() {
        rv_spares.layoutManager = LinearLayoutManager(mContext)
        sparesAdapter = SparesAdapter(mContext)
        rv_spares.adapter = sparesAdapter
        sparesList = ArrayList()
        sparesList!!.add("")
        sparesList!!.add("")
        sparesAdapter!!.addList(sparesList!!)
    }

    companion object {
        @JvmStatic
        fun newInstance(purifierId: String): SparesFragment {
            return SparesFragment().apply {
                val bundle = Bundle()
                bundle.putString("purifierId", purifierId)
                val fragment = SparesFragment()
                fragment.arguments = bundle
            }
        }
    }
}