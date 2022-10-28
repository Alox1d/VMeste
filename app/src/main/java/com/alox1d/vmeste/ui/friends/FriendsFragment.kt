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
import com.yandex.mapkit.geometry.Geometry
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val friendsViewModel =
            ViewModelProvider(this).get(FriendsViewModel::class.java)
        MapKitFactory.initialize(requireContext());
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        friendsViewModel.friends.observe(viewLifecycleOwner) {
            // `searchManager` и `searchSession` – это поля. Менеджер нет смысла
// создавать на каждый запрос, а сессию просто нужно сохранять.
            searchManager = SearchFactory.getInstance().createSearchManager(
                SearchManagerType.ONLINE
            )
            val point = Geometry.fromPoint(Point(59.945933, 30.320045))
            val point1 = (Point(65.80278, 37.79297))
            val point2 = (Point(45.70618 , 46.93359))
            val point3 = (Point(71.46912 , 180.70313))
            val point4 = (Point(48.9225, 156.62109))

            for (friend in it){
                friend.city?.let { city ->
                     val s = searchManager!!.submit(city, VisibleRegionUtils.toPolygon(mapview!!.getMap().getVisibleRegion()),
                         SearchOptions().apply {
                         setGeometry(true)
                         searchTypes = SearchType.GEO.value
                         },
                        object: Session.SearchListener {
                            override fun onSearchError(p0: com.yandex.runtime.Error) {
                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                            }

                            override fun onSearchResponse(response: Response) {
                                val mapObjects: MapObjectCollection =
                                    mapview!!.getMap().getMapObjects()
//                                mapObjects.clear()

                                for (searchResult in response.getCollection().getChildren()) {
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
//                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    session.add(s)
                }
            }
        }
        mapview = binding.mapview
//        mapview?.getMap()?.move(
//            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
//            Animation(Animation.Type.SMOOTH, 0f),
//            null
//        )
        mapview?.getMap()?.move(
            CameraPosition(TARGET_LOCATION, 4.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )


        friendsViewModel.makeFriends()

        return root
    }
     override fun onStop() {
        // Вызов onStop нужно передавать инстансам MapView и MapKit.
        mapview?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
     override fun onStart() {
        // Вызов onStart нужно передавать инстансам MapView и MapKit.
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview?.onStart()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}