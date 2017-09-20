/*
 * Copyright (C) 2017 Marcos Rivas Rojas
 *
 *
 */
package com.marivr.ejemplorecyclerview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import static com.marivr.ejemplorecyclerview.R.id.textView;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private TextView tvLat, tvLon;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    
    private RecyclerViewClickListener listener;
    private GoogleApiClient googleApiClient; // Cliente de Google API
    private Location location;           // Objeto para obtener ubicación
    private final int REQUEST_LOCATION = 1;   // Código de petición para ubicación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               Intent intent = new Intent(v.getContext(), ContactDtails.class);
               startActivityForResult(intent, 0);
           }
        });
       tvLat = (TextView) findViewById(textView);
        tvLon = (TextView) findViewById(R.id.textView2);


// TODO: 3.- Inicializar cliente de Google API
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        /*
            Un LayoutManager es el responsable de medir y saber la posición de los elementos dentro
            de un RecyclerView, así como determinar el momento en que los elementos se ocultan y muestran
         */
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        ArrayList<Foto> fotos = new ArrayList<Foto>();

        fotos.add(new Foto("Slark", R.mipmap.slark));
        fotos.add(new Foto("Overwatch", R.mipmap.over));

        // TODO: 13.- Ingresamos a nuestro adaptador un nuevo listener para poder saber el elemento al que se le dio click
        RecyclerViewCustomAdapter adapter = new RecyclerViewCustomAdapter(fotos, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                Toast.makeText(MainActivity.this, "Elemento " + position, Toast.LENGTH_SHORT).show();
            }


        });
        recyclerView.setAdapter(adapter);


    }

    // TODO: 4.- Mandar a llamar la función processLocation()

    /**
     *
     *
     * Mandamos a llamar el método processLocation donde vamos a validar
     * permisos, ubicación y errores
     * @param bundle
     *
     *
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        processLocation();
    }


    // TODO: 5.- obtener ubicación y validar que no esté vacío

    private void processLocation() {
        // Se trata de obtener la última ubicación registrada
        getLocation();

        // Si ubicación es diferente de nulo se actualizan los campos para escribir la ubicación
        if (location != null) {
            updateLocationUI();
        }
    }

    // TODO 6.- Definimos el método getLocation()

    private void getLocation() {
        // Se valida que haya permisos garantizados
        if (isLocationPermissionGranted()) {
            // Si los hay se regresa un objeto con información de ubicacion
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } else {
            // Sino se administra la petición de permisos
            requestPermission();
        }
    }

    // TODO 7.- Definimos el método para saber si tenemos acceso a usar la ubicación

    /**
     *
     *
     * Maneja la condicional para saber si el usuario ha dado permiso
     * de usar la ubicación en la app. En este ejemplo se usa el condicional
     * solo con ACCESS_FINE_LOCATION
     * @return
     */
    private boolean isLocationPermissionGranted() {
        /* Valida si ya se dio permiso */
        int permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        /* Se regresa un valor booleano para saber si la app tiene permisos o no */
        return permission == PackageManager.PERMISSION_GRANTED;

    }

    // TODO: 8.- Pedimos permiso para poder acceder a la ubicación

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(this, "No quisiste dar acceso a tu ubicación", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    // TODO: 9.- Se obtienen los valores de la ubicación
    private void updateLocationUI() {

        tvLat.setText(String.valueOf(location.getLatitude()));
        tvLon.setText(String.valueOf(location.getLongitude()));
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                /* se pide la última ubicación */
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                /* Si la ubicación es diferente de nulo, es decir, se regresó la ubicación
                *   Entonces se actualiza la interfaz con los valores
                *   */
                if (location != null)
                    updateLocationUI();
                else
                    Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}


    // TODO: 10.- Define los métodos de onStart y onStop, ya que son los que permiten que se active el servicio
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d("onLocationChanged", "cambió ubicación");
        updateLocationUI();
    }


}
