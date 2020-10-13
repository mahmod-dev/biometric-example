/*
 * created by mahmoud-dev
 */

package com.mahmouddev.biometric_example

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat


class BiometricActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    private lateinit var biometricPrompt: BiometricPrompt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric)

        //check Device has Biometric Support
        val biometricManager = BiometricManager.from(this)
        val isSupportBiometric = when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> true
            else -> false
        }
        if (isSupportBiometric) {
            biometricPrompt = createBiometricPrompt()

            val packageManager: PackageManager = packageManager

            // check device has fingerPrint
            val hasFingerprint = packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)

            // check device has face biometric
            val hasFace = packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)

            if (hasFingerprint)
                Log.e(TAG, "has Fingerprint: true ")

            if (hasFace)
                Log.e(TAG, "has Face recognition: true ")

            biometricPrompt.authenticate(createPromptInfo())

        }


    }


    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.e(TAG, "$errorCode :: $errString")

                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Log.e(TAG, "onAuthenticationError: use password instead of biometric")
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.e(TAG, "Authentication failed for an unknown reason")
                toast("failed")

            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                toast("success login!")
                startActivity(Intent(this@BiometricActivity, MainActivity::class.java))


            }
        }

        return BiometricPrompt(this, executor, callback)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("set title")
            .setSubtitle("set subtitle")
            .setDescription("set description")
            // Authenticate without requiring the user to press a "confirm"
            // button after satisfying the biometric check
            .setConfirmationRequired(true)
            .setNegativeButtonText("navigate button")
            .build()
    }

    private fun toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }


}