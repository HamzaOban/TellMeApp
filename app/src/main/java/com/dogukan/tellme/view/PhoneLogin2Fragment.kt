package com.dogukan.tellme.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.dogukan.tellme.R
import com.dogukan.tellme.databinding.FragmentPhoneLogin2Binding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class PhoneLogin2Fragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : FragmentPhoneLogin2Binding
    private lateinit var cpp : CountryCodePicker
    private var storedVerificationId: String? = ""

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhoneLogin2Binding.inflate(inflater)
        return binding.root
    }
    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]

        val options = this.activity?.let {
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(it)                 // Activity (for callback binding)
                .setCallbacks(callbacks)
                .build()
        }

        options?.let { PhoneAuthProvider.verifyPhoneNumber(it) }
        // [END start_phone_auth]
    }
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        if (!code.isEmpty()){
            val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId.toString(), code)
            signInWithPhoneAuthCredential(credential)
        }

        // [END verify_with_code]
    }
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = activity?.let {
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(it)                 // Activity (for callback binding)
                .setCallbacks(callbacks)
        }          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder?.setForceResendingToken(token) // callback's ForceResendingToken
        }
        optionsBuilder?.let { PhoneAuthProvider.verifyPhoneNumber(it.build()) }
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val action = PhoneLogin2FragmentDirections.actionPhoneLogin2FragmentToPhoneLogin3Fragment()
                    Navigation.findNavController(requireView()).navigate(action)

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid

                    }
                    // Update UI
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cpp = binding.countryCodePicker

        auth = Firebase.auth
        // [END initialize_auth]
        // Initialize phone auth callbacks
        // Turn off phone auth app verification.
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.w(TAG, "a", e)
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.w(TAG, "b", e)
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")
                binding.ll.visibility= View.VISIBLE
                binding.codeContinueButton.visibility = View.VISIBLE
                binding.resendTv.visibility = View.VISIBLE
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        binding.sendCodeButton.setOnClickListener {
            cpp.registerCarrierNumberEditText(binding.editPhoneNumberInput)
            val newcpp = cpp.fullNumberWithPlus.replace(" ","")
            Log.d("Input",newcpp.toString())
            binding.sendCodeButton.isEnabled = false
            startPhoneNumberVerification(newcpp)
        }
        binding.codeContinueButton.setOnClickListener {
            val code = binding.Input1.text.toString().trim() +
                    binding.Input2.text.toString().trim() +
                    binding.Input3.text.toString().trim() +
                    binding.Input4.text.toString().trim() +
                    binding.Input5.text.toString().trim() +
                    binding.Input6.text.toString().trim()
            verifyPhoneNumberWithCode(storedVerificationId,code)
        }
        binding.resendTv.setOnClickListener {
            cpp.registerCarrierNumberEditText(binding.editPhoneNumberInput)
            val newcpp = cpp.fullNumberWithPlus.replace(" ","")
            resendVerificationCode(newcpp,resendToken)
        }
    }
}