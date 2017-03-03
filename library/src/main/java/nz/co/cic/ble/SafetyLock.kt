package nz.co.cic.ble

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

/**
 * Created by dipshit on 4/03/17.
 */

class SafetyLock(private val mActivity: Activity){

    private var requestCode: Int = 1001
    private var observerLock: ObservableEmitter<Boolean>? = null

    init {

    }

    fun lock(){
     //   return Observable.create<Boolean> { subscriber ->
            println("Subscribed")
         //   this.observerLock = subscriber
            if(!checkPermissions()) {
                println("Requesting permissions")
                requestPermissions()
            }else{
         //       this.observerLock!!.onComplete()
            }
        //}
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(mActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), requstCode)
    }
    private fun checkPermissions(): Boolean {
        print("Checking permissions")
        return(ContextCompat.checkSelfPermission(mActivity.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mActivity.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    fun onRequestPermissionsResult(reqCode: Int, permissions: Array<String>, grantResults: Array<Int>){
        when(reqCode){
            requestCode -> {
                if(grantResults.size > 0 && this.observerLock != null){
     //               this.observerLock!!.onComplete()
                }else{
       //             this.observerLock!!.onError(Throwable("Permissions not granted"))
                }
                println(grantResults)
            }
            else -> {

            }
        }
    }
}