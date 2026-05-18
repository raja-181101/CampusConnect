package com.example.try1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.try1.models.SubjectModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class admin extends AppCompatActivity {
    String[] items = {"ECE", "CSE", "IT", "EEE", "CIV", "MECH"};
    String[] sec = {"A", "B", "C", "D"};
    String[] year = {"1", "2", "3", "4"};
    AutoCompleteTextView autoCompleteTextView, section, years;
    ArrayAdapter<String> arrayAdapter, list1, list2;
    Button save;
    TextInputLayout subname, subkey;
    String bran, sect, yer;
    FirebaseDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        autoCompleteTextView = findViewById(R.id.auto_complete_type);
        section = findViewById(R.id.section);
        years = findViewById(R.id.year);
        save = findViewById(R.id.savebtn);
        subname = findViewById(R.id.subjectName);
        subkey = findViewById(R.id.subjectKey);
        database = FirebaseDatabase.getInstance();

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_items, items);
        autoCompleteTextView.setAdapter(arrayAdapter);

        list1 = new ArrayAdapter<String>(this, R.layout.list_items, sec);
        list2 = new ArrayAdapter<String>(this, R.layout.list_items, year);
        section.setAdapter(list1);
        years.setAdapter(list2);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bran = "";
                String item = parent.getItemAtPosition(position).toString();
                bran = item;
                Toast.makeText(getApplicationContext(), "branch: " + item, Toast.LENGTH_SHORT);

            }
        });
        section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sect = "";
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "branch: " + item, Toast.LENGTH_SHORT);
                sect = item;
            }
        });

        years.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                yer = "";
                String item = parent.getItemAtPosition(position).toString();
                yer = item;
                Toast.makeText(getApplicationContext(), "branch: " + item, Toast.LENGTH_SHORT);

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.getReference()
                        .child("Subjects")
                        .child(subname.getEditText().getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    SubjectModel model1 = new SubjectModel();
                                    model1.setSec(sect);
                                    model1.setSubjectkey(subkey.getEditText().getText().toString());
                                    database.getReference()
                                            .child("Subjects")
                                            .child(subname.getEditText().getText().toString())
                                            .child("Sections")
                                            .child(sect)
                                            .setValue(model1);
                                }else {
                                    SubjectModel model = new SubjectModel();
                                    model.setSubjectName(subname.getEditText().getText().toString());
                                   // model.setSubjectkey(subkey.getEditText().getText().toString());
                                    model.setBranch(bran);
                                    model.setSec(sect);
                                    model.setYear(yer);

                                    database.getReference()
                                            .child("Subjects")
                                            .child(subname.getEditText().getText().toString())
                                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                   SubjectModel model = new SubjectModel();
                                                   model.setSec(sect);
                                                   model.setSubjectkey(subkey.getEditText().getText().toString());
                                                    database.getReference().child("Subjects")
                                                                    .child(subname.getEditText().getText().toString())
                                                                            .child("Sections")
                                                                                    .child(sect)
                                                                                            .setValue(model);


                                                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });





            }
        });


    }
}