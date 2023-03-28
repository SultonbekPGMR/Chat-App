package com.sultonbek1547.chatapprealtime

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sultonbek1547.chatapprealtime.utils.MyData.USER
import com.sultonbek1547.chatapprealtime.utils.MyData.screenLengthItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        screenLengthItem = (getScreenWidthInPixels(this) * 0.6).toInt()


    }

    override fun onStop() {
        super.onStop()
        CoroutineScope(IO).launch {
            USER.statusTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            USER.isOnline = "false"
            FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().uid!!).setValue(USER)
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(IO).launch {
            USER.statusTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            USER.isOnline = "true"
            FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().uid!!).setValue(USER)
        }
    }

    private fun getScreenWidthInPixels(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
}