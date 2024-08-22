package com.dam.e_biblioteka

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import com.dam.e_biblioteka.database.DatabaseManager
import com.dam.e_biblioteka.databinding.FragmentBookListBinding
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BookListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding:FragmentBookListBinding
    private lateinit var navController:NavController
    private val args : BookListFragmentArgs by navArgs();
    private var kriterijum_autor=""
    private var kriterijum_naslov=""
    private var kriterijum_godinaizd=""
    private var kriterijum_isbn=""
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
        binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.spinner.setSelection(binding.spinner.adapter.count - 1)
        if (args.ListType == "SVE") {
            PrikaziSve()
        } else if (args.ListType == "IZNAJMLJENE") {
            PrikaziIznajmljene()
        }
        binding.txtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // This method is called when the user submits the query
                query?.let {
                    // Do something with the query
                    //Toast.makeText(requireContext(), "Search: $it", Toast.LENGTH_SHORT).show()
                    if (binding.spinner.selectedItemPosition == Kriterijum.NASLOV.toInt()) {
                        kriterijum_naslov = it
                    } else if (binding.spinner.selectedItemPosition == Kriterijum.AUTOR.toInt()) {
                        kriterijum_autor = it
                    } else if (binding.spinner.selectedItemPosition == Kriterijum.GODINA_IZDAVANJA.toInt()) {
                        kriterijum_godinaizd=it

                    }
                    else if(binding.spinner.selectedItemPosition==Kriterijum.ISBN.toInt())
                    {
                        kriterijum_isbn=it
                    }

                    if(binding.spinner.selectedItemPosition!=Kriterijum.SVE.toInt()) {
                        if (args.ListType == "SVE") {
                            PretragaSvih()
                        } else if (args.ListType == "IZNAJMLJENE") {
                            PretragaIznajmljenih()
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (binding.spinner.selectedItemPosition == Kriterijum.NASLOV.toInt()) {
                    binding.txtSearch.setQuery(kriterijum_naslov,false)
                } else if (binding.spinner.selectedItemPosition == Kriterijum.AUTOR.toInt()) {
                    binding.txtSearch.setQuery(kriterijum_autor,false)
                } else if (binding.spinner.selectedItemPosition == Kriterijum.GODINA_IZDAVANJA.toInt()) {
                    binding.txtSearch.setQuery(kriterijum_godinaizd,false)

                }
                else if(binding.spinner.selectedItemPosition==Kriterijum.ISBN.toInt())
                {
                    binding.txtSearch.setQuery(kriterijum_isbn,false)
                }
                else if(binding.spinner.selectedItemPosition==Kriterijum.SVE.toInt())
                {
                    binding.txtSearch.isIconified=true
                    kriterijum_isbn=""
                    kriterijum_autor=""
                    kriterijum_naslov=""
                    kriterijum_godinaizd=""
                    if (args.ListType == "SVE") {
                        PrikaziSve()
                    } else if (args.ListType == "IZNAJMLJENE") {
                        PrikaziIznajmljene()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where nothing is selected if needed
            }
        }
    }

    private fun PrikaziSve()
    {
        binding.tvInfo.visibility=View.INVISIBLE
        val baza = DatabaseManager()
        var recyclerView = binding.rvKnjige;
        recyclerView.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.Main)
        {
            var rez = baza.getKnjige()
            var data : ArrayList<ItemsViewModel> = ArrayList();
            if (rez != null) {
                if(rez.count()==0)
                {
                    binding.tvInfo.text=getString(R.string.str_nemaknjiga)
                    binding.tvInfo.visibility=View.VISIBLE
                }
                else {
                    for (k in rez) {
                        data.add(
                            ItemsViewModel(
                                R.drawable.blank,
                                k.Naslov + " - " + k.Autor,
                                k.ID.toString()
                            )
                        )
                    }
                }
            }
            else
            {
                binding.tvInfo.text=getString(R.string.str_nemaknjiga)
                binding.tvInfo.visibility=View.VISIBLE
            }
            SetAdapter(data);
        }
    }

    private fun PrikaziIznajmljene()
    {
        binding.tvInfo.visibility=View.INVISIBLE
        val baza = DatabaseManager()
        var recyclerView = binding.rvKnjige;
        recyclerView.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.Main)
        {
            var rez = baza.getIznajmljivanja(baza.getUID())
            var data : ArrayList<ItemsViewModel> = ArrayList();

            if (rez != null) {
                if(rez.count()==0)
                {
                    binding.tvInfo.text=getString(R.string.str_nemaiznajmljenih)
                    binding.tvInfo.visibility=View.VISIBLE
                }
                else {
                    for (iz in rez) {
                        data.add(
                            ItemsViewModel(
                                R.drawable.blank,
                                iz.Knjiga?.Naslov + " - " + iz.Knjiga?.Autor,
                                iz.ID.toString()
                            )
                        )
                    }
                }
            }
            else
            {
                binding.tvInfo.text=getString(R.string.str_nemaiznajmljenih)
                binding.tvInfo.visibility=View.VISIBLE
            }
            SetAdapter(data);
        }
    }

    private fun PretragaSvih()
    {
        binding.tvInfo.visibility=View.INVISIBLE
        var recyclerView = binding.rvKnjige;
        recyclerView.layoutManager = LinearLayoutManager(context)
        val baza = DatabaseManager()
        lifecycleScope.launch(Dispatchers.Main)
        {
            var rez = baza.getKnjige()
            var data : ArrayList<ItemsViewModel> = ArrayList();
            if (rez != null) {
                for(k in rez) {
                    var dodaj = false
                    if (kriterijum_autor != "") {
                        if (k.Autor.contains(kriterijum_autor)) {
                            dodaj = true
                        }
                    }

                    if (kriterijum_naslov != "") {
                        if (k.Naslov.contains(kriterijum_naslov)) {
                            dodaj = true
                        }
                    }

                    if (kriterijum_godinaizd != "") {
                        if (k.GodinaIzdavanja.toString() == kriterijum_godinaizd) {
                            dodaj = true
                        }
                    }
                    if (kriterijum_isbn != "")
                    {
                        if(k.ISBN.toString()==kriterijum_isbn)
                        {
                            dodaj=true
                        }
                    }
                    if(dodaj) {
                        data.add(
                            ItemsViewModel(
                                R.drawable.blank,
                                k.Naslov+ " - " + k.Autor,
                                k.ID.toString()
                            )
                        )
                    }
                }
            }

            if(data.count()==0)
            {
                binding.tvInfo.text=getString(R.string.str_nemapodudaranja)
                binding.tvInfo.visibility=View.VISIBLE
            }
            SetAdapter(data);
        }
    }

    private fun PretragaIznajmljenih()
    {
        binding.tvInfo.visibility=View.INVISIBLE
        val baza = DatabaseManager()
        lifecycleScope.launch(Dispatchers.Main)
        {
            var recyclerView = binding.rvKnjige;
            recyclerView.layoutManager = LinearLayoutManager(context)
            var rez = baza.getKnjige()
            var data : ArrayList<ItemsViewModel> = ArrayList();
            if (rez != null) {
                for(k in rez) {
                    var dodaj = false
                    if(kriterijum_autor!="")
                    {
                        if(k.Autor.contains(kriterijum_autor))
                        {
                            dodaj=true
                        }
                    }

                    if(kriterijum_naslov!="")
                    {
                        if(k.Naslov.contains(kriterijum_naslov))
                        {
                            dodaj=true
                        }
                    }
                    if (kriterijum_godinaizd != "") {
                        if (k.GodinaIzdavanja.toString() == kriterijum_godinaizd) {
                            dodaj = true
                        }
                    }
                    if (kriterijum_isbn != "")
                    {
                        if(k.ISBN.toString()==kriterijum_isbn)
                        {
                            dodaj=true
                        }
                    }
                    if(dodaj) {
                        data.add(
                            ItemsViewModel(
                                R.drawable.blank,
                                k.Naslov + " - " + k.Autor,
                                k.ID.toString()
                            )
                        )
                    }
                }
            }
            if(data.count()==0)
            {
                binding.tvInfo.text=getString(R.string.str_nemapodudaranja)
                binding.tvInfo.visibility=View.VISIBLE
            }
            SetAdapter(data);
        }
    }

    private fun SetAdapter(data:ArrayList<ItemsViewModel>)
    {
        var recyclerView = binding.rvKnjige;
        var adapter = CustomAdapter(data)
        adapter.setOnLongClickListener(object:CustomAdapter.OnLongClickListener{
            override fun onLongClick(position: Int, model: ItemsViewModel) : Boolean
            {
                return true;
            }
        })
        adapter.setOnClickListener(object:CustomAdapter.OnClickListener{
            override fun onClick(position: Int, model: ItemsViewModel) {
                if(args.ListType=="SVE")
                {
                    val dir = BookListFragmentDirections.actionBookListFragmentToRentBookFragment(model.payload.toInt(),"IZNAJMI")
                    navController.navigate(dir)
                }
                else if(args.ListType=="IZNAJMLJENE")
                {
                    val dir = BookListFragmentDirections.actionBookListFragmentToRentBookFragment(-1,"VRATI",model.payload.toInt())
                    navController.navigate(dir)
                }
            }
        })
        recyclerView.adapter=adapter;
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}