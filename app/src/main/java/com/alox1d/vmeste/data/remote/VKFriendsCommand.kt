package com.alox1d.vkvoicenotes.data.remote

import com.alox1d.vmeste.data.remote.FriendModel
import com.vk.api.sdk.VKApiJSONResponseParser
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject


class VKFriendsCommand() : ApiCommand<List<FriendModel>>() {

    companion object {
        const val RETRY_COUNT = 3
    }

    override fun onExecute(manager: VKApiManager): List<FriendModel> {
        return getFriends(manager)
    }

    private fun getFriends(manager: VKApiManager): List<FriendModel> {
        val uploadInfoCall = VKMethodCall.Builder()
            .method("friends.get")
            .args("fields", "city")
            .version(manager.config.version)
            .build()
        return manager.execute(uploadInfoCall, ServerParser())
    }

    private class ServerParser : VKApiJSONResponseParser<List<FriendModel>> {
        override fun parse(responseJson: JSONObject): List<FriendModel> {
            try {
                val list = mutableListOf<FriendModel>()
                val joResponse = responseJson.getJSONObject("response").getJSONArray("items")
                for (userIndex in 0 until joResponse.length()){
                    val parsed = joResponse.getJSONObject(userIndex)
                    try {
                    val user = FriendModel(name = parsed.getString("first_name") + " " + parsed.getString("last_name"),
                    city = parsed.getJSONObject("city").getString("title"))
                    list.add(user)
                    } catch (ex: JSONException) {
                        Unit
                    }
                }
                return list
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

}