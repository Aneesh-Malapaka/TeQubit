package com.stormatte.tequbit

import android.icu.util.Calendar
import android.icu.util.LocaleData
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import java.util.UUID

class QubitViewModel :ViewModel(){

    //user preference variables

    private val _preferenceSelected =
        mutableStateListOf(
            PreferenceSelected(),
            PreferenceSelected(),
            PreferenceSelected()
        )
    var preferenceSelected: SnapshotStateList<PreferenceSelected> = _preferenceSelected

    private val _knowledgePreference = mutableStateOf("")
    val knowledgePreference: State<String> = _knowledgePreference

    private val _selectedUsages = mutableStateListOf<String>()
    val selectedUsages: SnapshotStateList<String> = _selectedUsages

    private val _selectedResponseWays = mutableStateListOf<String>()
    val selectedResponseWays: SnapshotStateList<String> = _selectedResponseWays

    fun setKnowledgePreference(value: String) {
        _knowledgePreference.value = value
    }

   fun generateChatID() :String {
//        val date2 = Calendar.getInstance().timeInMillis
//            val chatID = "chat"+UUID.randomUUID()
        val chatID = "chat"+Date().time+"-"+UUID.randomUUID()
        println("chatID is $chatID ")
        return chatID
    }

}
