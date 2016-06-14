package com.example.clarisselawson.tripbudget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.clarisselawson.tripbudget.adapter.TripAdapter;
import com.example.clarisselawson.tripbudget.adapter.TripViewHolder;
import com.example.clarisselawson.tripbudget.database.DBHelper;
import com.example.clarisselawson.tripbudget.listener.SwipeCardListener;
import com.example.clarisselawson.tripbudget.logger.Log;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static String TAG = "MainActivity";
    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;

    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private ArrayList<Trip> allTrips;
    DBHelper myDb;

    private int REQUEST_CREATE_TRIP = 0;
    private int REQUEST_UPDATE_TRIP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        myDb = DBHelper.getInstance(getApplicationContext());
        allTrips = myDb.getAllTrips();

        findViewById(R.id.add_trip_icon).setClipToOutline(true);

        tripAdapter = new TripAdapter(allTrips, this);
        recyclerView = (RecyclerView) findViewById(R.id.trip_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(tripAdapter);
        setupSwipeListeners();
        setupPlacePhotoApi();
    }

    private void setupPlacePhotoApi() {
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

    }

    private void setupSwipeListeners() {
        SwipeCardListener tripCardTouchListener =
                new SwipeCardListener(recyclerView,
                        new SwipeCardListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    myDb.deleteTrip(allTrips.get(position).getId());
                                    allTrips.remove(position);
                                    tripAdapter.notifyItemRemoved(position);
                                }

                                tripAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                Intent intent = new Intent(getApplicationContext(), EditTripActivity.class);
                                int position = reverseSortedPositions[0];
                                intent.putExtra("trip", allTrips.get(position));
                                intent.putExtra("position", position);
                                startActivityForResult(intent, REQUEST_UPDATE_TRIP);
                            }
                        });

        recyclerView.addOnItemTouchListener(tripCardTouchListener);
    }

    public void newTrip(View v) {
        Intent intent = new Intent(this, EditTripActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_TRIP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        int position = 0;
        Trip trip = data.getExtras().getParcelable("trip");
        if (requestCode == REQUEST_CREATE_TRIP) {
            allTrips.add(trip);
            position = allTrips.size() - 1;
        }
        if (requestCode == REQUEST_UPDATE_TRIP) {
            position = data.getExtras().getInt("position");
            allTrips.set(position, trip);
        }
        tripAdapter.notifyDataSetChanged();

        // afficher l'image de la place si on a sélectionné une place
        String placeId = data.getExtras().getString("placeId");
        if (placeId != null) {
            placePhotosAsync(placeId, trip.getId(), position);
        }
    }

    /**
     * Load a bitmap from the photos API asynchronously
     * by using buffers and result callbacks.
     */
    private void placePhotosAsync(final String placeId, final long tripId, final int tripPosition) {

        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            final ImageView imageView = ((TripViewHolder) recyclerView.findViewHolderForAdapterPosition(tripPosition)).getImageView();

                            // TODO: handle new trip
                            if (imageView == null) return;

                            // Display the first bitmap in an ImageView in the size of the view
                            photoMetadataBuffer.get(0)
                                    .getScaledPhoto(mGoogleApiClient, imageView.getWidth(),
                                            imageView.getHeight())
                                    .setResultCallback(new ResultCallback<PlacePhotoResult>() {
                                        @Override
                                        public void onResult(PlacePhotoResult placePhotoResult) {
                                            if (!placePhotoResult.getStatus().isSuccess()) {
                                                return;
                                            }
                                            imageView.setImageBitmap(placePhotoResult.getBitmap());
                                            try {
                                                FileOutputStream outputStream = openFileOutput(String.valueOf(tripId), Context.MODE_PRIVATE);
                                                placePhotoResult.getBitmap().compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                                                outputStream.close();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }
                        photoMetadataBuffer.release();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google API connexion failed: " + connectionResult.getErrorMessage());
    }
}


