package ru.tsu.testws.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.tsu.testws.R
import ru.tsu.testws.databinding.ActivityFirebaseBinding

class FirebaseActivity : AppCompatActivity(R.layout.activity_firebase) {

    private val binding: ActivityFirebaseBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.value as String
                Log.e("Value is: ", value)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error Firebase", "Failed to read value.", error.toException())
            }

        })

        val patientsRef = database.getReference("patients")
        patientsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val firstPatient = snapshot.child("0").getValue(String::class.java)
                Log.e("First patient: ", firstPatient ?: "")

                val values = snapshot.children
                values.forEach {
                    val patient = it.getValue(String::class.java)
                    Log.e("Patient: ", patient ?: "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error Firebase", "Failed to read value.", error.toException())
            }
        })
    }
}