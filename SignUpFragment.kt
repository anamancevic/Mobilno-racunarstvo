package com.dam.e_biblioteka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.dam.e_biblioteka.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
       binding.btnSignup.setOnClickListener {
           val email = binding.txtEmail.text.toString()
           val pass = binding.txtPassword.text.toString()
           val repass = binding.txtRePass.text.toString()
           if(email.isNullOrEmpty() || email.isNullOrBlank())
           {
               Toast.makeText(context, R.string.str_obavezanemail, Toast.LENGTH_SHORT).show()
           }
           else if(pass.isNullOrBlank() || pass.isNullOrEmpty())
           {
               Toast.makeText(context, R.string.str_obaveznalozinka, Toast.LENGTH_SHORT).show()
           }
           else if(pass!=repass)
           {
               Toast.makeText(context, R.string.str_lozinkaseneslaze, Toast.LENGTH_SHORT).show()
           }
           else
           {
              MakeUser(email,pass)
           }
       }
    }

    private fun MakeUser(email:String,password:String)
    {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if (it.isSuccessful)
                navController.navigate(R.id.action_signUpFragment_to_logInFragment)
            else
                Toast.makeText(context, R.string.str_greskakreiranje, Toast.LENGTH_SHORT).show()

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}