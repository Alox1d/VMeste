package com.alox1d.vmeste.ui.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alox1d.vkvoicenotes.data.remote.VKService
import com.alox1d.vmeste.data.remote.FriendModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FriendsViewModel : ViewModel() {

    private val _friends = MutableLiveData<List<FriendModel>>()
    val friends: LiveData<List<FriendModel>> = _friends

    fun makeFriends() {
        val dispos = Single.fromCallable {
            VKService().getFriends()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                for (friendModel in list) {
                    Log.d("TAG", friendModel.toString())
                }
                _friends.value = list
            }, {
                // error
            })
    }
}