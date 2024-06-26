package com.example.wannwardat

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.example.wannwardat.SharedData

class MainActivity : AppCompatActivity() {
    private val clientId = "cb37b750c12c44e49fd03a9dc9b11bf2"
    private val redirectUri = "http://localhost:8080"

    private var REQUEST_CODE = 1337
    private var REDIRECT_URI = "http://localhost:8080"

    private lateinit var builder: AuthorizationRequest.Builder



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        builder = AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.TOKEN, redirectUri)

        builder.setScopes(arrayOf("app-remote-control"))
        var request = builder.build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE)
        {
            var response = AuthorizationClient.getResponse(resultCode, data)
            Log.d("Login", response.toString())
            if (response.type == AuthorizationResponse.Type.TOKEN)
            {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // We will start writing our code here.
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                SharedData.spotifyAppRemote= appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })

    }

    private fun connected() {
        // Then we will write some more code here.
        val intent = Intent(this, ScanActivity::class.java)

        startActivity(intent)



    }

    override fun onStop() {
        super.onStop()
        // Aaand we will finish off here.



    }

    override fun onDestroy() {
        super.onDestroy()

    }
}