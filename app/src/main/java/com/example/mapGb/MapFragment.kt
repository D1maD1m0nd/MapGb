package com.example.mapGb

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mapGb.databinding.FragmentMapBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider


class MapFragment : Fragment(),CameraListener, MapObjectTapListener, UserLocationObjectListener,GeoObjectTapListener, InputListener {
    private var permissionLocation = false
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var userLocationLayer: UserLocationLayer? = null
    private var mapObjects: MapObjectCollection? = null
    private val permissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            onMapReady()
        } else {
            Toast.makeText(context, getString(R.string.disable_permission), Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermission() {
        permissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkPermission() {
        activity?.let {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    onMapReady()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        checkPermission()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun moveCameraToPosition(target: Point?) {
        binding.mapview.map.move(
            CameraPosition(target!!, 10.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 2F), null
        )
    }

    private fun initView() = with(binding) {
        setBottomSheetBehavior(binding.behaivorContainer.bottomSheetContainer)
        mapObjects = mapview.map.mapObjects.addCollection()
        mapview.map.isRotateGesturesEnabled = false
        mapview.map.addTapListener(this@MapFragment)
        mapview.map.addInputListener(this@MapFragment)
    }

    private fun onMapReady() {
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        userLocationLayer?.let { userLocationLayer ->
            userLocationLayer.setObjectListener(this)

            binding.mapview.map.addCameraListener(this)
            binding.fabPin.setOnClickListener { _ ->
                userLocationLayer.isVisible = true
                userLocationLayer.isHeadingEnabled = true
                userLocationLayer.cameraPosition()?.let { camera ->
                    moveCameraToPosition(camera.target)
                }
            }
        }
        permissionLocation = true
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onDestroyView() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onDestroyView()
    }



    override fun onObjectAdded(userLocationView: UserLocationView) = with(binding) {

        userLocationLayer?.setAnchor(
            PointF((mapview.width * 0.5).toFloat(), (mapview.height * 0.5).toFloat()),
            PointF((mapview.width * 0.5).toFloat(), (mapview.height * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                context, R.drawable.user_arrow
            )
        )

        val pinIcon: CompositeIcon = userLocationView.pin.useCompositeIcon()

        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(context, R.drawable.ic_baseline_pin_drop_24),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )


        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
    }

    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        val selectionMetadata: GeoObjectSelectionMetadata = geoObjectTapEvent
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)

        binding.mapview.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)

        return true
    }

    override fun onMapTap(p0: Map, p1: Point) {
        addPlaceMark(p1)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        TODO("Not yet implemented")
    }



    private fun addPlaceMark(point : Point) {
        mapObjects?.addPlacemark(point)?.apply {
            setIcon(
                    ImageProvider.fromResource(context, R.drawable.search_result)
                )
            isDraggable = true
        }
    }

    override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
        TODO("Not yet implemented")
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}