package com.home.myapplication

import android.app.Fragment
import android.content.Context
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.FragmentManager
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.drawer.WearableActionDrawerView
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import kotlinx.android.synthetic.main.fragment_planet.*
import kotlinx.android.synthetic.main.fragment_planet.view.*
import java.util.*

class MainActivity : WearableActivity(), AmbientModeSupport.AmbientCallbackProvider,
    MenuItem.OnMenuItemClickListener,
    WearableNavigationDrawerView.OnItemSelectedListener {
    private var mWearableNavigationDrawer: WearableNavigationDrawerView? = null
    private var mWearableActionDrawer: WearableActionDrawerView? = null
    private var mSolarSystem: ArrayList<Planet>? = null
    private var mSelectedPlanet = 0
    private var mPlanetFragment: PlanetFragment? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_main)

        // Enables Ambient mode.
        setAmbientEnabled()
        mSolarSystem = initializeSolarSystem()
        mSelectedPlanet = 0

        // Initialize content to first planet.
        mPlanetFragment = PlanetFragment()
        val args = Bundle()
        val imageId: Int = getResources().getIdentifier(
            mSolarSystem!![mSelectedPlanet].image,
            "drawable", getPackageName()
        )
        args.putInt(PlanetFragment.ARG_PLANET_IMAGE_ID, imageId)
        mPlanetFragment!!.arguments = args
        val FragmentManager = getFragmentManager()
       FragmentManager.beginTransaction().replace(R.id.content_frame, mPlanetFragment as Fragment).commit()


        // Top Navigation Drawer
        mWearableNavigationDrawer = findViewById(R.id.top_navigation_drawer)
        mWearableNavigationDrawer!!.setAdapter(NavigationAdapter(this))
        // Peeks navigation drawer on the top.
        mWearableNavigationDrawer!!.controller.peekDrawer()
        mWearableNavigationDrawer!!.addOnItemSelectedListener(this)

        // Bottom Action Drawer
        mWearableActionDrawer = findViewById(R.id.bottom_action_drawer)
        // Peeks action drawer on the bottom.
        mWearableActionDrawer!!.controller.peekDrawer()
        mWearableActionDrawer!!.setOnMenuItemClickListener(this)

        /* Action Drawer Tip: If you only have a single action for your Action Drawer, you can use a
         * (custom) View to peek on top of the content by calling
         * mWearableActionDrawer.setPeekContent(View). Make sure you set a click listener to handle
         * a user clicking on your View.
         */
    }

    private fun initializeSolarSystem(): ArrayList<Planet> {
        val solarSystem: ArrayList<Planet> = ArrayList<Planet>()
        val planetArrayNames: Array<String> =
            getResources().getStringArray(R.array.planets_array_names)
        for (planet in planetArrayNames) {
            val planetResourceId: Int =
                getResources().getIdentifier(planet, "array", getPackageName())
            val planetInformation: Array<String> =
                getResources().getStringArray(planetResourceId)
            solarSystem.add(
                Planet(
                    planetInformation[0],  // Name
                    planetInformation[1],  // Navigation icon
                    planetInformation[2],  // Image icon
                    planetInformation[3],  // Moons
                    planetInformation[4],  // Volume
                    planetInformation[5]
                )
            ) // Surface area
        }
        return solarSystem
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        Log.d(TAG, "onMenuItemClick(): $menuItem")
        val itemId = menuItem.itemId
        var toastMessage = ""
        when (itemId) {
            R.id.menu_planet_name -> toastMessage = mSolarSystem!![mSelectedPlanet].volume
            R.id.menu_number_of_moons -> toastMessage = mSolarSystem!![mSelectedPlanet].volume
            R.id.menu_volume -> toastMessage = mSolarSystem!![mSelectedPlanet].volume
            R.id.menu_surface_area -> toastMessage =
                mSolarSystem!![mSelectedPlanet].surfaceArea
        }
        mWearableActionDrawer!!.controller.closeDrawer()
        return if (toastMessage.length > 0) {
            val toast = Toast.makeText(
                getApplicationContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            )
            toast.show()
            true
        } else {
            false
        }
    }

    // Updates content when user changes between items in the navigation drawer.
    override fun onItemSelected(position: Int) {
        Log.d(
            TAG,
            "WearableNavigationDrawerView triggered onItemSelected(): $position"
        )
        mSelectedPlanet = position
        val selectedPlanetImage: String = mSolarSystem!![mSelectedPlanet].image
        val drawableId: Int =
            getResources().getIdentifier(selectedPlanetImage, "drawable", getPackageName())
        mPlanetFragment!!.updatePlanet(drawableId)
    }

    private inner class NavigationAdapter /* package */ internal constructor(private val mContext: Context) :
        WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
        override fun getCount(): Int {
            return mSolarSystem!!.size
        }

        override fun getItemText(pos: Int): String {
            return mSolarSystem!![pos].name
        }

        override fun getItemDrawable(pos: Int): Drawable {
            val navigationIcon: String = mSolarSystem!![pos].navigationIcon
            val drawableNavigationIconId: Int =
                getResources().getIdentifier(navigationIcon, "drawable", getPackageName())
            return mContext.getDrawable(drawableNavigationIconId)!!
        }

    }

    /**
     * Fragment that appears in the "content_frame", just shows the currently selected planet.
     */
    class PlanetFragment : Fragment() {
        //private var mImageView: ImageView? = null
        private var mImageViewColorFilter: ColorFilter? = null
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            val rootView: View =
                inflater.inflate(R.layout.fragment_planet, container, false)
            //mImageView = rootView.findViewById(R.id.image)
            val imageIdToLoad =
                (R.drawable.mercury)
            //image.setImageResource(R.drawable.mercury)
            //mImageViewColorFilter = image.getColorFilter()
            return rootView
        }

        /* package */
        fun updatePlanet(imageId: Int) {
            image.setImageResource(imageId)
        }

        /* package */
        fun onEnterAmbientInFragment(ambientDetails: Bundle) {
            Log.d(
                TAG,
                "PlanetFragment.onEnterAmbient() $ambientDetails"
            )

            // Convert image to grayscale for ambient mode.
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(matrix)
            image.colorFilter = filter
        }

        /**
         * Restores the UI to active (non-ambient) mode.
         */
        /* package */
        fun onExitAmbientInFragment() {
            Log.d(TAG, "PlanetFragment.onExitAmbient()")
            image.colorFilter = mImageViewColorFilter
        }

        companion object {
            /* package */
            const val ARG_PLANET_IMAGE_ID = "planet_image_id"
        }
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return MyAmbientCallback()
    }

    private inner class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {
        /**
         * Prepares the UI for ambient mode.
         */
        override fun onEnterAmbient(ambientDetails: Bundle) {
            super.onEnterAmbient(ambientDetails)
            Log.d(TAG, "onEnterAmbient() $ambientDetails")
            mPlanetFragment!!.onEnterAmbientInFragment(ambientDetails)
            mWearableNavigationDrawer!!.controller.closeDrawer()
            mWearableActionDrawer!!.controller.closeDrawer()
        }

        /**
         * Restores the UI to active (non-ambient) mode.
         */
        override fun onExitAmbient() {
            super.onExitAmbient()
            Log.d(TAG, "onExitAmbient()")
            mPlanetFragment!!.onExitAmbientInFragment()
            mWearableActionDrawer!!.controller.peekDrawer()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}