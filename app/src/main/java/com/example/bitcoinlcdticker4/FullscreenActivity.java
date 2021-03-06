package com.example.bitcoinlcdticker4;

import android.os.Bundle;
import android.app.Activity;
import android.util.TypedValue;
import android.widget.TextView;
import android.graphics.Typeface;
import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FullscreenActivity extends Activity
{
    private int mInterval = 5000;
    private Handler mHandler;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        TextView tx = findViewById(R.id.fullscreen_content);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/7segment.ttf");
        tx.setTypeface(custom_font);

        mHandler = new Handler();
        mQueue = Volley.newRequestQueue(this);
        startRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                updateStatus();
            }
            finally
            {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    boolean skip = true;

    private void updateStatus()
    {
        final TextView tx = findViewById(R.id.fullscreen_content);
        String url ="https://www.bitmex.com/api/v1/trade?symbol=XBT&count=1&reverse=true";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                if (skip)
                {
                    skip = false;
                }
                else
                {
                    try
                    {
                        int value = (int)response.getJSONObject(0).getDouble("price");
                        tx.setText(String.valueOf(value));
                        if (value >= 10000)
                        {
                            tx.setTextSize(TypedValue.COMPLEX_UNIT_SP, 360);
                        }
                        else
                        {
                            tx.setTextSize(TypedValue.COMPLEX_UNIT_SP, 420);
                        }
                    }
                    catch (JSONException e)
                    {
                        tx.setText("N/A");
                    }
                }
            }
        },
        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                tx.setText("N/A");
            }
        });

        mQueue.add(request);
    }

    void startRepeatingTask()
    {
        mStatusChecker.run();
    }
}
