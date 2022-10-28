package com.alox1d.vmeste.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alox1d.vmeste.R
import com.alox1d.vmeste.databinding.FragmentFriendsBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.runtime.image.ImageProvider


class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val TARGET_LOCATION: Point = Point(55.89996, 41.66016)
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var mapview: MapView? = null
    private var searchManager: SearchManager? = null
    private var session: MutableList<Session> = mutableListOf()
    private var friendsViewModel: FriendsViewModel? = null
    private var foundCities: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        friendsViewModel = ViewModelProvider(this).get(FriendsViewModel::class.java)
        MapKitFactory.initialize(requireContext());
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        friendsViewModel?.friends?.observe(viewLifecycleOwner) { friendModels ->
            // `searchManager` и `searchSession` – это поля. Менеджер нет смысла
// создавать на каждый запрос, а сессию просто нужно сохранять.
            searchManager = SearchFactory.getInstance().createSearchManager(
                SearchManagerType.ONLINE
            )

            mapview?.map?.mapObjects?.clear()
            foundCities.clear()
            for (friend in friendModels) {
                friend.city?.let { city ->
                    if (foundCities.contains(city)) return@let
                    foundCities.add(city)
                    val s = searchManager!!.submit(city + friend.country.orEmpty(), VisibleRegionUtils.toPolygon(mapview!!.getMap().getVisibleRegion()),
                        SearchOptions().apply {
                            setGeometry(true)
                            searchTypes = SearchType.GEO.value
                        },
                        object: Session.SearchListener {
                            override fun onSearchError(p0: com.yandex.runtime.Error) {
                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                            }

                            override fun onSearchResponse(response: Response) {
                                val mapObjects: MapObjectCollection = mapview?.map?.mapObjects ?: return

                                val searchResult = response.collection.children.getOrNull(0) ?: return
                                val resultLocation = searchResult.obj!!.geometry[0].point
                                if (resultLocation != null) {
                                    mapObjects.addPlacemark(
                                        resultLocation,
                                        ImageProvider.fromResource(
                                            requireContext(),
                                            R.drawable.search_result
                                        )
                                    )
                                }

                            }
                        }
                    )
                    session.add(s)
                }
            }
        }
        mapview = binding.mapview
        mapview?.getMap()?.move(
            CameraPosition(TARGET_LOCATION, 4.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )

        return root
    }

    override fun onStart() {
        // Вызов onStart нужно передавать инстансам MapView и MapKit.
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview?.onStart()
        friendsViewModel?.makeFriends()
    }

    override fun onStop() {
        // Вызов onStop нужно передавать инстансам MapView и MapKit.
        mapview?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        friendsViewModel = null
    }

    companion object {
        fun newInstance() = FriendsFragment()
    }
}