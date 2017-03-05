package nz.co.cic.ble

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.reactivex.FlowableEmitter

/**
 * Created by dipshit on 4/03/17.
 */

class SafetyLock(private val mActivity: Activity) : MultiplePermissionsListener{

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
        if(report!!.areAllPermissionsGranted()){
            println("I have the powerrrr")
        }
    }

    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
        token!!.continuePermissionRequest()
    }

    init {
        Dexter.withActivity(mActivity)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(this)
    }


}