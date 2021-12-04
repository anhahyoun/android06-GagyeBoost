package com.example.gagyeboost.ui.home

import androidx.lifecycle.*
import com.example.gagyeboost.common.NOW_YEAR
import com.example.gagyeboost.common.intToStringDate
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.CustomDate
import com.example.gagyeboost.model.data.MonthTotalMoney

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    private val _selectedDate = MutableLiveData<CustomDate?>()
    val selectedDate: LiveData<CustomDate?> = _selectedDate

    val monthTotalMoney = MonthTotalMoney(MutableLiveData(), MutableLiveData(), MutableLiveData())

    val detailItemList = Transformations.switchMap(_selectedDate) { date ->
        if (date == null) {
            MutableLiveData(listOf())
        } else {
            repository.flowLoadDayData(date.year, date.month, date.day).asLiveData()
        }
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == NOW_YEAR) "${month}월" else "${year}년 ${month}월"
        _yearAndMonth.value = stringDate
        _selectedDate.value = null
    }

    fun setSelectedDate(dateItem: CustomDate) {
        _selectedDate.value = dateItem
    }

    fun getTodayString() = _selectedDate.value?.let {
        intToStringDate(it.year, it.month, it.day)
    } ?: ""

    fun loadTotalMoney(){

    }
}
