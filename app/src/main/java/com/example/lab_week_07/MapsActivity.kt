package com.example.lab_week_07

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.lab_week_07.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    // 🔹 (Langkah 4) Variabel untuk meluncurkan permission request
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Inisialisasi peta
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 🔹 (Langkah 15) Registrasi ActivityResult untuk permintaan izin
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Jika izin diberikan → ambil lokasi
                    getLastLocation()
                } else {
                    // Jika ditolak → tampilkan dialog alasan (rationale)
                    showPermissionRationale {
                        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                    }
                }
            }
    }

    // 🔹 (Langkah 17) Mengecek apakah izin lokasi sudah diberikan
    private fun hasLocationPermission() =
        ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    // 🔹 (Langkah 16) Menampilkan rationale dialog (penjelasan izin)
    private fun showPermissionRationale(positiveAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Location permission")
            .setMessage("This app will not work without knowing your current location.")
            .setPositiveButton(android.R.string.ok) { _, _ -> positiveAction() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // 🔹 (Langkah 12) Fungsi yang dipanggil setelah izin diberikan
    private fun getLastLocation() {
        Log.d("MapsActivity", "getLastLocation() called.")
        // Di tahap selanjutnya, di sini kamu bisa ambil lokasi real dari FusedLocationProviderClient
    }

    // 🔹 (Langkah 18) Logika saat peta siap digunakan
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Periksa izin lokasi
        when {
            hasLocationPermission() -> {
                // Jika sudah diizinkan, jalankan fungsi lokasi
                getLastLocation()
            }

            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                // Jika user sebelumnya menolak izin → tampilkan rationale dialog
                showPermissionRationale {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                }
            }

            else -> {
                // Jika belum pernah diminta → langsung minta izin
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }

        // Tambahkan marker default (misalnya Sydney)
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))
    }
}
