package com.zechadrian.worktimemanager.fragments

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.opencsv.CSVWriter
import com.zechadrian.worktimemanager.R
import com.zechadrian.worktimemanager.database.DatabaseTable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    private val MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "start_time" -> {
                timePicker(preference)
            }
            "break_time" -> {
                timePicker(preference)
            }
            "backup_directory" -> {
                Snackbar.make(view!!, "TODO", Snackbar.LENGTH_LONG).show()
            }
            "backup" -> {
                backup()
            }
            "restore" -> {
                restore()
            }
            "export_directory" -> {
                Snackbar.make(view!!, "TODO", Snackbar.LENGTH_LONG).show()
            }
            "export" -> {
                export()
            }
            "import" -> {
                import()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        updateSummary()

        val switch = findPreference<SwitchPreference>("night_mode") ?: return
        switch.setOnPreferenceChangeListener { _, _ ->
            if (switch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                activity?.recreate()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                activity?.recreate()
            }
            switch.isChecked = false
            true
        }
    }

    private fun updateSummary() {
        val startTimePreference = findPreference<Preference>("start_time")
        startTimePreference?.setSummaryProvider {
            val text: String? = PreferenceManager.getDefaultSharedPreferences(view?.context).getString("start_time", "08:00")
            if (TextUtils.isEmpty(text)) {
                "Not set"
            } else {
                text
            }
        }

        val breakTimePreference = findPreference<Preference>("break_time")
        breakTimePreference?.setSummaryProvider {
            val text: String? = PreferenceManager.getDefaultSharedPreferences(view?.context).getString("break_time", "01:00")
            if (TextUtils.isEmpty(text)) {
                "Not set"
            } else {
                text
            }
        }
    }

    private fun backup() {
        checkPermission()

        try {
            val currentDB = File(Environment.getDataDirectory(), "/data/com.zechadrian.worktimemanager/databases/work_times.db")
            val backupDB = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "work_times.db")

            if (currentDB.exists()) {
                val source = FileInputStream(currentDB).channel
                val destination = FileOutputStream(backupDB).channel
                destination.transferFrom(source, 0, source.size())
                source.close()
                destination.close()
            }
            Snackbar.make(view!!, R.string.backup_created, Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("SettingsFragment", e.toString())
        }
    }

    private fun restore() {
        checkPermission()

        try {
            val currentDB = File(Environment.getDataDirectory(), "/data/com.zechadrian.worktimemanager/databases/work_times.db")
            val backupDB = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "work_times.db")

            if (backupDB.exists()) {
                val source = FileInputStream(backupDB).channel
                val destination = FileOutputStream(currentDB).channel
                destination.transferFrom(source, 0, source.size())
                source.close()
                destination.close()
            }
            Snackbar.make(view!!, R.string.backup_restore,Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("SettingsFragment", e.toString())
        }
    }

    private fun export() {
        checkPermission()

        val exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }

        val workTimes = DatabaseTable(view!!.context).getData()
        val file = File(exportDir, "work_times.csv")

        val csvHeader: Array<String> = arrayOf(
            getString(R.string.date),
            getString(R.string.start_time),
            getString(R.string.end_time),
            getString(R.string.break_time),
            getString(R.string.total_time),
            getString(R.string.comment)
        )

        try {
            file.createNewFile()
            val csvWriter = CSVWriter(FileWriter(file))
            csvWriter.writeNext(csvHeader)

            for (workTime in workTimes) {
                val string: Array<String> = arrayOf(workTime.date, workTime.startTime, workTime.endTime, workTime.breakTime, workTime.totalTime, workTime.comment)
                csvWriter.writeNext(string)
            }

            csvWriter.close()
            Snackbar.make(view!!, R.string.exported, Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("SettingsFragment", e.toString())
        }
    }

    private fun import() {
        checkPermission()

        val exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val workTimes = DatabaseTable(view!!.context).getData()
        val file = File(exportDir, "work_times.csv")

        // TODO: Work on this

        try {
            file.createNewFile()
            val csvWriter = CSVWriter(FileWriter(file))

            for (workTime in workTimes) {
                val string: Array<String> = arrayOf(workTime.date, workTime.startTime, workTime.endTime, workTime.breakTime, workTime.totalTime, workTime.comment)
                csvWriter.writeNext(string)
            }

            csvWriter.close()
            Snackbar.make(view!!, R.string.imported, Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("SettingsFragment", e.toString())
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun timePicker(preference: Preference) {
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            val sharedPreference = PreferenceManager.getDefaultSharedPreferences(view!!.context)
            sharedPreference.edit().putString(preference.key, timeFormat.format(calendar.time)).apply()
            updateSummary()
        }
        context?.let {
            TimePickerDialog(
                it,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
}
