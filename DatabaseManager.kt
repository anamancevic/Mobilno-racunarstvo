package com.dam.e_biblioteka.database

import android.annotation.SuppressLint
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DatabaseManager {
    val DBinst = FirebaseDatabase.getInstance("https://e-biblioteka-df9a4-default-rtdb.europe-west1.firebasedatabase.app")


    suspend fun getKnjige():MutableList<Knjiga>? {
        try {
            val databaseReference: DatabaseReference = DBinst.getReference("Biblioteka/Knjige")
            val snapshot: DataSnapshot = databaseReference.get().await()
            val ret = mutableListOf<Knjiga>()
            for (knjigeSnapshot in snapshot.children) {
                var k:Knjiga = Knjiga()
                k.ID= knjigeSnapshot.key!!.toInt()
                k.Naslov = knjigeSnapshot.child("Naslov").getValue(String::class.java)!!
                k.Autor = knjigeSnapshot.child("Autor").getValue(String::class.java)!!
                k.Slika=knjigeSnapshot.child("Slika").getValue(String::class.java)!!
                k.GodinaIzdavanja=knjigeSnapshot.child("GodinaIzdavanja").getValue(Int::class.java)!!
                k.ISBN = knjigeSnapshot.child("ISBN").getValue(Int::class.java)!!
                ret.add(k);
            }

            return ret
        } catch (e: DatabaseException) {
            println(e.message)
        }
        return null
    }

    suspend fun getKnjigaByID(id:Int):Knjiga? {
        try {
            val databaseReference: DatabaseReference =
                DBinst.getReference("Biblioteka/Knjige/" + id)
            val snapshot: DataSnapshot = databaseReference.get().await()
            if (snapshot.exists()) {
                var k: Knjiga = Knjiga()
                k.ID = snapshot.key!!.toInt()
                k.Naslov = snapshot.child("Naslov").getValue(String::class.java)!!
                k.Autor = snapshot.child("Autor").getValue(String::class.java)!!
                k.Slika = snapshot.child("Slika").getValue(String::class.java)!!
                k.GodinaIzdavanja=snapshot.child("GodinaIzdavanja").getValue(Int::class.java)!!
                k.ISBN = snapshot.child("ISBN").getValue(Int::class.java)!!
                return k
            }

        } catch (e: DatabaseException) {
            println(e.message)
        }
        return null
    }

    suspend fun getIznajmljivanja(uid: String?):MutableList<Iznajmljivanje>? {
        try {
            val databaseReference: DatabaseReference =
                DBinst.getReference("Biblioteka/Iznajmljivanja")
            val query = databaseReference.orderByChild("Korisnik").equalTo(uid.toString());
            val snapshot: DataSnapshot = query.get().await()
            val ret = mutableListOf<Iznajmljivanje>()
            for (iznSnapshot in snapshot.children) {
                val izn:Iznajmljivanje = Iznajmljivanje()
                izn.ID=iznSnapshot.key?.toInt()!!
                izn.Knjiga = getKnjigaByID(iznSnapshot.child("Knjiga").getValue(Int::class.java)!!)
                val dateformat = SimpleDateFormat("yyyy-MM-dd")
                var tempdatum = iznSnapshot.child("DatumOd").getValue(String::class.java);
                izn.DatumOd = dateformat.parse(tempdatum);
                tempdatum = iznSnapshot.child("DatumDo").getValue(String::class.java);
                izn.DatumDo= dateformat.parse(tempdatum);
                izn.KorisnikID=iznSnapshot.child("Korisnik").getValue(String::class.java)!!
                ret.add(izn);
            }

            return ret
        } catch (e: DatabaseException) {
            println(e.message)
        }
        return null
    }

    suspend fun getIznajmljivanjeByID(id: Int):Iznajmljivanje? {
        try {
            val databaseReference: DatabaseReference =
                DBinst.getReference("Biblioteka/Iznajmljivanja/"+id)
            val snapshot: DataSnapshot = databaseReference.get().await()

            if(snapshot.exists())
            {
                val izn:Iznajmljivanje = Iznajmljivanje()
                izn.ID=snapshot.key?.toInt()!!
                izn.Knjiga = getKnjigaByID(snapshot.child("Knjiga").getValue(Int::class.java)!!)
                val dateformat = SimpleDateFormat("yyyy-MM-dd")
                var tempdatum = snapshot.child("DatumOd").getValue(String::class.java);
                izn.DatumOd = dateformat.parse(tempdatum);
                tempdatum = snapshot.child("DatumDo").getValue(String::class.java);
                izn.DatumDo= dateformat.parse(tempdatum);
                izn.KorisnikID=snapshot.child("Korisnik").getValue(String::class.java)!!
                return izn
            }

        } catch (e: DatabaseException) {
            println(e.message)
        }
        return null
    }

    suspend fun getLastIznajmljivanjeID():String
    {
        val databaseReference: DatabaseReference =
            DBinst.getReference("Biblioteka/Iznajmljivanja")
        val snapshot: DataSnapshot = databaseReference.orderByKey().limitToLast(1).get().await()
        if(snapshot.hasChildren()) {
            return snapshot.children.first().key!!;
        }
        else
        {
            return "0";
        }
    }

    suspend fun InsertIznajmljivanje(izn:Iznajmljivanje)
    {
        try {
            val ref = DBinst.getReference().child("Biblioteka/Iznajmljivanja")
            val dateformat = SimpleDateFormat("yyyy-MM-dd")
            val objMap: Map<String, Any?> = mapOf(
                "Knjiga" to izn.Knjiga?.ID,
                "Korisnik" to getUID(),
                "DatumOd" to dateformat.format(izn.DatumOd),
                "DatumDo" to dateformat.format(izn.DatumDo)

            )

            val newid = getLastIznajmljivanjeID().toInt() + 1;
            val insertMap: Map<String, Any?> = mapOf(
                newid.toString() to objMap
            )
            ref.child(newid.toString()).setValue(objMap);
        }
        catch(e:DatabaseException)
        {
            println(e.message)
        }
    }

    suspend fun deleteIznajmljivanje(id:String)
    {
        try {
            val ref = DBinst.getReference().child("Biblioteka/Iznajmljivanja/" + id)
            ref.removeValue().await()
        }
        catch (e: DatabaseException) {
            println(e.message)
        }
    }



    fun getUID() : String?
    {
        return Firebase.auth.uid;
    }

    @SuppressLint("RestrictedApi")
    private suspend fun Task<DataSnapshot>.await(): DataSnapshot {
        return suspendCoroutine { continuation ->
            addOnCompleteListener {
                if (isSuccessful) {
                    continuation.resume(result!!)
                } else {
                    continuation.resumeWithException(exception ?: DatabaseException("Unknown error"))
                }
            }
        }
    }

}