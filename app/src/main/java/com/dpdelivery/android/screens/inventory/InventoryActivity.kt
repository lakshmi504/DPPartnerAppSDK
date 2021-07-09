package com.dpdelivery.android.screens.inventory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.InventoryRes
import com.dpdelivery.android.model.techres.PartInfo
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.inventoryDetails.InventoryDetailsActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class InventoryActivity : TechBaseActivity(), InventoryContract.View, IAdapterClickListener {

    lateinit var mContext: Context
    lateinit var adapterInventory: BasicAdapter
    private var partsList: ArrayList<PartInfo> = ArrayList()

    @Inject
    lateinit var inventoryPresenter: InventoryPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context).inflate(R.layout.activity_inventory, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Inventory")
        setUpBottomNavView(true)
    }

    // layout to inflate our menu file.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // below line is to get our inflater
        val inflater = menuInflater

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu)

        // below line is to get our menu item.
        val searchItem = menu.findItem(R.id.actionSearch)

        // getting search view of our item.
        val searchView: SearchView = searchItem.actionView as SearchView

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // calling a method to filter our recycler view.
                filter(newText)
                return false
            }
        })
        return true
    }

    private fun filter(text: String?) {
        // creating a new array list to filter our data.
        val filteredList: ArrayList<PartInfo> = ArrayList<PartInfo>()

        // running a for loop to compare elements.
        for (item in partsList) {
            if (item.item_name.toLowerCase(Locale.ROOT).contains(text!!.toLowerCase(Locale.ROOT))) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Item Found", Toast.LENGTH_LONG).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapterInventory.addList(filteredList)
        }
    }

    private fun getInventoryItems() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        rv_inventory.layoutManager = LinearLayoutManager(this)
        rv_inventory.setHasFixedSize(true)
        adapterInventory =
            BasicAdapter(context, R.layout.item_inventory, adapterClickListener = this)
        rv_inventory.adapter = adapterInventory
        inventoryPresenter.getInventory(CommonUtils.getId())
    }

    override fun showInventoryRes(res: InventoryRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.part_info.isNotEmpty()) {
            partsList = res.part_info
            adapterInventory.addList(res.part_info)
        } else {
            showViewState(MultiStateView.VIEW_STATE_EMPTY)
            empty_button.visibility = View.GONE
        }

    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (throwable is HttpException) {
            when (throwable.code()) {
                403 -> {
                    SharedPreferenceManager.clearPreferences()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                else -> {
                    showViewState(MultiStateView.VIEW_STATE_ERROR)
                    toast(throwable.message.toString())
                }
            }
        } else {
            showViewState(MultiStateView.VIEW_STATE_ERROR)
            error_textView.text = throwable.message
        }
    }

    override fun onResume() {
        super.onResume()
        inventoryPresenter.takeView(this)
        getInventoryItems()
        bottom_navigation.selectedItemId = R.id.action_inventory
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is PartInfo) {
            when (op) {
                Constants.PICKED_UP_INVENTORY -> {
                    startActivity(
                        Intent(
                            this,
                            InventoryDetailsActivity::class.java
                        ).putExtra("title", "Picked Up Items")
                            .putExtra("item_code", any.item_code)
                            .putExtra("item_id", any.item_id.toString())
                            .putExtra("item_name", any.item_name)
                    )
                }
                Constants.TO_BE_PICKED_UP_INVENTORY -> {
                    startActivity(
                        Intent(
                            this,
                            InventoryDetailsActivity::class.java
                        ).putExtra("title", "To be picked up")
                            .putExtra("item_code", any.item_code)
                            .putExtra("item_id", any.item_id.toString())
                            .putExtra("item_name", any.item_name)
                    )
                }
                Constants.RETURNABLE_INVENTORY -> {
                    startActivity(
                        Intent(
                            this,
                            InventoryDetailsActivity::class.java
                        ).putExtra("title", "To be returned")
                            .putExtra("item_code", any.item_code)
                            .putExtra("item_id", any.item_id.toString())
                            .putExtra("item_name", any.item_name)
                    )
                }
            }
        }
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TechJobsListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}