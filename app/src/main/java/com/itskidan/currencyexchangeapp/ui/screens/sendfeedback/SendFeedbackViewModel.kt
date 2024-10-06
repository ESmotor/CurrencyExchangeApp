package com.itskidan.currencyexchangeapp.ui.screens.sendfeedback

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject

class SendFeedbackViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    private var interstitialAd: InterstitialAd? = null

    private val firestoreDB = Firebase.firestore

    private val _btnText = MutableStateFlow("Loading Interstitial Ad...")
    val btnText: MutableStateFlow<String> get() = _btnText

    private val _btnEnable = MutableStateFlow(false)
    val btnEnable: MutableStateFlow<Boolean> get() = _btnEnable

    init {
        App.instance.dagger.inject(this)
    }

    fun loadInterstitialAd(context: Context) {
        InterstitialAd.load(context,
            "ca-app-pub-3940256099942544/1033173712",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    interstitialAd = null
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    interstitialAd = p0
                    _btnText.value = "Show Interstitial Ad"
                    _btnEnable.value = true
                }
            })
    }

    fun showInterstitialAd(context: Context, onAdDismissed: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd!!.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        interstitialAd = null
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        interstitialAd = null

                        loadInterstitialAd(context)
                        onAdDismissed()

                        btnText.value = "Loading Interstitial Ad..."
                        btnEnable.value = false
                    }
                }
            interstitialAd!!.show(context as Activity)
        }
    }

    fun generateDocumentId(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        val currentDate = dateFormat.format(Date())
        val randomPart = UUID.randomUUID().toString().substring(0, 8)
        return "${currentDate}_$randomPart"
    }

    fun sendUserFeedback(type:String, messageText:String){
        val feedback = hashMapOf(type to messageText)
        val documentId = generateDocumentId()
        val documentName = when(type){
            "problem" -> "problems"
            "idea" -> "ideas"
            else-> "unknown_document"
        }

        firestoreDB.collection("user_feedback")
            .document(documentName)
            .collection(documentName)
            .document(documentId)
            .set(feedback)
            .addOnSuccessListener {
                Timber.tag("MyLog")
                    .d("$type added with ID: $documentId")
            }
            .addOnFailureListener { e ->
                Timber.tag("MyLog").d("Error adding $type: $e")
            }
    }

}