package com.dam.e_biblioteka

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.dam.e_biblioteka.database.DatabaseManager
import com.dam.e_biblioteka.database.Iznajmljivanje
import com.dam.e_biblioteka.databinding.FragmentRentBookBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RentBookFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RentBookFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentRentBookBinding
    private lateinit var  navController: NavController
    private val args:RentBookFragmentArgs by navArgs()

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
        binding = FragmentRentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)

            //your codes here
        }
        if(args.Razlog=="IZNAJMI")
        {
            val db = DatabaseManager()
            lifecycleScope.launch(Dispatchers.Main){
                val k = db.getKnjigaByID(args.BookID)!!
                binding.tvNaslov.text=k.Naslov
                binding.tvAutor.text=k.Autor
                binding.tvISBN.text=k.ISBN.toString()
                binding.tvGodinaIzdavanja.text=k.GodinaIzdavanja.toString()
                try {
                    val i = binding.imageView2
                    val bitmap = BitmapFactory.decodeStream(URL(k.Slika).content as InputStream)
                    i.setImageBitmap(bitmap)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                binding.textView8.visibility=View.INVISIBLE
                binding.textView9.visibility=View.INVISIBLE
                binding.btIznajmi.visibility=View.VISIBLE
                binding.btVrati.visibility=View.INVISIBLE
                binding.tvDatumDo.visibility=View.INVISIBLE
                binding.tvDatumOd.visibility=View.INVISIBLE

                binding.btIznajmi.setOnClickListener{
                    lifecycleScope.launch(Dispatchers.Main) {
                        var izn:Iznajmljivanje = Iznajmljivanje()
                        izn.Knjiga=k
                        izn.DatumOd= Date()
                        izn.DatumDo=Date()
                        izn.DatumDo.time=izn.DatumDo.getTime() + 20*24*60*60*1000
                        db.InsertIznajmljivanje(izn)
                        Toast.makeText(context, getString(R.string.str_knjigaiznajmljena), Toast.LENGTH_SHORT).show()
                        //navController.navigate(R.id.action_rentBookFragment_to_bookListFragment)
                    }

                }
            }
        }
        else if(args.Razlog=="VRATI")
        {
            val db = DatabaseManager()
            lifecycleScope.launch(Dispatchers.Main) {
                val iz = db.getIznajmljivanjeByID(args.IznajmljivanjeID)!!
                val k = iz.Knjiga!!;
                binding.tvNaslov.text = k.Naslov
                binding.tvAutor.text = k.Autor
                binding.tvISBN.text = k.ISBN.toString()
                binding.tvGodinaIzdavanja.text = k.GodinaIzdavanja.toString()
                try {
                    val i = binding.imageView2
                    val bitmap = BitmapFactory.decodeStream(URL(k.Slika).content as InputStream)
                    i.setImageBitmap(bitmap)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val dateformat = SimpleDateFormat("dd.MM.yyyy")
                binding.tvDatumOd.text=dateformat.format(iz.DatumOd)
                binding.tvDatumDo.text=dateformat.format(iz.DatumDo)
                binding.textView8.visibility = View.VISIBLE
                binding.textView9.visibility = View.VISIBLE
                binding.btIznajmi.visibility = View.INVISIBLE
                binding.btVrati.visibility = View.VISIBLE
                binding.tvDatumDo.visibility = View.VISIBLE
                binding.tvDatumOd.visibility = View.VISIBLE
                binding.btVrati.setOnClickListener{
                    lifecycleScope.launch(Dispatchers.Main) {
                        db.deleteIznajmljivanje(args.IznajmljivanjeID.toString())
                        Toast.makeText(context, getString(R.string.str_knjigavracena), Toast.LENGTH_SHORT).show()
                        //navController.navigate(R.id.action_rentBookFragment_to_bookListFragment)
                    }

                }
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RentBookFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RentBookFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}