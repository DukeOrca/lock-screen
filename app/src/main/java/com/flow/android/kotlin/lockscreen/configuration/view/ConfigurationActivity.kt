package com.flow.android.kotlin.lockscreen.configuration.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.flow.android.kotlin.lockscreen.R
import com.flow.android.kotlin.lockscreen.calendar.CalendarLoader
import com.flow.android.kotlin.lockscreen.preferences.ConfigurationPreferences
import com.flow.android.kotlin.lockscreen.configuration.adapter.AdapterItem
import com.flow.android.kotlin.lockscreen.configuration.adapter.CheckBoxAdapter
import com.flow.android.kotlin.lockscreen.configuration.adapter.CheckBoxItem
import com.flow.android.kotlin.lockscreen.configuration.adapter.ConfigurationAdapter
import com.flow.android.kotlin.lockscreen.configuration.calendar.view.CalendarConfigurationFragment
import com.flow.android.kotlin.lockscreen.configuration.display.view.DisplayConfigurationFragment
import com.flow.android.kotlin.lockscreen.configuration.lockscreen.view.LockScreenConfigurationFragment
import com.flow.android.kotlin.lockscreen.configuration.viewmodel.ConfigurationViewModel
import com.flow.android.kotlin.lockscreen.databinding.ActivityConfigurationBinding

class ConfigurationActivity: AppCompatActivity() {
    private val viewBinding by lazy { ActivityConfigurationBinding.inflate(layoutInflater) }
    private val viewModel: ConfigurationViewModel by viewModels()

    private val activity = this
    private val checkBoxAdapter = CheckBoxAdapter(arrayListOf())

    private val adapter: ConfigurationAdapter by lazy {
        ConfigurationAdapter(arrayListOf(
                AdapterItem.Item(
                        drawable = ContextCompat.getDrawable(this, R.drawable.ic_round_today_24),
                        description = getString(R.string.configuration_activity_006),
                        onClick = { _, _ ->
                            addFragment(CalendarConfigurationFragment())
                        },
                        title = getString(R.string.calendar)
                ),
                AdapterItem.Item(
                        drawable = ContextCompat.getDrawable(this, R.drawable.ic_mobile_48px),
                        description = getString(R.string.configuration_activity_002),
                        onClick = { _, _ ->
                            addFragment(DisplayConfigurationFragment())
                        },
                        title = getString(R.string.configuration_activity_001)
                ),
                AdapterItem.Item(
                        drawable = ContextCompat.getDrawable(this, R.drawable.ic_round_screen_lock_portrait_24),
                        description = getString(R.string.configuration_activity_000),
                        onClick = { _, _ ->
                            addFragment(LockScreenConfigurationFragment())
                        },
                        title = getString(R.string.configuration_activity_003)
                )
        ))
    }

    object Name {
        private const val Prefix = "com.flow.android.kotlin.lockscreen.configuration.view" +
                ".ConfigurationActivity.Name"
        const val ConfigurationChange = "$Prefix.ConfigurationChange"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initializeToolbar(viewBinding.toolbar)

        viewBinding.recyclerView.apply {
            adapter = this@ConfigurationActivity.adapter
            layoutManager = LinearLayoutManager(activity)
        }

        val uncheckedCalendarIds = ConfigurationPreferences.getUncheckedCalendarIds(this)
        val checkBoxItems = CalendarLoader.calendars(contentResolver).map {
            CheckBoxItem(
                    isChecked = uncheckedCalendarIds.contains(it.id.toString()).not(),
                    text = it.name,
                    onCheckedChange = { isChecked ->
                        if (isChecked)
                            ConfigurationPreferences.removeUncheckedCalendarId(this, it.id.toString())
                        else
                            ConfigurationPreferences.addUncheckedCalendarId(this, it.id.toString())
                    }
            )
        }

        checkBoxAdapter.addAll(checkBoxItems)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.isNotEmpty()) {
            super.onBackPressed()
            return
        }

        val intent = Intent().apply {
            putExtra(Name.ConfigurationChange, viewModel.configurationChanged)
        }

        setResult(RESULT_OK, intent)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left)
    }

    private fun initializeToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .add(R.id.fragment_container_view, fragment, fragment.tag)
                .commit()
    }
}