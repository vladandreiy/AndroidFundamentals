package com.example.stockwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.room.Room;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RetrieveStockInfoTask extends AsyncTask<String, Void, Response> {
    private Activity activity;
    private final String TAG = RetrieveStockInfoTask.this.getClass().getSimpleName();

    public RetrieveStockInfoTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Response doInBackground(String... info) {
        if (info[0] == null)
            return null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://apidojo-yahoo-finance-v1.p.rapidapi.com/" + info[0])
                .get()
                .addHeader("x-rapidapi-key", "914356c683msh869064b7cf3f32bp1ca759jsn30a6e9d42022")
                .addHeader("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                .build();
        Response response = null;
        Log.d(TAG, "Request sent" + request.url());
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Response received" + response.body().toString());
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response.body().string());
            if (activity.getClass().getName().equals(MainActivity.class.getName())) {
                if (jsonObject.has("quoteResponse")) {
                    JSONArray result = jsonObject.getJSONObject("quoteResponse").getJSONArray("result");
                    AppDatabase db = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "production")
                            .allowMainThreadQueries()
                            .build();
                    List<PortfolioStock> portfolioStocks = db.portfolioStockDao().getAllStocks();
                    TextView holdings = activity.findViewById(R.id.HoldingsValueTV);
                    Double holdingsValue = 0.0;
                    TextView dayGain = activity.findViewById(R.id.DayGainNumberTV);
                    Double dayGainValue = 0.0;
                    TextView totalGain = activity.findViewById(R.id.TotalGainNumberTV);
                    Double totalGainValue = 0.0;
                    Double totalInvested = 0.0;
                    Double dayChange = 0.0;
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject stockQuote = result.getJSONObject(i);
                        Double currentPrice = stockQuote.getDouble("regularMarketPrice");
                        Double lastPrice = stockQuote.getDouble("regularMarketPreviousClose");
                        holdingsValue += portfolioStocks.get(i).getSharesBought() * currentPrice;
                        totalInvested = portfolioStocks.get(i).getPricePaid() * portfolioStocks.get(i).getSharesBought();
                        dayGainValue += (currentPrice - lastPrice) * portfolioStocks.get(i).getSharesBought();
                    }
                    totalGainValue = holdingsValue - totalInvested;
                    Double totalGainPercentage = totalGainValue / totalInvested * 100;
                    Double dayGainPercentage = dayGainValue / totalInvested * 100;
                    Double finalHoldingsValue = holdingsValue;
                    Double finalTotalGainValue = totalGainValue;
                    Double finalDayGainValue = dayGainValue;
                    Double finalTotalInvested = totalInvested;
                    activity.runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            holdings.setText("$" + String.format(Locale.getDefault(), "%,.2f", finalHoldingsValue));
                            if (finalTotalGainValue > 0)
                                totalGain.setTextColor(activity.getBaseContext().getColor(R.color.green));
                            else
                                totalGain.setTextColor(activity.getBaseContext().getColor(R.color.pink));
                            if(finalDayGainValue > 0)
                                dayGain.setTextColor(activity.getBaseContext().getColor(R.color.green));
                            else
                                dayGain.setTextColor(activity.getBaseContext().getColor(R.color.pink));
                            totalGain.setText("$" + String.format(Locale.getDefault(), "%,.2f", finalTotalGainValue)
                                    + " (" + String.format(Locale.getDefault(), "%,.2f", totalGainPercentage) + "%)");
                            dayGain.setText("$" + String.format(Locale.getDefault(), "%,.2f", finalDayGainValue)
                                    + " (" + String.format(Locale.getDefault(), "%,.2f", dayGainPercentage) + "%)");
                        }
                    });
                } else if (jsonObject.has("count")) {
                    if (jsonObject.getInt("count") <= 0) {
                    }
//                    textView.setText("Couldn't find ticker");
                    else {
                        JSONArray jsonArrayTickers = (JSONArray) jsonObject.get("quotes");
                        Intent intent = new Intent(activity, DisplayStockActivity.class);
                        String symbolFound = (String) ((JSONObject) jsonArrayTickers.get(0)).get("symbol");
                        intent.putExtra("StockSymbol", symbolFound);
                        String stockLongName = jsonArrayTickers.getJSONObject(0).getString("shortname");
                        intent.putExtra("StockLongName", stockLongName);
                        activity.startActivity(intent);
                    }
                }
            }
            if (activity.getClass().getName().equals(DisplayStockActivity.class.getName())) {
                JSONArray jsonQuoteClose = (JSONArray) ((JSONObject) ((JSONArray) ((JSONObject) ((JSONObject)
                        ((JSONArray)
                                ((JSONObject) jsonObject.get("chart")).get("result")).get(0))
                        .get("indicators")).get("quote")).get(0)).get("close");
                JSONArray jsonTimestamp = jsonObject.getJSONObject("chart").getJSONArray("result").getJSONObject(0).getJSONArray("timestamp");
                GraphView graph = (GraphView) activity.findViewById(R.id.StockGraph);
                graph.removeAllSeries();
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                double maxY = Double.NEGATIVE_INFINITY;
                double minY = Double.POSITIVE_INFINITY;
                for (int i = 0; i < jsonTimestamp.length() || i < jsonQuoteClose.length(); i++) {
                    Log.d(TAG, "Value: " + jsonQuoteClose.getDouble(i) + "\n");
                    Log.d(TAG, "Epoch: " + String.format("%15f", jsonTimestamp.getDouble(i)) + "\n");
                    Date date = new Date((long) jsonTimestamp.getDouble(i) * 1000);
                    DateFormat format = DateFormat.getDateInstance();
                    format.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                    String formatted = format.format(date);
                    Log.d(TAG, "Timestamp: " + formatted + "\n");
                    series.appendData(new DataPoint(jsonTimestamp.getDouble(i), jsonQuoteClose.getDouble(i)), false, 500);
                    if (jsonQuoteClose.getDouble(i) > maxY)
                        maxY = jsonQuoteClose.getDouble(i);
                    if (jsonQuoteClose.getDouble(i) < minY)
                        minY = jsonQuoteClose.getDouble(i);

                }
                Viewport viewport = graph.getViewport();
                viewport.setYAxisBoundsManual(true);
                viewport.setXAxisBoundsManual(true);
                viewport.setMinX(jsonTimestamp.getDouble(0));
                viewport.setMaxX(jsonTimestamp.getDouble(jsonTimestamp.length() - 1));
                viewport.setMaxY(105 / 100.0 * maxY);
                viewport.setMinY(95 / 100.0 * minY);
                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            Format formatter = new SimpleDateFormat("MM-dd HH:mm");
                            return formatter.format(value);
                        } else {
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });
                graph.getGridLabelRenderer().setNumHorizontalLabels(2);
                graph.addSeries(series);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonObject.toString());
        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
    }
}
