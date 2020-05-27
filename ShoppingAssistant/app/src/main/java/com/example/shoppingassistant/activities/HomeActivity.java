package com.example.shoppingassistant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingassistant.LocationService;
import com.example.shoppingassistant.R;
import com.example.shoppingassistant.model.Data;
import com.example.shoppingassistant.model.ItemType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.example.shoppingassistant.utils.DatabaseWrapper;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends AppCompatActivity
{
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private Double latitude, longitude;
    private ItemType type;
    private String amount;
    private String name;
    private String post_key;
    private Boolean checked;
    private NotificationManagerCompat notificationManager;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    boolean isLocationServiceActive;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        notificationManager = NotificationManagerCompat.from(this);
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ShoppingList");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String uId = mUser.getUid();

        if (mDatabase == null)
        {
            mDatabase = DatabaseWrapper.getDatabase().getReference().child("ShoppingList").child(uId);
        }
        recyclerView = findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab_btn = findViewById(R.id.fab);
        fab_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editOrRemoveItem();
            }
        });
    }

    private void editOrRemoveItem()
    {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_data, null);

        final AlertDialog dialog = mydialog.create();

        dialog.setView(myview);

        final Spinner type = myview.findViewById(R.id.edt_type);
        final EditText amount = myview.findViewById(R.id.edt_amount);
        final EditText name = myview.findViewById(R.id.edt_name);
        Button btn_save = myview.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ItemType mType = ItemType.valueOf(type.getSelectedItem().toString());
                String mAmount = amount.getText().toString().trim();
                String mName = name.getText().toString().trim();
                Boolean mChecked = false;

                if (TextUtils.isEmpty(mAmount))
                {
                    amount.setError("Required Field...");
                    return;
                }
                if (TextUtils.isEmpty(mName))
                {
                    name.setError("Required Field...");
                    return;
                }

                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(mType, mAmount, mName, date, id, mChecked);

                assert id != null;
                mDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(), "Data add", Toast.LENGTH_SHORT).show();

                mDatabase.push();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart()
    {
        stopService();
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.item_data,
                MyViewHolder.class,
                mDatabase.orderByChild("checked")
        )
        {

            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, final Data data, final int position)
            {
                myViewHolder.setType(data.getType().toString());
                myViewHolder.setName(data.getName());
                myViewHolder.setDate(data.getDate());
                myViewHolder.setChecked(data.getChecked());
                myViewHolder.setAmount(String.valueOf(data.getAmount()));

                final TextView update_name = myViewHolder.myView.findViewById(R.id.name);
                final TextView update_type = myViewHolder.myView.findViewById(R.id.type);
                final TextView update_amount = myViewHolder.myView.findViewById(R.id.amount);
                final CheckBox update_checked = myViewHolder.myView.findViewById(R.id.checked);

                myViewHolder.myView.findViewById(R.id.item_map).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getNearStores(data.getType().toString());
                    }
                });

                myViewHolder.myView.findViewById(R.id.checked).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        post_key = getRef(position).getKey();
                        type = ItemType.valueOf(update_type.getText().toString());
                        String mAmount = update_amount.getText().toString().trim();
                        name = update_name.getText().toString().trim();

                        String date = DateFormat.getDateInstance().format(new Date());

                        Data data = new Data(type, mAmount, name, date, post_key, update_checked.isChecked());

                        mDatabase.child(post_key).setValue(data);
                    }
                });

                myViewHolder.myView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        post_key = getRef(position).getKey();
                        type = data.getType();
                        name = data.getName();
                        amount = data.getAmount();
                        checked = data.getChecked();

                        updateData();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        View myView;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            myView = itemView;
        }

        void setType(String type)
        {
            TextView mType = myView.findViewById(R.id.type);
            mType.setText(type);
        }

        public void setName(String name)
        {
            TextView mName = myView.findViewById(R.id.name);
            mName.setText(name);
        }

        void setDate(String date)
        {
            TextView mDate = myView.findViewById(R.id.date);
            mDate.setText(date);
        }

        void setAmount(String amount)
        {
            TextView mAmount = myView.findViewById(R.id.amount);
            mAmount.setText(String.valueOf(amount));
        }

        void setChecked(Boolean checked)
        {
            CheckBox mChecked = myView.findViewById(R.id.checked);
            mChecked.setChecked(checked);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.log_out)
        {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            notificationManager.cancelAll();
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        else
        {
            Toast.makeText(getBaseContext(), "Tap back button in order to sign-out", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    public void updateData()
    {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View mView = inflater.inflate(R.layout.update_input_field, null);
        final AlertDialog dialog = myDialog.create();
        dialog.setView(mView);
        final Spinner edtType = mView.findViewById(R.id.edt_type_upd);
        final EditText edtAmount = mView.findViewById(R.id.edt_amount_upd);
        final EditText edtName = mView.findViewById(R.id.edt_name_upd);

        edtType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ItemType.values()));
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());
        edtName.setText(name);
        edtName.setSelection(name.length());
        Button btnUpdate = mView.findViewById(R.id.btn_update);
        Button btnDelete = mView.findViewById(R.id.btn_delete);

        btnUpdate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                type = ItemType.valueOf(edtType.getSelectedItem().toString());
                String mAmount = edtAmount.getText().toString().trim();
                name = edtName.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(type, mAmount, name, date, post_key, checked);

                mDatabase.child(post_key).setValue(data);

                mDatabase.push();
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void getNearStores(String itemType)
    {
        requestPermissions();
        FusedLocationProviderClient clientLocation = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(HomeActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            latitude = 0.0;
            longitude = 0.0;
        }
        else
        {
            clientLocation.getLastLocation().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<Location>()
            {
                @Override
                public void onSuccess(Location location)
                {
                    if (location != null)
                    {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            });
        }

        String mainCategory = "";
        switch (ItemType.valueOf(itemType))
        {
            case Food:
                mainCategory = "Market";
                break;
            case Tool:
                mainCategory = "Home Store";
                break;
            case Electronic:
                mainCategory = "Electronics store";
                break;
            case Book:
                mainCategory = "Bookstore";
                break;
            case Clothing:
                mainCategory = "Clothes shop";
                break;
            case Pharmacy:
                mainCategory = "Pharmacy";
                break;
        }

        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + mainCategory);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(mapIntent);
        }
    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    public void startService()
    {
        if (!isLocationServiceActive)
        {
            System.out.println("Started location service");
            Intent serviceIntent = new Intent(this, LocationService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
            isLocationServiceActive = true;
        }
    }

    public void stopService()
    {
        if (isLocationServiceActive)
        {
            System.out.println("Stopped location service");
            Intent serviceIntent = new Intent(this, LocationService.class);
            stopService(serviceIntent);
            isLocationServiceActive = false;
        }
    }

    @Override
    protected void onStop()
    {
        startService();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        startService();
        super.onDestroy();
    }
}
