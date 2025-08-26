package com.example.solidconversion.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nimbusds.jwt.SignedJWT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.aesirlab.mylibrary.generateDPoPKey
import org.aesirlab.mylibrary.sharedfunctions.buildTokenRequest
import org.aesirlab.mylibrary.sharedfunctions.createUnsafeOkHttpClient
import org.json.JSONObject
import org.skCompiler.generatedModel.AuthTokenStore
import java.lang.reflect.Modifier

private const val TAG = "AuthCompleteScreen"
@Composable
fun AuthCompleteScreen(
    onFinishedAuth: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context as Activity }

    var didExchange by rememberSaveable { mutableStateOf(false) }

    // Create the token store once
    val tokenStore = remember(context) { AuthTokenStore(context) }

    val deepLinkUri = remember(activity.intent) { activity.intent?.data }
    val code = remember(deepLinkUri) { deepLinkUri?.getQueryParameter("code") }

    LaunchedEffect(code) {
        if (code.isNullOrBlank()) return@LaunchedEffect
        if (didExchange) return@LaunchedEffect

        didExchange = true
        try {
            withContext(Dispatchers.IO) {
                preliminaryAuth(tokenStore, code)
            }
            activity.intent?.data = null
            onFinishedAuth()
        } catch (t: Throwable) {
            didExchange = false
            Toast.makeText(activity, "Sign-in failed: ${t.message}", Toast.LENGTH_LONG).show()
        }
    }
}


private suspend fun preliminaryAuth(tokenStore: AuthTokenStore, code: String?)  {
    val clientId = tokenStore.getClientId().first()
    val rClientSecret = tokenStore.getClientSecret().first()
    val tokenUrl = tokenStore.getTokenUri().first()
    val codeVerifier = tokenStore.getCodeVerifier().first()
    val redirectUri = tokenStore.getRedirectUri().first()

    var clientSecret: String? = null
    if (rClientSecret != "") {
        clientSecret = rClientSecret
    }

    val dpop = generateDPoPKey()

//    val authForm = DPoPAuth(tokenUri = tokenUrl)
//    val authString = authForm.generateAuthString("POST")
    tokenStore.setSigner(dpop.toJSONObject().toString())
//    val authString = generateAuthString("POST", tokenUrl, dpop)
//    tokenStore.setSigner(authString)
//    val response = tokenRequest(
//        clientId,
//        clientSecret,
//        tokenUrl,
//        code!!,
//        codeVerifier,
//        redirectUri,
//        authString
//    )
    val tokenRequest = buildTokenRequest(
        clientId,
        tokenUrl,
        codeVerifier,
        redirectUri,
        dpop,
        clientSecret,
        code!!
    )
    val response = createUnsafeOkHttpClient().newCall(tokenRequest).execute()
//    val responseDict = parseTokenResponseBody(response.body.)
    val json = JSONObject(response.body!!.string())
    val accessToken = json.getString("access_token")

    val idToken: String
    try {
        idToken = json.getString("id_token")

        tokenStore.setIdToken(idToken)

        try {
            val jwtObject = SignedJWT.parse(idToken)
            val body = jwtObject.payload
            val jsonBody = JSONObject(body.toJSONObject())
            val webId = jsonBody.getString("webid")
            Log.d(TAG, webId)
            tokenStore.setWebId(webId)
        } catch (e: Exception) {
            e.message?.let { Log.d("error", it) }
        }
    } catch (e: Exception) {
        e.message?.let { Log.d("error", it) }
    }

    val refreshToken: String
    try {
        refreshToken = json.getString("refresh_token")
        tokenStore.setRefreshToken(refreshToken)
    } catch (e: Exception){
        e.message?.let { Log.d("error", it) }
    }

    tokenStore.setAccessToken(accessToken)
}