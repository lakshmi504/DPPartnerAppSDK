package com.dpdelivery.android.screens.workflow.workflowadapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.model.techres.PartInfo
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_spares.*

/**
 * Created by user on 19/08/21.
 */
class SparesListAdapter(var context: Context) :
    RecyclerView.Adapter<SparesListAdapter.SparesListViewHolder>() {

    private var list: ArrayList<PartInfo>? = null

    fun addList(list: ArrayList<PartInfo>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SparesListViewHolder {
        val view = parent.inflate(R.layout.item_spares)
        return SparesListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SparesListViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    class SparesListViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is PartInfo) {
                tv_spare_name.text = item.item_name
                iv_add.setOnClickListener {
                    iv_add.visibility = View.GONE
                    ll_add.visibility = View.VISIBLE
                }
                iv_increment.setOnClickListener { clickForModify(it, context, item) }
                iv_decrement.setOnClickListener { clickForModify(it, context, item) }
                if (item.serializable) {

                } else {

                }

            }
        }

        private fun clickForModify(view: View?, context: Context, item: PartInfo) {
            when (view!!.id) {
                R.id.iv_increment -> {
                    if (item.serializable) {
                        //context.startActivity(Intent(context, ScannerActivity::class.java))
                        item.apply {
                            tempMyCart = mycart + 2
                            tv_quantity.text = tempMyCart.toString()
                        }
                    } else {

                    }
                }
                R.id.iv_decrement -> {

                }
            }
        }
    }
}