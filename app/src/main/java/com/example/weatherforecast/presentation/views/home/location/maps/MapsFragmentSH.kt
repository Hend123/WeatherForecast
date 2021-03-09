package com.example.weatherforecast.presentation.views.home.location.maps

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.presentation.views.alerts.AddAlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapsFragmentSH : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerDragListener {
    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private var checkOneClick: Int = 0
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private lateinit var markerOptions: MarkerOptions
    private lateinit var marker: Marker


    companion object {
        private val TAG = "MapsFragmentSH"
        val MAPS_LATLON = "mapsLatLon"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_maps_s_h, container, false)
        val deleteBtn = v.findViewById(R.id.deleteBtn) as Button
        val doneBtn = v.findViewById<Button>(R.id.doneBtn)
        deleteBtn.setOnClickListener {
            marker?.let {
                marker.remove()
            }
        }
        doneBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragmentSH_to_settingsFragment)
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        geocoder = Geocoder(context)
        markerOptions = MarkerOptions()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        map.setOnMapClickListener(this)
        map.setOnMarkerDragListener(this)
        val egypt = LatLng(30.033333, 31.233334)
     //  marker =  map.addMarker(markerOptions.position(egypt).title("Cairo"))
        map.animateCamera(CameraUpdateFactory.newLatLng(egypt))
    }

    override fun onMapClick(latLng: LatLng?) {
        Log.v(TAG, latLng!!.toString())
        init()
       try {
           val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
           if (addresses.size > 0) {
               val address = addresses.get(0)
               val stAddress: String = address.getAddressLine(0)
             marker =  map.addMarker(markerOptions.position(latLng).title(stAddress).draggable(true))
               editor.clear()
               editor.putString(
                   MAPS_LATLON,
                   latLng.latitude.toString() + "," + latLng.longitude
               )
               Log.v(TAG, latLng.latitude.toString() + "," + latLng.longitude)

               editor.putInt(AddAlertDialog.LOCATION_CHOICE, 2)
               editor.apply()
               editor.commit()
               checkOneClick = 1
           } else {
               Toast.makeText(requireActivity(), "Invilde Address", Toast.LENGTH_LONG).show()
           }
       }catch (e:IOException){
           Toast.makeText(requireActivity(), "No Internet Connection or Invilde Address", Toast.LENGTH_LONG).show()

       }

            }


    private fun init() {
        sharedPreferences = requireActivity().getSharedPreferences(
            "prefs",
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
    }

    override fun onMarkerDragEnd(p0: Marker?) {
        Log.v(TAG, "drag end")
        Log.v(TAG, "Lat: " + p0!!.position.latitude + "Lng: " + p0.position.longitude)
        try {
            val addresses = geocoder.getFromLocation(p0.position.latitude, p0.position.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses.get(0)
                val stAddress: String = address.getAddressLine(0)
                p0.title = stAddress
                editor.putString(
                    MAPS_LATLON,
                    p0!!.position.latitude.toString() + "," + p0.position.longitude
                )
                editor.apply()
                editor.commit()
            } else {
                Toast.makeText(requireActivity(), "Invilde Address", Toast.LENGTH_LONG).show()

            }
        }catch (e:IOException){
            Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_LONG).show()

        }
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }
}