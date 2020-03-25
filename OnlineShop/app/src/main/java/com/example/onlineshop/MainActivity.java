package com.example.onlineshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private static ExpandableListView expandableListView;
    private static ExpandableListAdapter adapter;
    private String lastSelectedObject;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            lastSelectedObject = savedInstanceState.getString("message");
            System.out.println(lastSelectedObject);
        }
        System.out.println("Called onCreate");
        setContentView(R.layout.activity_main);

        expandableListView = findViewById(R.id.simple_expandable_listview);

        // Setting group indicator null for custom indicator
        expandableListView.setGroupIndicator(null);
        setItems();
        setListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("message", lastSelectedObject);
        System.out.println("Called onSaveInstaceState");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        System.out.println("Called onPostCreate");
        System.out.println(lastSelectedObject);
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy()
    {
        System.out.println("Called onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        System.out.println("Called onResume");
        System.out.println(lastSelectedObject);
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        System.out.println("Called onPause");
        System.out.println(lastSelectedObject);
        super.onPause();
    }


    // Setting headers and childs to expandable listview
    void setItems() {

        // Array list for header
        ArrayList<String> header = new ArrayList<>();

        // Array list for child items
        List<String> child1 = new ArrayList<>();
        List<String> child2 = new ArrayList<>();
        List<String> child3 = new ArrayList<>();
        List<String> child4 = new ArrayList<>();

        // Hash map for both header and child
        HashMap<String, List<String>> hashMap = new HashMap<>();

        // Adding headers to list
        for (int i = 1; i < 5; i++)
        {
            header.add("Obiect " + i);
        }
        // Adding child data
       child1.add("Detaliu 1");

        // Adding child data
       child2.add("Detaliu 2");

        // Adding child data
       child3.add("Detaliu 3");

        // Adding child data
        child4.add("Detaliu 4");

        // Adding header and childs to hash map
        hashMap.put(header.get(0), child1);
        hashMap.put(header.get(1), child2);
        hashMap.put(header.get(2), child3);
        hashMap.put(header.get(3), child4);

        adapter = new ExpandableListAdapter(MainActivity.this, header, hashMap);

        // Setting adpater over expandablelistview
        expandableListView.setAdapter(adapter);
    }

    // Setting different listeners to expandablelistview
    void setListener()
    {
        // This listener will show toast on group click
        expandableListView.setOnGroupClickListener(new OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView listview, View view, int group_pos, long id)
            {
                Toast.makeText(MainActivity.this, "You clicked : " + adapter.getGroup(group_pos),
                        Toast.LENGTH_SHORT).show();
                lastSelectedObject = adapter.getGroup(group_pos).toString();
                return false;
            }
        });

        // This listener will expand one group at one time
        // You can remove this listener for expanding all groups
        expandableListView.setOnGroupExpandListener(new OnGroupExpandListener()
        {
            // Default position
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition)
            {
                if (groupPosition != previousGroup)
                    // Collapse the expanded group
                    expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        // This listener will show toast on child click
        expandableListView.setOnChildClickListener(new OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView listview, View view, int groupPos, int childPos, long id)
            {
                Toast.makeText(MainActivity.this, "You clicked : " + adapter.getChild(groupPos, childPos),
                        Toast.LENGTH_SHORT).show();
                lastSelectedObject = adapter.getChild(groupPos, childPos).toString();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onProfileSelected()
    {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void onSettingsSelected()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onSensorsSelected()
    {
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.item1:
                onProfileSelected();
                return true;
            case R.id.item2:
                onSettingsSelected();
                return true;
            case R.id.item3:
                onSensorsSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}