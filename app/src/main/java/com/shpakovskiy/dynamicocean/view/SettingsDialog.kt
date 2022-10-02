package com.shpakovskiy.dynamicocean.view

import android.app.Activity
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shpakovskiy.dynamicocean.R
import com.shpakovskiy.dynamicocean.common.Constants
import com.shpakovskiy.dynamicocean.repository.GameSettingsRepository

class SettingsDialog(
    private val activity: Activity,
    private val gameSettingsRepository: GameSettingsRepository
) : SeekBar.OnSeekBarChangeListener {
    private lateinit var difficultySeekBar: SeekBar
    private lateinit var currentDifficultyTextView: TextView

    private var currentDifficulty = gameSettingsRepository.difficulty

    fun show() {
        val customDialogView = activity.layoutInflater.inflate(R.layout.settings_dialog, null)

        difficultySeekBar = customDialogView.findViewById(R.id.difficulty_seek_bar)
        currentDifficultyTextView = customDialogView.findViewById(R.id.current_difficulty)

        difficultySeekBar.max = Constants.Difficulty.MIN
        difficultySeekBar.min = Constants.Difficulty.MAX
        difficultySeekBar.setOnSeekBarChangeListener(this)

        updateCurrentDifficulty(currentDifficulty)

        val alertDialog = MaterialAlertDialogBuilder(activity)
            .setView(customDialogView)
            .setPositiveButton(R.string.settings_button_save) { dialog, _ ->
                gameSettingsRepository.difficulty = currentDifficulty
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {
        seekBar?.let {
            updateCurrentDifficulty(seekBar.progress)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) = Unit
    override fun onStopTrackingTouch(p0: SeekBar?) = Unit

    private fun updateCurrentDifficulty(difficulty: Int) {
        currentDifficulty = difficulty
        currentDifficultyTextView.text = "$difficulty"
        difficultySeekBar.progress = difficulty
    }
}