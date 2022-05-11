package ru.tsu.testws

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import ru.tsu.testws.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity(R.layout.activity_media) {

    private val player by lazy { ExoPlayer.Builder(this).build() }
    private lateinit var audioPlayer: MediaPlayer

    private val selectVideoFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            setVideo(uri)
        }
    }

    private val selectAudioFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            setAudio(uri)
        }
    }

    private val requestMediaPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if(it.all { it.value }) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Что-то пошло не так")
                .setPositiveButton("Ясно") {_,_ ->}
                .create()
                .show()
        }
    }

    private val mediaPermissions = listOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val viewBinding: ActivityMediaBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.videoPlayer.player = player
        viewBinding.selectVideoView.setOnClickListener {
            if (mediaPermissions.all { checkPermissions(it) }) {
                selectVideoFromGalleryResult.launch("video/*")
            } else {
                requestMediaPermissions.launch(mediaPermissions.toTypedArray())
            }
        }
        viewBinding.selectAudio.setOnClickListener {
            if (mediaPermissions.all { checkPermissions(it) }) {
                selectAudioFromGalleryResult.launch("audio/*")
            } else {
                requestMediaPermissions.launch(mediaPermissions.toTypedArray())
            }
        }

        viewBinding.audioPlayButton.setOnClickListener {
            if(audioPlayer.isPlaying) {
                audioPlayer.pause()
            } else {
                audioPlayer.start()
            }
        }
    }

    private fun setVideo(uri: Uri) {
        player.clearMediaItems()
        player.addMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
    }

    private fun setAudio(uri: Uri) {
        audioPlayer = MediaPlayer.create(this, uri)
        audioPlayer.start()
    }

    private fun checkPermissions(permission: String) =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
}