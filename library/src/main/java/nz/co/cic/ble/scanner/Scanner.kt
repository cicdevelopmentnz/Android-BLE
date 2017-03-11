package nz.co.cic.ble.scanner


import android.content.Context
import com.beust.klaxon.JsonObject
import io.reactivex.Flowable
import org.json.JSONObject

/**
 * Created by dipshit on 3/03/17.
 */

class Scanner(private val c: Context) {

    private var radio: Radio? = null
    private var connected: Boolean = false

    init {
        this.radio = Radio(c)
        this.radio?.enable()
    }

    fun start(): Flowable<JSONObject>? {
        return this.radio?.start()
    }

    fun startFiltered(name: String, keys: Array<String>): Flowable<JsonObject>? {
        var filter = ScanFilter(name, keys)
        return filter.filter(this.radio?.start())
    }

    fun stop() {
        if (this.radio != null) {
            this.radio?.stop()
        }
    }

}
