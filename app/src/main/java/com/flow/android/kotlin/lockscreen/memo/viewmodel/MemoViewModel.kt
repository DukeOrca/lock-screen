package com.flow.android.kotlin.lockscreen.memo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flow.android.kotlin.lockscreen.base.DataChanged
import com.flow.android.kotlin.lockscreen.base.DataChangedState
import com.flow.android.kotlin.lockscreen.persistence.entity.Memo
import com.flow.android.kotlin.lockscreen.repository.MemoRepository
import com.flow.android.kotlin.lockscreen.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MemoRepository(application)

    override fun onCleared() {
        repository.clearCompositeDisposable()
        super.onCleared()
    }

    suspend fun getAll() = repository.getAll()

    private val _dataChanged = MutableLiveData<DataChanged<Memo>>()
    val dataChanged: LiveData<DataChanged<Memo>>
        get() = _dataChanged

    private val _refresh = SingleLiveEvent<Unit>()
    val refresh: LiveData<Unit>
        get() = _refresh

    fun callRefresh() {
        _refresh.call()
    }

    fun delete(memo: Memo) {
        repository.delete(memo) { _dataChanged.value = DataChanged(memo, DataChangedState.Deleted) }
    }

    fun insert(memo: Memo) {
        repository.insert(memo) { _dataChanged.value = DataChanged(memo, DataChangedState.Inserted) }
    }

    fun update(memo: Memo, onComplete: (() -> Unit)? = null) {
        repository.update(memo) {
            onComplete?.invoke()
            _dataChanged.value = DataChanged(memo, DataChangedState.Updated)
        }
    }

    fun updateAll(list: List<Memo>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAll(list)
        }
    }
}