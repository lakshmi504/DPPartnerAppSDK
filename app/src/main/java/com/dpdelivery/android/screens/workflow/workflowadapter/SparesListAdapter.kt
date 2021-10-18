package com.dpdelivery.android.screens.workflow.workflowadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.PartInfo
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_spares.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by user on 19/08/21.
 */
class SparesListAdapter(
    var context: Context,
    var adapterClickListener: IAdapterClickListener? = null
) :
    RecyclerView.Adapter<SparesListAdapter.SparesListViewHolder>(), Filterable {

    private var list: ArrayList<PartInfo>? = null
    var partsFilterList = ArrayList<PartInfo>()

    fun addList(list: ArrayList<PartInfo>?) {
        this.list = list
        partsFilterList = list!!
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SparesListViewHolder {
        val view = parent.inflate(R.layout.item_spares)
        return SparesListViewHolder(view, adapterClickListener)
    }

    override fun onBindViewHolder(holder: SparesListViewHolder, position: Int) {
        if (partsFilterList.size > 0) {
            holder.bind(context, partsFilterList[position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    override fun getItemCount() =
        if (partsFilterList != null && partsFilterList.size > 0) partsFilterList.size else 0

    class SparesListViewHolder(
        override val containerView: View,
        var adapterClickListener: IAdapterClickListener? = null
    ) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is PartInfo) {
                tv_spare_name.text = item.item_name
                if (item.mycart > 0) {
                    tv_quantity.text = item.mycart.toString()
                    iv_add.visibility = View.GONE
                    ll_add.visibility = View.VISIBLE
                } else {
                    iv_add.visibility = View.VISIBLE
                    ll_add.visibility = View.GONE
                }
                iv_add.setOnClickListener {
                    adapterClickListener?.onclick(
                        any = item,
                        pos = pos,
                        type = itemView,
                        op = Constants.ADD_SPARES
                    )
                }
                iv_increment.setOnClickListener {
                    adapterClickListener?.onclick(
                        any = item,
                        pos = pos,
                        type = itemView,
                        op = Constants.INCREMENT
                    )
                }
                iv_decrement.setOnClickListener {
                    adapterClickListener?.onclick(
                        any = item,
                        pos = pos,
                        type = itemView,
                        op = Constants.DECREMENT
                    )
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                when {
                    charSearch.isEmpty() -> {
                        partsFilterList = list!!
                    }
                    else -> {
                        val resultList = ArrayList<PartInfo>()
                        for (row in list!!) {
                            if (row.item_name.toLowerCase(Locale.ROOT)
                                    .contains(charSearch.toLowerCase(Locale.ROOT))
                            ) {
                                resultList.add(row)
                            }
                        }
                        partsFilterList = resultList
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = partsFilterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                partsFilterList = results?.values as ArrayList<PartInfo>
                notifyDataSetChanged()
            }

        }
    }
}