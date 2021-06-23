package com.dpdelivery.android.screens.account

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.model.techres.AccountModel
import com.dpdelivery.android.screens.payout.DetailEarningsActivity
import com.dpdelivery.android.screens.summary.SummaryActivity

class AccountAdapter(var mCtx: Context, private val accountList: AccountModel) :
    RecyclerView.Adapter<BaseViewholder>() {
    override fun getItemCount(): Int {
        return accountList.data!!.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewholder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.items_account, p0, false)
        return AccountViewHolder(view)

    }

    override fun onBindViewHolder(p0: BaseViewholder, p1: Int) {
        p0.bind(mCtx, accountList.data!![p1], p1)
    }

    class AccountViewHolder(view: View) : BaseViewholder(view) {
        override fun bind(context: Context, item: Any, pos: Int) {
            val titleName = itemView.findViewById(R.id.tv_title) as AppCompatTextView
            when (item.toString()) {
                Constants.MY_EARNINGS -> {
                    titleName.text = Constants.MY_EARNINGS
                    itemView.setOnClickListener(View.OnClickListener {
                        context.startActivity(Intent(context, DetailEarningsActivity::class.java))
                    })
                }

                Constants.SUMMARY -> {
                    titleName.text = Constants.SUMMARY
                    itemView.setOnClickListener(View.OnClickListener {
                        context.startActivity(Intent(context, SummaryActivity::class.java))
                    })
                }
            }
        }

    }
}