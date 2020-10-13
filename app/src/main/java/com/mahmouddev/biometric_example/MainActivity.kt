package com.mahmouddev.biometric_example

import android.content.pm.PackageManager
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class MainActivity : AppCompatActivity() {
    private lateinit var biometricPrompt: BiometricPrompt
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check Device has Biometric Support
        val biometricManager = BiometricManager.from(this)
        val isSupportBiometric = when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> true
            else -> false
        }
        biometricPrompt = createBiometricPrompt()


        val packageManager: PackageManager = packageManager

        // check device has fingerPrint
        val hasFingerprint = packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)

        // check device has face biometric
        val hasFace = packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)

        if (hasFingerprint)
            Log.e(TAG, "hasFingerprint: true ")

        if (!hasFace)
            Log.e(TAG, "hasFace: false ")
        //   val cryptoObject: BiometricPrompt.CryptoObject = BiometricPrompt.CryptoObject(getEncryptCipher(createKey()))
        biometricPrompt.authenticate(createPromptInfo())

    }


    private fun toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.e(TAG, "$errorCode :: $errString")

                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Log.e(TAG, "onAuthenticationError: use password instead of biometric")
                    //    loginWithPassword() // Because in this app, the negative button allows the user to enter an account password. This is completely optional and your app doesn’t have to do it.
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.e(TAG, "Authentication failed for an unknown reason")
                toast("failed")

            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                toast("success")

            }
        }

        return BiometricPrompt(this, executor, callback)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("biometricName")
            .setSubtitle("prompt_info_subtitle")
            .setDescription("prompt_info_description")
            // Authenticate without requiring the user to press a "confirm"
            // button after satisfying the biometric check
            .setConfirmationRequired(true)
            .setNegativeButtonText("prompt_info_use_app_password")
            .build()
        return promptInfo
    }
    

}