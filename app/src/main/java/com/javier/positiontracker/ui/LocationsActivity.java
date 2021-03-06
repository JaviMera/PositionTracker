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
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

public class LocationsActivity extends AppCompatActivity
    implements LocationsActivityView{

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

        mAddresses = new LinkedList<>();

        RecyclerView.ItemAnimator animator = new FadeInDownAnimator();

        if(mRecyclerView.getItemAnimator() != animator)
            mRecyclerView.setItemAnimator(animator);
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

                mAddresses.clear();

                Long date = (Long) mSpinner.getAdapter().getItem(i);
                List<UserLocation> locations = getLocations(date);
                PositionTrackerDataSource source = new PositionTrackerDataSource(LocationsActivity.this);

                for(UserLocation location : locations) {

                    // Get a location address from cache object
                    LocationAddress locationAddress = source.readLocationAddress(
                        location.getPosition().latitude,
                        location.getPosition().longitude
                    );

                    locationAddress.setHour(location.getHour());
                    locationAddress.setMinute(location.getMinute());

                    // Add location address to list of addresses
                    mAddresses.add(locationAddress);
                }

                LocationRecyclerAdapter adapter = getRecyclerAdapter();

                // Initialize the adapter with the new list of addresses
                adapter.setLocations(mAddresses);
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
