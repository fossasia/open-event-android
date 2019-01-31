package org.fossasia.openevent.general

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_welcome.view.pickCityButton
import kotlinx.android.synthetic.main.fragment_welcome.view.currentLocation
import kotlinx.android.synthetic.main.fragment_welcome.view.locationProgressBar
import org.fossasia.openevent.general.search.GeoLocationViewModel
import org.fossasia.openevent.general.search.SearchLocationViewModel
import org.fossasia.openevent.general.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

const val LOCATION_SAVED = "LOCATION_SAVED"
const val LOCATION_PERMISSION_REQUEST = 1000

class WelcomeFragment : Fragment() {
    private lateinit var rootView: View
    private val geoLocationViewModel by viewModel<GeoLocationViewModel>()
    private val searchLocationViewModel by viewModel<SearchLocationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_welcome, container, false)
        val thisActivity = activity
        if (thisActivity is AppCompatActivity)
            thisActivity.supportActionBar?.hide()
        rootView.pickCityButton.setOnClickListener {
            Navigation.findNavController(rootView).navigate(R.id.searchLocationFragment, null, Utils.getAnimSlide())
        }

        geoLocationViewModel.currentLocationVisibility.observe(this, Observer {
            rootView.currentLocation.visibility = View.GONE
        })
        geoLocationViewModel.configure(null)

        rootView.currentLocation.setOnClickListener {
            checkLocationPermission()
            geoLocationViewModel.configure(activity)
            rootView.locationProgressBar.visibility = View.VISIBLE
        }
        geoLocationViewModel.location.observe(this, Observer { location ->
            searchLocationViewModel.saveSearch(location)
            redirectToMain()
        })

        return rootView
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    geoLocationViewModel.configure(activity)
                } else {
                    Snackbar.make(rootView, "Cannot fetch location!", Snackbar.LENGTH_SHORT).show()
                    rootView.locationProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun checkLocationPermission() {
        val permission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }
    }

    private fun redirectToMain() {
        Navigation.findNavController(rootView).navigate(R.id.eventsFragment, null, Utils.getAnimSlide())
    }
}
