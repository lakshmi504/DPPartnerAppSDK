package com.dpdelivery.android.screens.sync

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dpdelivery.android.R
import com.dpdelivery.android.utils.CommonUtils
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_sync.*
import java.text.SimpleDateFormat
import java.util.*

class SyncActivity : DaggerAppCompatActivity() {
    private var mBluetoothLeService: BluetoothLeService? = null
    private var botId: String? = "50:65:83:99:CD:31"
    private var mConnected = false
    lateinit var cmdH: CommandHandler
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mDeviceName: String? = null
    internal var changed = 0
    internal var servicelinked: Int = 0
    internal var enableback = false
    private val mHandler = Handler()
    lateinit var dbH: DatabaseHandler
    internal var values: MutableList<String> = ArrayList()
    internal var flowlimit: Int = 0
    internal var purifierstatus: Int = 0
    internal var currentliters: Int = 0
    internal var currentcmd: Int = 0
    lateinit var validity: String
    lateinit var purifierId: String
    lateinit var ownerName: String
    internal var prepaid: Int = 0


    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService!!.initialize()) {
                Log.i(TAG, "Unable to initialize Bluetooth")
                finish()
            }
            // Automatically connects to the device upon successful start-up initialization.
            Log.i(TAG, "Able to reach BLE")


            mBluetoothLeService!!.setFlag(false)

            mBluetoothLeService!!.connect(botId)

        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService!!.disconnect()
            mBluetoothLeService = null
            Log.i(TAG, "not able to reach BLE")

        }
    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothLeService.ACTION_GATT_CONNECTED == action) {
                mConnected = true
                invalidateOptionsMenu()
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED == action) {
                mConnected = false

                invalidateOptionsMenu()
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED == action) {
                displayGattServices(mBluetoothLeService!!.supportedGattServices)

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE == action) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
                val output = intent.getStringExtra(BluetoothLeService.EXTRA_DATA)
                if (output.substring(0, 2) == CommandHandler.READ_LITERS.substring(6, 8)) {
                    currentliters = cmdH.readLiters(output.substring(2))
                    Log.i("reached here", "current liters are $currentliters")
                } else if (output.substring(0, 2) == CommandHandler.READ_FLOWLIMIT.substring(6, 8)) {
                    flowlimit = cmdH.readFlowlimit(output.substring(2))
                    Log.i("reached here", "flow limit is  $flowlimit")
                } else if (output.substring(0, 2) == CommandHandler.READ_VALIDITY.substring(6, 8)) {
                    validity = cmdH.readValidity(output.substring(2))
                    Log.i("reached here", "validity is $validity")
                } else if (output.substring(0, 2) == CommandHandler.READ_STATUS.substring(6, 8)) {
                    purifierstatus = cmdH.readStatus(output.substring(2))
                    Log.i("reached here", "status is $purifierstatus")
                } else if (output.substring(0, 2) == CommandHandler.READ_PREPAID.substring(6, 8)) {
                    prepaid = cmdH.readPrepaid(output.substring(2))
                    Log.i("reached here", "prepaid is $prepaid")
                }
                if (values.size != 0)
                    readChar()
                else {
                    val cmd = dbH.getCmd(currentcmd)
                    if (cmd != null && cmd.status == "ISSUE") {
                        val cmdStr = cmd.cmd
                        val pieces = cmdStr?.split("-".toRegex())!!.dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (pieces[0] == "101") {
                            var c = Integer.parseInt(pieces[1])
                            c += Integer.parseInt(pieces[2])
                            if (c == flowlimit) {
                                val cm = Command(cmd.id, cmd.cmd, "SUCCESS")
                                dbH.deleteCommand(cmd)
                                dbH.addAck(cm)
                            }
                        } else if (pieces[0] == "102") {
                            val date1 = pieces[1].substring(0, 4) + "-" + pieces[1].substring(4, 6) + "-" + pieces[1].substring(6, 8)
                            val OLD_FORMAT = "yyyy-MM-dd"
                            try {
                                val sdf = SimpleDateFormat(OLD_FORMAT)
                                var cmddate = sdf.parse(date1)
                                val purifierdate = sdf.parse(validity)
                                val calender = Calendar.getInstance()
                                calender.time = cmddate
                                calender.add(Calendar.DATE, Integer.parseInt(pieces[2]))
                                cmddate = calender.time
                                if (cmddate == purifierdate) {
                                    val cm = Command(cmd.id, cmd.cmd, "SUCCESS")
                                    dbH.deleteCommand(cmd)
                                    dbH.addAck(cm)
                                }
                            } catch (e: Exception) {
                            }

                        } else if (pieces[0] == "103") {
                            val cm = Command(cmd.id, cmd.cmd, "SUCCESS")
                            dbH.deleteCommand(cmd)
                            dbH.addAck(cm)
                        }
                    }
                    executeCommand()
                }

            } else if (BluetoothLeService.ACTION_DATA_WRITE == action) {
                val uuid = intent.getStringExtra("Char")
                Log.i("reached here", "reached here dude")
                val cmd = dbH.getCmd(currentcmd)
                if (cmd != null) {
                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
                    val cmdStr = cmd.cmd
                    val pieces = cmdStr?.split("-".toRegex())!!.dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (pieces[0] == "101") {
                        val flc = mBluetoothLeService!!.primeService.getCharacteristic(UUID.fromString(SampleGattAttributes.FLOW_LIMIT))
                        mBluetoothLeService!!.readCharacteristic(flc)
                    } else if (pieces[0] == "102") {
                        val flc = mBluetoothLeService!!.primeService.getCharacteristic(UUID.fromString(SampleGattAttributes.VALIDITY))
                        mBluetoothLeService!!.readCharacteristic(flc)
                    }
                }
                //executeCommand();
            } else if (BluetoothLeService.ACTION_DATA_NOTIFY == action) {

            } else if (BluetoothLeService.ACTION_DATA_COMMAND == action) {
                val data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA)
                //Log.i(TAG, "the data written in command is "+data);
                val flc = mBluetoothLeService!!.primeService.getCharacteristic(UUID.fromString(SampleGattAttributes.COMMAND))

                if (data.substring(0, 2) == "80") {
                    flc.setValue(Integer.parseInt(CommandHandler.READ_FLOWLIMIT, 16), BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                    Log.i(TAG, "inside flowlimit ")

                    mBluetoothLeService!!.writeCharacteristic(flc)

                } else if (data.substring(0, 2) == "81") {
                    flc.setValue(Integer.parseInt(CommandHandler.READ_VALIDITY, 16), BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                    Log.i(TAG, "inside validity ")

                    mBluetoothLeService!!.writeCharacteristic(flc)
                } else if (data.substring(0, 2) == "10") {
                    flc.setValue(Integer.parseInt(CommandHandler.READ_STATUS, 16), BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                    Log.i(TAG, "inside status ")

                    mBluetoothLeService!!.writeCharacteristic(flc)
                } else if (data.substring(0, 2) == "20") {
                    flc.setValue(Integer.parseInt(CommandHandler.READ_LITERS, 16), BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                    Log.i(TAG, "inside liters ")

                    mBluetoothLeService!!.writeCharacteristic(flc)
                } else if (data.substring(0, 2) == "12") {
                    flc.setValue(Integer.parseInt(CommandHandler.READ_PREPAID, 16), BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                    Log.i(TAG, "inside prepaid ")

                    mBluetoothLeService!!.writeCharacteristic(flc)
                } else if (data.substring(0, 2) == "11") {
                    Log.i(TAG, "notifications started ")
                } else {
                    mBluetoothLeService!!.readCharacteristic(flc)
                }
            } else if (BluetoothLeService.ACTION_WRITE_DESCRIPTION == action) {
                val flc = mBluetoothLeService!!.primeService.getCharacteristic(UUID.fromString(SampleGattAttributes.COMMAND))
                val `val` = 0x00000011
                flc.setValue(`val`, BluetoothGattCharacteristic.FORMAT_UINT32, 0)

                mBluetoothLeService!!.writeCharacteristic(flc)
            } else if (BluetoothLeService.ACTION_RETRY_NOTIFY == action) {
                iv_not_coonect.visibility = View.VISIBLE
                synctext_notcompleted.visibility = View.VISIBLE
                iv_coonect.visibility = View.GONE
                synctext_completed.visibility = View.GONE
                iv_sync.visibility = View.VISIBLE
                synctext.visibility = View.GONE
                progress.visibility = View.INVISIBLE
                progress1.visibility = View.INVISIBLE
                progress2.visibility = View.INVISIBLE
                mBluetoothLeService!!.disconnect()
                enableback = false

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)
        dbH = DatabaseHandler(this)
        cmdH = CommandHandler()
        if (intent != null) {
            botId = intent.getStringExtra("botId")
            purifierId = intent.getStringExtra("purifierId")!!
            ownerName = intent.getStringExtra("owner")!!
        }
        connectBLE()
        iv_close.setOnClickListener {
            mBluetoothLeService!!.disconnect()
            finish()
        }
        tv_botId.text = botId
        tv_pid.text = purifierId
        tv_owner.text = ownerName
    }

    private fun connectBLE() {

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
        }
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show()
        }
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
            servicelinked = 1
        }
        mDeviceName = "WaterWala Prime"//intent.getStringExtra(EXTRAS_DEVICE_NAME);

    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            connectBLE()  //recheck permission when RESULT_CANCELED

        }
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
            servicelinked = 1
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onDestroy() {
        if (servicelinked == 1) {
            unbindService(mServiceConnection)
            mBluetoothLeService!!.disconnect()
            Log.i(TAG, "not able to reach BLE")
        }
        mBluetoothLeService = null
        super.onDestroy()
    }

    fun executeCommand() {
        val result = issueCommand()
        if (!result) {
            mBluetoothLeService!!.setFlag(true)
            mBluetoothLeService!!.disconnect()
            CommonUtils.setStatus(currentliters, flowlimit, validity, purifierstatus, prepaid, "")
            iv_coonect.visibility = View.VISIBLE
            synctext_completed.visibility = View.VISIBLE
            iv_sync.visibility = View.VISIBLE
            synctext.visibility = View.GONE
            progress.visibility = View.INVISIBLE
            progress1.visibility = View.INVISIBLE
            progress2.visibility = View.INVISIBLE
            mHandler.postDelayed({
                finish()
            }, 2000)
            enableback = false
        }
    }

    private fun issueCommand(): Boolean {
        var cmd = dbH.getnextCmd()
        if (cmd == null) {
            if (changed == 0) {
                Log.i("waterwala", "command returned is null")
                return false
            } else {
                dbH.resetCursor()
                changed = 0
                cmd = dbH.getnextCmd()
                if (cmd == null)
                    return false
            }
        }
        currentcmd = cmd.id
        // Toast.makeText(this, cmd.id.toString() + "," + cmd.cmd.toString(), Toast.LENGTH_LONG).show()

        Log.i("waterwala status", cmd.id.toString() + " " + cmd.status + " " + cmd.cmd)
        val flc = mBluetoothLeService!!.primeService.getCharacteristic(UUID.fromString(SampleGattAttributes.COMMAND))
        val cmdH = CommandHandler()
        val c = cmd.cmd
        val slices = c?.split("-".toRegex())!!.dropLastWhile { it.isEmpty() }.toTypedArray()
        when (slices[0]) {

            "101"// add liters
            -> {
                val temp = Integer.parseInt(slices[1])
                if (temp == flowlimit) {
                    Log.i("WaterWala", "Reached equal state $temp $flowlimit")
                    val `val` = cmdH.writeLitersCmd(temp + Integer.parseInt(slices[2]))
                    flc.setValue(`val`, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                    cmd.status = "ISSUE"
                    dbH.changeState(cmd)
                    changed = 1
                    mBluetoothLeService!!.writeCharacteristic(flc)
                } else if (temp < flowlimit) {
                    Log.i("WaterWala", "Reached less state $temp $flowlimit")
                    executeCommand()
                } else if (temp > flowlimit) {
                    Log.i("WaterWala", "Reached greater state $temp $flowlimit")
                    executeCommand()
                }
            }

            "102"// add validity
            -> try {
                val date = slices[1].substring(0, 4) + "-" + slices[1].substring(4, 6) + "-" + slices[1].substring(6, 8)
                val OLD_FORMAT = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(OLD_FORMAT)
                val cmddate = sdf.parse(date)
                val purifierdate = sdf.parse(validity)
                if (cmddate == purifierdate) {
                    Log.i("WaterWala", "Both dates are equal $date $validity")
                    val calender = Calendar.getInstance()
                    calender.time = cmddate
                    calender.add(Calendar.DATE, Integer.parseInt(slices[2]))
                    val y = calender.get(Calendar.YEAR) % 2000
                    val year = if (y < 16) "0" + Integer.toHexString(y) else Integer.toHexString(y)
                    val m = calender.get(Calendar.MONTH) + 1
                    val month = "0" + Integer.toHexString(m)
                    val d = calender.get(Calendar.DAY_OF_MONTH)
                    val day = if (d < 16) "0" + Integer.toHexString(d) else Integer.toHexString(d)
                    Log.i("WaterWala", "the incremented date is 12$day$month$year")
                    val enddate = year + "" + month + "" + day + "81"
                    val `val` = Integer.parseInt(enddate, 16)
                    flc.setValue(`val`, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                    cmd.status = "ISSUE"
                    dbH.changeState(cmd)
                    changed = 1
                    mBluetoothLeService!!.writeCharacteristic(flc)
                } else {
                    Log.i("WaterWala", "Both dates are not equal $date $validity")
                    executeCommand()

                }
            } catch (e: Exception) {
                Log.i("WaterWala", "exception")
            }

            "103"// reset the parameters
            -> try {
                when (slices[1]) {

                    "100"// current liters
                    -> {
                        val `val` = cmdH.writeCurrentLitersCmd(Integer.parseInt(slices[2]))
                        flc.setValue(`val`, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                        changed = 1
                        mBluetoothLeService!!.writeCharacteristic(flc)
                    }

                    "101"// flowlimit
                    -> {
                        val flowliters = cmdH.writeLitersCmd(Integer.parseInt(slices[2]))
                        flc.setValue(flowliters, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                        changed = 1
                        mBluetoothLeService!!.writeCharacteristic(flc)
                    }

                    "102"// validity
                    -> {
                        val date = slices[2].substring(0, 4) + "-" + slices[2].substring(4, 6) + "-" + slices[2].substring(6, 8)
                        val OLD_FORMAT = "yyyy-MM-dd"
                        val sdf = SimpleDateFormat(OLD_FORMAT)
                        val cmddate = sdf.parse(date)
                        val calender = Calendar.getInstance()
                        calender.time = cmddate
                        val y = calender.get(Calendar.YEAR) % 2000
                        val year = if (y < 16) "0" + Integer.toHexString(y) else Integer.toHexString(y)
                        val m = calender.get(Calendar.MONTH) + 1
                        val month = "0" + Integer.toHexString(m)
                        val d = calender.get(Calendar.DAY_OF_MONTH)
                        val day = if (d < 16) "0" + Integer.toHexString(d) else Integer.toHexString(d)
                        Log.i("WaterWala", "the incremented date is 12$day$month$year")
                        val enddate = year + "" + month + "" + day + "81"
                        val validi = Integer.parseInt(enddate, 16)
                        flc.setValue(validi, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                        changed = 1
                        mBluetoothLeService!!.writeCharacteristic(flc)
                    }

                    "103"// rtc date
                    -> {
                        val OLD_FORMAT1 = "yyyy-MM-dd"
                        val sdf1 = SimpleDateFormat(OLD_FORMAT1)
                        val now1 = Calendar.getInstance()
                        val y1 = now1.get(Calendar.YEAR) % 2000
                        val m1 = now1.get(Calendar.MONTH) + 1 // Note: zero based!
                        val d1 = now1.get(Calendar.DAY_OF_MONTH)
                        val year1 = if (y1 < 16) "0" + Integer.toHexString(y1) else Integer.toHexString(y1)
                        val month1 = "0" + Integer.toHexString(m1)
                        val day1 = if (d1 < 16) "0" + Integer.toHexString(d1) else Integer.toHexString(d1)
                        Log.i("WaterWala", "the incremented date is $day1-$month1-20$year1")
                        val enddate1 = year1 + "" + month1 + "" + day1 + "22"
                        val validi1 = Integer.parseInt(enddate1, 16)
                        flc.setValue(validi1, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                        changed = 1
                        mBluetoothLeService!!.writeCharacteristic(flc)
                    }

                    "104"// rtc time
                    -> {
                        val now = Calendar.getInstance()
                        val h = now.get(Calendar.HOUR_OF_DAY)
                        val hrs = if (h < 16) "0" + Integer.toHexString(h) else Integer.toHexString(h)
                        val mi = now.get(Calendar.MINUTE)
                        val mins = if (mi < 16) "0" + Integer.toHexString(mi) else Integer.toHexString(mi)
                        val s = now.get(Calendar.SECOND)
                        val secs = if (s < 16) "0" + Integer.toHexString(s) else Integer.toHexString(s)
                        Log.i("WaterWala", "the time is $hrs:$mins:$secs")
                        val endtime = hrs + "" + mins + "" + secs + "23"
                        val rtctime = Integer.parseInt(endtime, 16)
                        flc.setValue(rtctime, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                        changed = 1
                        mBluetoothLeService!!.writeCharacteristic(flc)
                    }

                    "105"// purifier status
                    -> {
                        val status = "0000" + slices[2] + "10"
                        val pstatus = Integer.parseInt(status, 16)
                        flc.setValue(pstatus, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                        changed = 1
                        mBluetoothLeService!!.writeCharacteristic(flc)
                    }

                    "107"// purifier plan
                    -> {
                        val prepaid = "0000" + slices[2] + "12"
                        val pprepaid = Integer.parseInt(prepaid, 16)
                        flc.setValue(pprepaid, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
                        changed = 1
                        mBluetoothLeService!!.writeCharacteristic(flc)
                    }
                }
                cmd.status = "ISSUE"
                dbH.changeState(cmd)
            } catch (e: Exception) {
            }

        }
        return true
    }

    private fun displayData(data: String) {
        //Log.i("WaterWala","The data is "+data);
    }

    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return
        var uuid: String? = null
        for (gattService in gattServices) {
            uuid = gattService.uuid.toString()
            if (uuid != SampleGattAttributes.PRIME_SERVICE)
                continue
            values = CommandHandler.values
            readChar()
        }
    }

    internal fun readChar() {
        val flc = mBluetoothLeService!!.primeService.getCharacteristic(UUID.fromString(SampleGattAttributes.COMMAND))
        val chars = values[values.size - 1]
        val `val` = Integer.parseInt(chars, 16)
        flc.setValue(`val`, BluetoothGattCharacteristic.FORMAT_UINT32, 0)
        mBluetoothLeService!!.writeCharacteristic(flc)
        values.removeAt(values.size - 1)
    }

    override fun onBackPressed() {
        if (enableback) {
            super.onBackPressed()
            mBluetoothLeService!!.disconnect()
            finish()
        }
    }

    companion object {

        private val TAG = SyncActivity::class.java.simpleName
        private val REQUEST_ENABLE_BT = 5
        private val SYNC_ENABLE_BT = 8

        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_COMMAND)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_NOTIFY)
            intentFilter.addAction(BluetoothLeService.ACTION_WRITE_DESCRIPTION)
            intentFilter.addAction(BluetoothLeService.ACTION_RETRY_NOTIFY)

            return intentFilter
        }
    }


}
