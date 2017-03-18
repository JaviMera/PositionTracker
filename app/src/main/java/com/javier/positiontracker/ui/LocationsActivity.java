package com.javier.positiontracker.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.javier.positiontracker.R;
import com.javier.positiontracker.adapters.DatesSpinnerAdater;
import com.javier.positiontracker.adapters.LocationRecyclerAdapter;
import com.javier.positiontracker.adapters.RecyclerLinearLayout;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.io.FileManager;
import com.javier.positiontracker.model.LocationAddress;
import com.javier.positiontracker.model.UserLocation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationsActivity extends AppCompatActivity
    implements LocationsActivityView{

    @BindView(R.id.allLocationsCheckBox)
    CheckBox mCheckBox;

    @BindView(R.id.locationDatesSpinner)
    Spinner mSpinner;

    @BindView(R.id.locationsRecyclerView)
    RecyclerView mRecyclerView;

    private LocationsActivityPresenter mPresenter;
    private List<LocationAddress> mAddresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        mPresenter = new LocationsActivityPresenter(this);

        ButterKnife.bind(this);

        mPresenter.initializeRecyclerView();
        mPresenter.initializeSpinnerView();
        mPresenter.initializeCheckBoxView();

        mAddresses = new LinkedList<>();
    }

    @Override
    public void setRecyclerEnabled(boolean enabled) {

        // Change color of each item's text depending if they are Enabled / Disabled
        LocationRecyclerAdapter recyclerAdapter = (LocationRecyclerAdapter) mRecyclerView.getAdapter();
        recyclerAdapter.setEnabled(enabled);

        // Enable / Disable scrolling of recycler
        RecyclerLinearLayout layout = (RecyclerLinearLayout) mRecyclerView.getLayoutManager();
        layout.setCanScroll(enabled);
    }

    @Override
    public void setSpinnerEnabled(boolean enabled) {

        // Enable / Disable spinner control
        mSpinner.setEnabled(enabled);

        // Change color of spinner's view text depending if it's Enabled / Disabled
        DatesSpinnerAdater spinnerAdater = (DatesSpinnerAdater) mSpinner.getAdapter();
        spinnerAdater.setEnabled(enabled);
    }

    @Override
    public void initializeRecyclerView() {

        LocationRecyclerAdapter recyclerAdapter = new LocationRecyclerAdapter(this);
        mRecyclerView.setAdapter(recyclerAdapter);

        mRecyclerView.setLayoutManager(new RecyclerLinearLayout(this));
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void initializeSpinnerView() {

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);
        List<Long> dates = source.readAllDates();
        SpinnerAdapter adapter = new DatesSpinnerAdater(this, dates);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(getSpinnerListener());
    }

    @Override
    public void initializeCheckBoxView() {

        mCheckBox.setOnCheckedChangeListener(getCheckBoxListener());
    }

    private Intent getEmailIntent(File file) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(getString(R.string.export_intent_type));
        intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.export_intent_email));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_intent_subject));
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        return intent;
    }

    private AdapterView.OnItemSelectedListener getSpinnerListener() {

        return new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mAddresses.clear();

                PositionTrackerDataSource source = new PositionTrackerDataSource(LocationsActivity.this);

                Long date = (Long) mSpinner.getAdapter().getItem(i);
                List<UserLocation> locations = source.readLocationsWithRange(date, date);
                LocationRecyclerAdapter adapter = (LocationRecyclerAdapter) mRecyclerView.getAdapter();

                for(UserLocation location : locations) {

                    LocationAddress address = source.readLocationAddress(
                        location.getPosition().latitude,
                        location.getPosition().longitude
                    );

                    address.setHour(location.getHour());
                    address.setMinute(location.getMinute());
                    mAddresses.add(address);
                }

                adapter.setLocations(mAddresses);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener getCheckBoxListener() {

        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                mPresenter.setRecyclerEnabled(!checked);
                mPresenter.setSpinnerEnabled(!checked);
            }
        };
    }

    @OnClick(R.id.sendLocationsButton)
    public void onSendLocationsButtonClick(View view) {

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            FileManager fileManager = new FileManager(Environment.getExternalStorageDirectory());

            if(fileManager.createDirectory(Environment.DIRECTORY_DOCUMENTS)) {

                try {

                    // If the user checked the box, load all locations from the database
                    // Do this call only upon button click, so we don't load/unload them everytime
                    // the user might check/uncheck the box
                    if(mCheckBox.isChecked()) {

                        PositionTrackerDataSource source = new PositionTrackerDataSource(this);
//                        mAddresses = source.readAllAddresses();
                    }

                    File file = fileManager.createFile(
                        Environment.DIRECTORY_DOCUMENTS,
                        getString(R.string.locations_file_name),
                        mAddresses
                    );

                    Intent emailIntent = getEmailIntent(file);
                    Intent chooserIntent = Intent.createChooser(
                        emailIntent,
                        getString(R.string.export_intent_chooser_title)
                    );

                    startActivity(chooserIntent);
                }
                catch (ActivityNotFoundException anf) {

                    Toast.makeText(this, "There is no email client installed in the device.", Toast.LENGTH_LONG);
                }
                catch (IOException ignored) {

                    // By this instance the permissions should be already set for external storage
                }
            }
            else {

                Toast.makeText(this, "Unable to send locations via email", Toast.LENGTH_LONG).show();
            }
        }
        else {

            Toast.makeText(this, "Unable to send locations via email", Toast.LENGTH_LONG).show();
        }
    }
}
