// SharedViewModel.kt
package com.attendance.attendancemate.view

import android.content.SharedPreferences
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.attendance.attendancemate.data.SessionData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    private val _sessionData = MutableLiveData<SessionData?>()
    val sessionData: LiveData<SessionData?> = _sessionData

    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> = _email

    private val _password = MutableLiveData<String?>()
    val password: LiveData<String?> = _password

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val _dataRetentionAgreed = MutableLiveData<Boolean>()
    val dataRetentionAgreed: LiveData<Boolean> = _dataRetentionAgreed

    private val _hasSeenPolicy = MutableLiveData<Boolean>()
    val hasSeenPolicy: LiveData<Boolean> = _hasSeenPolicy


    init {
        // Load saved data from SharedPreferences when ViewModel is created
        val cookie = sharedPreferences.getString("cookie", null)
        val expiry = sharedPreferences.getLong("cookieExpiry", -1)
        _sessionData.value = if (cookie != null && expiry != -1L) {
            SessionData(cookie, expiry)
        } else {
            null
        }

        _email.value = sharedPreferences.getString("email", "")
        _date.value = sharedPreferences.getString("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
        _password.value = sharedPreferences.getString("password", "")
        _dataRetentionAgreed.value = sharedPreferences.getBoolean("dataRetentionAgreed", false)
        _hasSeenPolicy.value = sharedPreferences.getBoolean("hasSeenPolicy", false)
    }

    fun setSessionData(sessionData: SessionData) {
        _sessionData.value = sessionData
        with(sharedPreferences.edit()) {
            putString("cookie", sessionData.cookie)
            putLong("cookieExpiry", sessionData.expiry)
            apply()
        }
    }

    fun setEmail(email: String) {
        _email.value = email
        sharedPreferences.edit().putString("email", email).apply()
    }

    fun setDate(date: String) {
        _date.value = date
        sharedPreferences.edit().putString("date", date).apply()
    }

    fun setPassword(password: String) {
        _password.value = password
        sharedPreferences.edit().putString("password", password).apply()
    }

    fun setDataRetentionAgreed(agreed: Boolean) {
        _dataRetentionAgreed.value = agreed
        sharedPreferences.edit().putBoolean("dataRetentionAgreed", agreed).apply()
    }

    fun setHasSeenPolicy(seen: Boolean) {
        _hasSeenPolicy.value = seen
        sharedPreferences.edit().putBoolean("hasSeenPolicy", seen).apply()
    }

    fun clearData() {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        _sessionData.value = null
        _email.value = ""
        _date.value = currentDate
        _password.value = ""

        val editor = sharedPreferences.edit()
        editor.remove("cookie")
        editor.remove("cookieExpiry")
        editor.remove("email")
        editor.remove("password")
        editor.remove("date")
        editor.apply()
    }

    // Check if the session is expired
    fun isSessionExpired(): Boolean {
        val sessionData = _sessionData.value ?: return true
        return System.currentTimeMillis() > sessionData.expiry
    }
}
