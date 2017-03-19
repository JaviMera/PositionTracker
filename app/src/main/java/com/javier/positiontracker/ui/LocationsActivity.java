package com.javier.positiontracker.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import com.javier.positiontracker.model.AddressCache;
import com.javier.positiontracker.model.LocationAddress;
import com.javier.positiontracker.model.UserLocation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationsActivity extends AppCompatActivity
    implements LocationsActivityView{

    @BindView(R.id.locationDatesSpinner)
    Spinner mSpinner;

    @BindView(R.id.locationsRecyclerView)
    RecyclerView mRecyclerView;

    private LocationsActivityPresenter mPresenter;
    private List<LocationAddress> mAddresses;
    private Geocoder mGeocoder;
    private AddressCache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        mPresenter = new LocationsActivityPresenter(this);

        ButterKnife.bind(this);

        mPresenter.initializeRecyclerView();
        mPresenter.initializeSpinnerView();

        mAddresses = new LinkedList<>();
        mGeocoder = new Geocoder(this, Locale.getDefault());

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        mCache = new AddressCache(maxMemory);
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

                try {
                    mAddresses.clear();

                    Long date = (Long) mSpinner.getAdapter().getItem(i);
                    List<UserLocation> locations = getLocations(date);

                    for(UserLocation location : locations) {

                        // Get a location address from cache object
                        LocationAddress locationAddress = mCache.get(location.getPosition());

                        // Check if location address does not exist in cache
                        if(locationAddress == null) {

                            // Create location address if it doesnt exist in cache
                            locationAddress = createLocationAddress(location);

                            // Store location address in cache to later retrieve it
                            mCache.insert(location.getPosition(), locationAddress);
                        }

                        // Add location address to list of addresses
                        mAddresses.add(locationAddress);
                    }

                    LocationRecyclerAdapter adapter = getRecyclerAdapter();

                    // Initialize the adapter with the new list of addresses
                    adapter.setLocations(mAddresses);
                }
                catch(IOException ex) {

                    Toast.makeText(
                        LocationsActivity.this,
                        "GPS is off. Please turn it on to retrieve location updates.",
                        Toast.LENGTH_LONG
                    )
                    .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
    }

    private LocationRecyclerAdapter getRecyclerAdapter() {

        return (LocationRecyclerAdapter) mRecyclerView.getAdapter();
    }

    private List<UserLocation> getLocations(long date) {

        PositionTrackerDataSource source = new PositionTrackerDataSource(LocationsActivity.this);
        return source.readLocationsWithRange(date, date);
    }

    private Address getGeoAddress(UserLocation location) throws IOException{

        return mGeocoder.getFromLocation(
            location.getPosition().latitude,
            location.getPosition().longitude,
            1
        )
        .get(0);
    }

    private LocationAddress createLocationAddress(UserLocation location) throws IOException {

        return new LocationAddress(
            getGeoAddress(location),
            location.getHour(),
            location.getMinute()
        );
    }

    @OnClick(R.id.sendLocationsButton)
    public void onSendLocationsButtonClick(View view) {

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            FileManager fileManager = new FileManager(Environment.getExternalStorageDirectory());

            if(fileManager.createDirectory(Environment.DIRECTORY_DOCUMENTS)) {

                try {

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
