package nz.co.cic.ble.scanner

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.array
import com.beust.klaxon.string
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import org.json.JSONObject
import java.util.*

/**
 * Created by dipshit on 10/03/17.
 */

class ScanFilter(val name: String, val messageKeys: Array<String>){

    private var uuid: UUID? = null
    private var messageUuids: Array<UUID>? = null
    private val parser: Parser = Parser()

    init {
        this.uuid = UUID.nameUUIDFromBytes(name.toByteArray())
        this.messageUuids = Array(messageKeys.size,{
                ix ->
                UUID.nameUUIDFromBytes(messageKeys.get(ix).toByteArray())
        })
    }

    fun filter(radioFlow: Flowable<JSONObject>): Flowable<JsonObject>{
        return Flowable.create({
            subscriber ->
            radioFlow.subscribe({
                jsonInfo ->

                var json = parser.parse(StringBuilder(jsonInfo.toString())) as JsonObject

                var prefiltered = json.array<JsonObject>("messages")
                //Save the lungs
                var filtered = prefiltered?.filter {
                    it.string("id") == this.uuid.toString()
                }

                json.set("messages", filtered)

                subscriber.onNext(json)

            }, {
                err ->
                subscriber.onError(err)
            }, {
                subscriber.onComplete()
            })
        }, BackpressureStrategy.BUFFER)
    }



}
