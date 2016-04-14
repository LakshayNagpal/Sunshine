package com.example.android.sunshinefragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshinefragment.data.WeatherContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int FORECAST_LOADER = 0;
    private ForecastAdapter mForecastAdapter;
    ListView listView;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_refresh){
//            EditText e = (EditText) getView().findViewById(R.id.edittext1);
//            String edit = e.getText().toString();
            updateweather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        String locationSetting = Utility.getPreferredLocation(getActivity());
//
//        // Sort order:  Ascending, by date.
//        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis());
//
//        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
//                null, null, null, sortOrder);
//        mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);

        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - Thunder - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

//        mForecastAdapter =
//                new ArrayAdapter<String>(
//                        getActivity(), // The current context (this activity)
//                        R.layout.list_item_forecast, // The name of the layout ID.
//                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
//                        weekForecast);


        View rootview =  inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView) rootview.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    startActivity(intent);
                }
            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//                // ListView Clicked item index
//                //int itemPosition = position;
//
//                // ListView Clicked item value
//                String itemValue = (String) listView.getItemAtPosition(position);
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, itemValue);
//                startActivity(intent);
//
////
////                // Show Alert
////                Toast.makeText(getActivity(),
////                        "  ListItem : " + itemValue, Toast.LENGTH_SHORT)
////                        .show();
//            }
//
//        });
        return rootview;
    }

    void onLocationChanged( ){
        updateweather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
        public void onActivityCreated(Bundle savedInstanceState) {
                getLoaderManager().initLoader(FORECAST_LOADER, null, this);
               super.onActivityCreated(savedInstanceState);
            }

    private void updateweather(){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
//        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);

        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
                String location = Utility.getPreferredLocation(getActivity());

        weatherTask.execute(location);

    }


    @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String locationSetting = Utility.getPreferredLocation(getActivity());

            // Sort order:  Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                                locationSetting, System.currentTimeMillis());

            return new CursorLoader(getActivity(),
                    weatherForLocationUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    sortOrder);
            }

    @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
                mForecastAdapter.swapCursor(cursor);
        }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }



//    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
//
//        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//
//        private String getReadableDateString(long time){
//            // Because the API returns a unix timestamp (measured in seconds),
//            // it must be converted to milliseconds in order to be converted to valid date.
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(time);
//        }
//
//        private String formatHighLows(double high, double low, String unitType) {
//            // For presentation, assume the user doesn't care about tenths of a degree.
//
//            if(unitType.equals("imperial")){
//                high = (high * 1.8) + 32;
//                low = (low * 1.8) +32;
//            }else if(!unitType.equals("metric")){
//                Log.d(LOG_TAG, "Unit type not found: " + unitType);
//            }
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//            String highLowStr = roundedHigh + "/" + roundedLow;
//            return highLowStr;
//        }
//
//        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String OWM_LIST = "list";
//            final String OWM_WEATHER = "weather";
//            final String OWM_TEMPERATURE = "temp";
//            final String OWM_MAX = "max";
//            final String OWM_MIN = "min";
//            final String OWM_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            String[] resultStrs = new String[numDays];
//
//            SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            String unitType = s.getString(getString(R.string.pref_syncConnectionType_key),"metric");
//            for(int i = 0; i < weatherArray.length(); i++) {
//                // For now, using the format "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // The date/time is returned as a long.  We need to convert that
//                // into something human-readable, since most people won't read "1400356800" as
//                // "this saturday".
//                long dateTime;
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay+i);
//                day = getReadableDateString(dateTime);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                double high = temperatureObject.getDouble(OWM_MAX);
//                double low = temperatureObject.getDouble(OWM_MIN);
//
//                highAndLow = formatHighLows(high, low, unitType);
//                resultStrs[i] = day + " - " + description + " - " + highAndLow;
//            }
//
//            for (String s1 : resultStrs) {
//                Log.v(LOG_TAG, "Forecast entry: " + s1);
//            }
//            return resultStrs;
//
//        }
//
//
//        @Override
//        protected String[] doInBackground(String... params){
//
//            if(params.length == 0){
//                return null;
//            }
//
//            HttpURLConnection urlconnection = null;
//            BufferedReader reader = null;
//
//            String forecastjson = null;
//            String units = "metric";
//            int numDays = 15;
//
//            try{
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("http")
//                        .authority("api.openweathermap.org")
//                        .appendPath("data")
//                        .appendPath("2.5")
//                        .appendPath("forecast")
//                        .appendPath("daily")
//                        .appendQueryParameter("q", params[0])
//                        .appendQueryParameter("mode", "json")
//                        .appendQueryParameter("units", units)
//                        .appendQueryParameter("cnt", Integer.toString(numDays));
//                String myurl = builder.build().toString();
//
//                //String baseurl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043,usa&mode=json&units=metric&cnt=7";
//                String apikey = "&appid=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
//                URL url = new URL(myurl.concat(apikey));
//                Log.v(LOG_TAG, "Built URI " + url.toString());
//
//                urlconnection = (HttpURLConnection) url.openConnection();
//                urlconnection.setRequestMethod("GET");
//                urlconnection.connect();
//
//                InputStream input = urlconnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//
//                if(input == null){
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(input));
//
//                String line;
//
//                while((line = reader.readLine())!=null){
//
//                    buffer.append(line+"\n");
//                }
//
//                if(buffer.length() == 0){
//                    return null;
//                }
//                forecastjson = buffer.toString();
//
//                Log.v(LOG_TAG,"Forecast JSON String" + forecastjson);
//
//            }catch(IOException e){
//
//                Log.e(LOG_TAG, "Error", e);
//
//                return null;
//            }finally{
//
//                if(urlconnection!=null){
//                    urlconnection.disconnect();
//                }
//                if(reader!=null){
//                    try{
//                        reader.close();
//                    }catch(final IOException e){
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            try{
//                return getWeatherDataFromJson(forecastjson, numDays);
//            }catch(JSONException e){
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        public void onPostExecute(String[] result){
//            if(result!=null){
//                mForecastAdapter.clear();
//                //mForecastAdapter.addAll(result);          //for newer api's(above gingerbread) this code will work
//                for(String w:result){
//                    mForecastAdapter.add(w);
//                }
//            }
//        }
//    }

}
