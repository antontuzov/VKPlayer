/*
 * Copyright (C) 2016 IRA-Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.irateam.vkplayer.ui.viewholder

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.irateam.vkplayer.R
import com.irateam.vkplayer.models.Audio
import com.irateam.vkplayer.ui.ItemTouchHelperViewHolder
import com.irateam.vkplayer.ui.viewholder.AudioViewHolder.State.*

/**
 * Main ViewHolder for Audio model. Contains title, artist, duration of audio.
 * Also shows different states of audio. States of audio shows in this hierarchy:
 * 1. Cover of audio that contains first letter of artist and material background
 * 2. Preparing/Playing/Paused state that shows above cover with dark overlay and icon
 * 3. Checked state with solid grey overlay and icon
 * 4. Sorting state that shows above cover with dark overlay and icon. When this state active
 * checked and playing states are invisible.
 *
 * @author Artem Glugovsky
 */
class AudioViewHolder : RecyclerView.ViewHolder, ItemTouchHelperViewHolder {

    private val resources: Resources

    val title: TextView
    val artist: TextView
    val duration: TextView
    val cachedIcon: ImageView

    /**
     * ImageView that contains rounded cover with one char and material background
     */
    val cover: ImageView

    /**
     * View that contains overlays for different states of item
     */
    val coverOverlay: View

    /**
     * View that contains overlay for checked state of item
     */
    val coverCheckedOverlay: View

    /**
     * ProgressBar that shows when audio item is preparing
     */
    val preparingProgress: ProgressBar

    /**
     * FrameLayout that holds all cover views
     */
    val coverHolder: FrameLayout

    private var state: State = NONE
    private var checked = false
    private var sorting = false
    private var cached = false

    constructor(v: View) : super(v) {
        setIsRecyclable(false)
        resources = v.context.resources

        title = v.findViewById(R.id.title) as TextView
        artist = v.findViewById(R.id.artist) as TextView
        duration = v.findViewById(R.id.duration) as TextView
        cover = v.findViewById(R.id.cover) as ImageView
        coverOverlay = v.findViewById(R.id.cover_overlay)
        coverCheckedOverlay = v.findViewById(R.id.cover_checked_overlay)
        preparingProgress = v.findViewById(R.id.preparing_progress) as ProgressBar
        coverHolder = v.findViewById(R.id.cover_holder) as FrameLayout
        cachedIcon = v.findViewById(R.id.cached_icon) as ImageView
    }

    override fun onItemSelected() {
    }

    override fun onItemClear() {
    }

    /**
     * Configure item for concrete audio
     * @param audio - Concrete audio
     */
    fun setAudio(audio: Audio) {
        title.text = audio.title
        artist.text = audio.artist
        duration.text = String.format("%02d:%02d", audio.duration / 60, audio.duration % 60)

        setCover(audio)
    }

    /**
     * Configure cover of audio. Basically consists with material colored background and first
     * letter of artist.
     * @param audio - Audio for configuring cover
     */
    private fun setCover(audio: Audio) {
        val char = audio.artist[0].toString()
        val color = ColorGenerator.MATERIAL.getColor(audio.artist)
        val coverDrawable = TextDrawable.builder().buildRound(char, color)
        cover.setImageDrawable(coverDrawable)
    }

    /**
     * Set up playing state of audio item
     * @param state - Playing state of item
     *
     * NONE - Default state of item. Invisible cover overlays and progress bar.
     *
     * PREPARE - State of loading audio item. Visible dark overlay and progress bar.
     *
     * PLAY - State of playing audio item. Visible dark overlay with play icon, invisible progress
     * bar.
     *
     * PAUSE - State of paused audio item. Visible dark overlay with pause icon, invisible progress
     * bar.
     */
    fun setPlayingState(state: State) {
        this.state = state
        when (state) {
            NONE -> {
                coverOverlay.visibility = GONE
                preparingProgress.visibility = GONE
            }
            PREPARE -> {
                coverOverlay.setBackgroundResource(R.drawable.overlay_item_audio)
                preparingProgress.visibility = VISIBLE
                coverOverlay.visibility = VISIBLE
            }
            PLAY -> {
                coverOverlay.setBackgroundResource(R.drawable.overlay_item_audio_play)
                preparingProgress.visibility = GONE
                coverOverlay.visibility = VISIBLE
            }
            PAUSE -> {
                coverOverlay.setBackgroundResource(R.drawable.overlay_item_audio_pause)
                preparingProgress.visibility = GONE
                coverOverlay.visibility = VISIBLE
            }
        }
    }

    /**
     * Set up checked state of item. Checked overlay always displays above playing state.
     * @param checked - determine if item checked
     */
    fun setChecked(checked: Boolean) {
        this.checked = checked
        if (checked) {
            coverCheckedOverlay.visibility = VISIBLE
            val color = resources.getColor(R.color.player_list_element_checked_color)
            itemView.setBackgroundColor(color)
        } else {
            coverCheckedOverlay.visibility = GONE
            val color = resources.getColor(R.color.player_list_element_color)
            itemView.setBackgroundColor(color)
        }
    }

    fun isChecked(): Boolean {
        return checked
    }

    fun toggleChecked() {
        checked = !checked
        setChecked(checked)
    }

    fun setSorting(sorting: Boolean) {
        this.sorting = sorting
        if (sorting) {
            coverCheckedOverlay.visibility = GONE
            coverOverlay.setBackgroundResource(R.drawable.overlay_item_audio_sorting)
            coverOverlay.visibility = VISIBLE
        } else {
            setPlayingState(state)
            setChecked(checked)
        }
    }

    fun setCached(cached: Boolean) {
        this.cached = cached
        cachedIcon.visibility = if (cached) VISIBLE else GONE
    }

    /**
     * Playing state of item
     */
    enum class State {
        NONE, PREPARE, PLAY, PAUSE
    }
}