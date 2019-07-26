package com.example.masternoteplus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private ArrayList notes;
    private ArrayAdapter adapter; // links array and listView

    private Button headingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        notes = new ArrayList();
        adapter = new ArrayAdapter<String>(this, R.layout.add_button, R.id.button, notes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                System.out.println("Clicked List Item");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LinearLayout linLayout = new LinearLayout(MainActivity.this);
                linLayout.setOrientation(LinearLayout.VERTICAL);
                builder.setTitle("Remove Note");
                builder.setMessage("Would you like to remove this note?");
                builder.setView(linLayout);

                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("Note Removed");

                        String arrayReference = notes.get(position).toString();
                        String[] keyValuePair = arrayReference.split("\n\n");
                        String key = keyValuePair[0];

                        Log.d("Array Reference: ", arrayReference);
                        System.out.println(keyValuePair[0]);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("notes");

                        notes.remove(position);
                        adapter.notifyDataSetChanged();

                        reference.child(key).removeValue();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("Canceled...");
                    }
                });

                builder.show();
            }
        });

        headingButton = findViewById(R.id.heading);
        headingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "This is the Note Feed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("notes");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                String data = dataSnapshot.getValue(String.class);
                System.out.println("Key: " + key);
                System.out.println("Value: " + data);

                adapter.add(key + "\n\n" + data);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("Following Child Removed, with key: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int getID = item.getItemId();
        System.out.println(getID);
        if (getID == R.id.plusButton) {
            System.out.println("Equals plus button!");
            clickedMethod();
        } else if (getID == R.id.minusButton) {
            System.out.println("Equals minus button!");
            removeAllNotes();
        } else {
            System.out.println("Can not find ID of 'plusButton'");
        }
        return super.onOptionsItemSelected(item);
    }

    private void clickedMethod() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle("Create New Message");
        builder.setView(linLayout);

        final EditText textField1 = new EditText(this);
        final EditText textField2 = new EditText(this);

        linLayout.addView(textField1);
        linLayout.addView(textField2);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("OK");
                String text1 = textField1.getText().toString();
                String text2 = textField2.getText().toString();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("notes");

//                myRef.setValue(text1 + " " + text2);
                myRef.child(text1).setValue(text2);


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("Canceled...");
            }
        });

        builder.show();

    }

    private void removeAllNotes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle("Remove All Notes");
        builder.setMessage("Are you sure you want to remove all notes?");
        builder.setView(linearLayout);

        builder.setPositiveButton("Remove All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               FirebaseDatabase.getInstance().getReference("notes").removeValue();
               adapter.clear();
               adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

}
