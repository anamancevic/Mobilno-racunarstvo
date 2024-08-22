package com.dam.e_biblioteka

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.dam.e_biblioteka.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LogInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogInFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding:FragmentLogInBinding;
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var sharedprefloggedin=context?.getSharedPreferences("sesija", Context.MODE_PRIVATE)?.getString("ulogovan","ne");
        if(sharedprefloggedin=="da")
        {

            var sharedprefemail=context?.getSharedPreferences("sesija", Context.MODE_PRIVATE)?.getString("email","x");
            var sharedprefpassword = context?.getSharedPreferences("sesija", Context.MODE_PRIVATE)?.getString("password","x");
            LoginUser(sharedprefemail!!,sharedprefpassword!!)
        }
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnLogin.setOnClickListener {
            val email = binding.txtEmail.text.toString();
            val password = binding.txtPassword.text.toString();
            if(email.isNullOrEmpty() || email.isNullOrBlank())
            {
                Toast.makeText(context, R.string.str_obavezanemail, Toast.LENGTH_SHORT).show()
            }
            else if(password.isNullOrBlank() || password.isNullOrEmpty())
            {
                Toast.makeText(context, R.string.str_obaveznalozinka, Toast.LENGTH_SHORT).show()
            }
            else
            {
                LoginUser(email,password)
            }
        }

        binding.btnNoviNalog.setOnClickListener {
           // Toast.makeText(context, "test.", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.action_logInFragment_to_signUpFragment)
        }

    }

    private fun LoginUser(email:String,pass:String)
    {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                context?.getSharedPreferences("sesija", Context.MODE_PRIVATE)?.edit()
                    ?.putString("ulogovan", "da")?.apply();
                context?.getSharedPreferences("sesija", Context.MODE_PRIVATE)?.edit()
                    ?.putString("email", email)?.apply();
                context?.getSharedPreferences("sesija", Context.MODE_PRIVATE)?.edit()
                    ?.putString("password", pass)?.apply();
                navController.navigate(R.id.action_logInFragment_to_dashboardFragment)
            }
            else
                Toast.makeText(context, R.string.str_greskaprijava, Toast.LENGTH_SHORT).show()

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LogInFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LogInFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}