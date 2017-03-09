package nz.co.cic.ble.scanner

import com.beust.klaxon.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import org.json.JSONObject
import java.util.*

/**
 * Created by dipshit on 10/03/17.
 */

class ScanFilter(val name: String, val messageKeys: Array<String>){

    private var uuid: UUID? = null
    private var messageUuids: Map<UUID, String>? = null
    private val parser: Parser = Parser()

    init {
        this.uuid = UUID.nameUUIDFromBytes(name.toByteArray())

        this.messageUuids = this.messageKeys.associateBy {
            keySelector ->

            UUID.nameUUIDFromBytes(keySelector.toByteArray())
        }
    }

    fun filter(radioFlow: Flowable<JSONObject>): Flowable<JsonObject>{
        return Flowable.create({
            subscriber ->
            radioFlow.subscribe({
                jsonInfo ->

                var json = parser.parse(StringBuilder(jsonInfo.toString())) as JsonObject
                
                subscriber.onNext(runFilter(json))

            }, {
                err ->
                subscriber.onError(err)
            }, {
                subscriber.onComplete()
            })
        }, BackpressureStrategy.BUFFER)
    }

    private fun runFilter(json: JsonObject): JsonObject{
        var filtered = getServiceById(json)
        filtered?.set("id", this.name)
        filtered?.set("messages", nameMessages(filtered?.array<JsonObject>("messages")))
        return filtered!!
    }

    private fun nameMessages(json: JsonArray<JsonObject>?): JsonArray<JsonObject>?{
        var it = json?.iterator()
        while(it!!.hasNext()){
            var message = it.next()
            var id = UUID.fromString(message.get("id") as String)
            var name = this.messageUuids?.get(id)
            message.set("id", name)
        }
        return json
    }

    private fun getServiceById(json: JsonObject): JsonObject? {
        var prefiltered = json.array<JsonObject>("messages")
        var filtered = prefiltered?.filter {
            it.string("id") == this.uuid.toString()
        }
        return filtered?.get(0)
    }



}
