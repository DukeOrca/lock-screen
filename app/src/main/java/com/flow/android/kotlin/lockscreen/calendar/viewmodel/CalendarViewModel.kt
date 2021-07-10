package com.flow.android.kotlin.lockscreen.calendar.viewmodel

import android.app.Application
import android.content.ContentResolver
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flow.android.kotlin.lockscreen.calendar.CalendarLoader
import com.flow.android.kotlin.lockscreen.calendar.model.Model
import com.flow.android.kotlin.lockscreen.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarViewModel(application: Application): AndroidViewModel(application) {
    val contentResolver: ContentResolver = application.contentResolver

    private val _calendars = MutableLiveData<List<Model.Calendar>>()
    val calendars: LiveData<List<Model.Calendar>>
        get() = _calendars

    private val _calendarEvents = MutableLiveData<List<Model.CalendarEvent>>()

    private val _refresh = SingleLiveEvent<Unit>()
    val refresh: LiveData<Unit>
        get() = _refresh

    fun callRefresh() {
        _refresh.call()
    }

    fun postValue() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _calendars.postValue(CalendarLoader.calendarDisplays(contentResolver))
            }
        }
    }
}