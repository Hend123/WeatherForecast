package com.example.weatherforecast.presentation.views.favorite.maps

import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.presentation.views.home.location.maps.MapsFragmentSH

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class FavoriteMaps : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,GoogleMap.OnMarkerDragListener {
    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private  var marker:Marker? = null
    private  var latLonBundle:Bundle? = null

    companion object {
        private val TAG = "FavoriteMaps"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_favorite_maps, container, false)
        val deleteBtn = v.findViewById(R.id.deleteBtn) as Button
        val doneBtn = v.findViewById<Button>(R.id.doneBtn)
        deleteBtn.setOnClickListener {
            marker?.let {
                marker!!.remove()
            }

        }
        doneBtn.setOnClickListener {
            if (latLonBundle == null) {
                findNavController().navigate(
                    R.id.action_favoriteMaps_to_favoriteFragment
                )
            } else {
                findNavController().navigate(
                    R.id.action_favoriteMaps_to_favoriteFragment,
                    latLonBundle
                )
            }
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        geocoder = Geocoder(context)
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        googleMap.setOnMapClickListener(this)
        googleMap.setOnMarkerDragListener(this)
        val egypt = LatLng(30.033333, 31.233334)
        map.moveCamera(CameraUpdateFactory.newLatLng(egypt))
    }

    override fun onMapClick(latLng: LatLng?) {
        Log.v(TAG, latLng!!.toString())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses.get(0)
                val stAddress: String = address.getAddressLine(0)
              marker =  googleMap.addMarker(MarkerOptions().position(latLng).title(stAddress).draggable(true))
                 latLonBundle = bundleOf("latLng" to latLng)
            } else {
                Toast.makeText(requireActivity(), "Invilde Address", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Toast.makeText(requireActivity(), "No Internet Connection or Invilde Address", Toast.LENGTH_LONG).show()

        }

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
                latLonBundle = bundleOf("latLng" to p0.position)

            } else {
                Toast.makeText(requireActivity(), "Invilde Address", Toast.LENGTH_LONG).show()

            }
        }catch (e:IOException){
            Toast.makeText(requireActivity(), "No Internet Connection or Invilde Address", Toast.LENGTH_LONG).show()

        }
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }


}