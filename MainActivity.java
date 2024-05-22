package com.example.eventmanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView eventListView;
    private Button addEventButton;
    private ArrayList<String> eventList;
    private ArrayAdapter<String> eventAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventListView = findViewById(R.id.eventListView);
        addEventButton = findViewById(R.id.addEventButton);

        dbHelper = new DBHelper(this);
        eventList = new ArrayList<>();
        eventAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventList);
        eventListView.setAdapter(eventAdapter);

        loadEvents();

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEventDialog(null);
            }
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String eventTitle = eventList.get(position);
                openEventDialog(eventTitle);
            }
        });

        eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String eventTitle = eventList.get(position);
                confirmDeleteEvent(eventTitle);
                return true;
            }
        });
    }

    private void loadEvents() {
        eventList.clear();
        Cursor cursor = dbHelper.getAllEvents();
        if (cursor.moveToFirst()) {
            do {
                eventList.add(cursor.getString(1)); // Assuming 1 is the index for the event title
            } while (cursor.moveToNext());
        }
        cursor.close();
        eventAdapter.notifyDataSetChanged();
    }

    private void openEventDialog(String eventTitle) {
        Intent intent = new Intent(this, EventDialogActivity.class);
        if (eventTitle != null) {
            intent.putExtra("EVENT_TITLE", eventTitle);
        }
        startActivity(intent);
    }

    private void confirmDeleteEvent(final String eventTitle) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteEvent(eventTitle);
                        loadEvents();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }
}
