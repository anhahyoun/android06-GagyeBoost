package com.example.gagyeboost.ui.map

import android.util.Log
import androidx.lifecycle.*
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.formatter
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Filter
import kotlinx.coroutines.launch
import java.util.*

class MapViewModel(private val repository: Repository) : ViewModel() {

    val byteMoneyType = MutableLiveData(EXPENSE)
    val intMoneyType: LiveData<Int> = Transformations.map(byteMoneyType) { it.toInt() }

    val intStartMoney = MutableLiveData(0)
    val startMoney: LiveData<String> =
        Transformations.map(intStartMoney) { formatter.format(it) + "원" }

    val intEndMoney = MutableLiveData(300000)
    val endMoney: LiveData<String> = Transformations.map(intEndMoney) {
        if (it == Int.MAX_VALUE) {
            Log.e("viewmodel", it.toString())
            formatter.format(it) + "원 이상"
        } else {
            Log.e("viewmodel", it.toString())
            formatter.format(it) + "원"
        }
    }

    val categoryList = MutableLiveData<List<Int>>()
    var startYear: Int = 1900
    var startMonth: Int = 1
    var startDay: Int = 1
    var endYear: Int = 2500
    var endMonth: Int = 12
    var endDay: Int = 31
    var startLatitude: Float = 37.566029F
    var startLongitude: Float = 126.897156F
    var endLatitude: Float = 37.481645F
    var endLongitude: Float = 127.213158F

    val filterData = MutableLiveData<List<AccountBook>>()

    //viewModel 공유하면 다시 map화면 돌아왔을때 init
    fun setInitData() {
        loadAllCategoryID()
        byteMoneyType.value = EXPENSE
        intStartMoney.value = 0
        intEndMoney.value = 300000
        startYear = Calendar.getInstance().get(Calendar.YEAR)
        startMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        startDay = 1
        endYear = startYear
        endMonth = startMonth
        endDay = Calendar.getInstance().getActualMaximum(Calendar.DATE)
        // 화면에 보이는 위도/경도로 설정 해야함
        startLatitude = 0.0f
        startLongitude = 0.0f
        endLatitude = 0.0f
        endLongitude = 0.0f
    }

    fun loadAllCategoryID() {
        viewModelScope.launch {
            categoryList.postValue(repository.loadAllCategoryID())
        }
    }

    fun setMoney(start: Int, end: Int) {
        intStartMoney.value = start
        intEndMoney.value = end
    }


    fun loadFilterData() {
        viewModelScope.launch {
            val data = setFilter()
            Log.e("viewModel filter data", data.toString())
            val filter = repository.loadFilterData(data)
            filterData.postValue(filter)
        }
    }

    private fun setFilter() = Filter(
        byteMoneyType.value ?: EXPENSE,
        startYear,
        startMonth,
        startDay,
        endYear,
        endMonth,
        endDay,
        startLatitude,
        startLongitude,
        endLatitude,
        endLongitude,
        intStartMoney.value ?: 0,
        intEndMoney.value ?: 300000,
        categoryList.value ?: listOf()
    )

    fun setPeriod(startDate: Date, endDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        startYear = calendar.get(Calendar.YEAR)
        startMonth = calendar.get(Calendar.MONTH) + 1
        startDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.time = endDate
        endYear = calendar.get(Calendar.YEAR)
        endMonth = calendar.get(Calendar.MONTH) + 1
        endDay = calendar.get(Calendar.DAY_OF_MONTH)

        loadFilterData()
    }
}
