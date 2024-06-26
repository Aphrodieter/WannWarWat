package com.example.wannwardat
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class ScanActivity: AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var qrScannerLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        qrScannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            if (intentResult != null){
                if(intentResult.contents == null){

                } else {
                    val scannedData = intentResult.contents
                    Log.d("scannedData", scannedData)
                    startPlayingSong(scannedData)
                }
            }
        }


        //
    }

    override fun onStart()
    {
        super.onStart()
        if (checkPermissions())
        {
            startQrScanner()
        } else {
            requestPermission()
        }
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
        SharedData.spotifyAppRemote!!.playerApi.pause()
        SharedData.spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("Permission", "PermissionRequestCallback")
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startQrScanner()
            } else {
                //Permission denied
            }
        }
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setPrompt("Scan a QR Code")
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(false)
        }
        qrScannerLauncher.launch(integrator.createScanIntent())

    }

    private fun startPlayingSong(playlistURI: String)
    {
        SharedData.spotifyAppRemote?.let {
            // Play a playlist
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }
    }
}