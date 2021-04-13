package com.flow.android.kotlin.lockscreen.configuration.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.flow.android.kotlin.lockscreen.R
import com.flow.android.kotlin.lockscreen.databinding.FragmentConfigurationBinding
import com.flow.android.kotlin.lockscreen.main.view_model.MainViewModel
import com.flow.android.kotlin.lockscreen.preferences.ConfigurationPreferences
import com.flow.android.kotlin.lockscreen.configuration.adapter.AdapterItem
import com.flow.android.kotlin.lockscreen.configuration.adapter.CheckBoxAdapter
import com.flow.android.kotlin.lockscreen.configuration.adapter.CheckBoxItem
import com.flow.android.kotlin.lockscreen.configuration.adapter.ConfigurationAdapter
import com.flow.android.kotlin.lockscreen.lock_screen.LockScreenService

class ConfigurationFragment: Fragment() {
    private var viewBinding: FragmentConfigurationBinding? = null
    private val checkBoxAdapter = CheckBoxAdapter(arrayListOf())
    private val viewModel: MainViewModel by activityViewModels()

    private object Id {
        const val DisplayAfterUnlocking = 2249L
    }

    private val configurationAdapter: ConfigurationAdapter by lazy {
        ConfigurationAdapter(arrayListOf(
                AdapterItem.SubtitleItem(
                        subtitle = getString(R.string.lock_screen)
                ),
                AdapterItem.SwitchItem(
                        drawable = getDrawable(R.drawable.ic_round_lock_24),
                        isChecked = ConfigurationPreferences.getShowOnLockScreen(requireContext()),
                        onCheckedChange = { isChecked ->
                            ConfigurationPreferences.putShowOnLockScreen(requireContext(), isChecked)

                            if (isChecked) {
                                configurationAdapter.showItem(Id.DisplayAfterUnlocking)

                                val intent = Intent(requireContext(), LockScreenService::class.java)

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    LockScreenService().enqueueWork(requireContext(), intent)
                                    requireActivity().startForegroundService(intent)
                                } else {
                                    LockScreenService().enqueueWork(requireContext(), intent)
                                    requireActivity().startService(intent)
                                }
                            } else {
                                configurationAdapter.hideItem(Id.DisplayAfterUnlocking)
                                requireContext().sendBroadcast(Intent(LockScreenService.Action.StopSelf))
                            }
                        },
                        title = getString(R.string.show_on_lock_screen)
                ),
                AdapterItem.SwitchItem(
                        drawable = null,
                        id = Id.DisplayAfterUnlocking,
                        isChecked = ConfigurationPreferences.getDisplayAfterUnlocking(requireContext()),
                        onCheckedChange = { isChecked ->
                            ConfigurationPreferences.putDisplayAfterUnlocking(requireContext(), isChecked)
                        },
                        title = getString(R.string.display_after_unlocking)
                ),
                AdapterItem.SubtitleItem (
                        subtitle = getString(R.string.calendar)
                ),
                AdapterItem.ListItem(
                        adapter = checkBoxAdapter,
                        drawable = getDrawable(R.drawable.ic_round_calendar_today_24),
                        onClick = { listItemBinding, listItem ->

                        },
                        title = getString(R.string.calendar),
                )
        ))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = FragmentConfigurationBinding.inflate(layoutInflater, container, false)

        viewBinding?.recyclerView?.apply {
            adapter = configurationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uncheckedCalendarIds = ConfigurationPreferences.getUncheckedCalendarIds(requireContext())
        val checkBoxItems = viewModel.calendarDisplays()?.map {
            CheckBoxItem(
                    isChecked = uncheckedCalendarIds.contains(it.id.toString()).not(),
                    text = it.name,
                    onCheckedChange = { isChecked ->
                        if (isChecked)
                            ConfigurationPreferences.removeUncheckedCalendarId(requireContext(), it.id.toString())
                        else
                            ConfigurationPreferences.addUncheckedCalendarId(requireContext(), it.id.toString())

                        viewModel.refreshCalendarDisplays()
                    }
            )
        } ?: listOf()

        checkBoxAdapter.addAll(checkBoxItems)
    }

    private fun getDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(requireContext(), id)
}