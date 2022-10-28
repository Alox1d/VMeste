package com.alox1d.vkvoicenotes.data.remote

import com.alox1d.vmeste.data.remote.FriendModel
import com.vk.api.sdk.VK

class VKService {

    fun getFriends(): List<FriendModel> {
        return VK.executeSync(VKFriendsCommand())
    }

}