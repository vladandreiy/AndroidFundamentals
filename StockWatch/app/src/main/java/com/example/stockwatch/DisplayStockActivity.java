package com.example.stockwatch;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.Map;

public class DisplayStockActivity extends AppCompatActivity {

    private final String TAG = DisplayStockActivity.this.getClass().getSimpleName();
    private static final Map<String, String> rangeInterval = Map.of("1m", "1d", "2m", "1d", "5m", "1d", "15m", "5d", "1h", "1mo", "1d", "3mo");

    TextView stockSymbolTV;
    TextView stockNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stock);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String stockSymbol = extras.getString("StockSymbol");
            String stockLongName = extras.getString("StockLongName");
            stockSymbolTV = findViewById(R.id.StockSymbolTV);
            stockNameTV = findViewById(R.id.StockNameTV);
            stockSymbolTV.setText(stockSymbol);
            stockNameTV.setText(stockLongName);
            String chartQuery = "stock/v2/get-chart?interval=5m&symbol=" + stockSymbol + "&range=1d" + "&region=US";
            new RetrieveStockInfoTask(DisplayStockActivity.this).execute(chartQuery);
            MaterialButtonToggleGroup intervalTB = findViewById(R.id.IntervalTB);
            intervalTB.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    String interval = getResources().getResourceEntryName(checkedId).replace("button", "");
                    String range = rangeInterval.get(interval);
                    Log.d(TAG, "Interval changed:" + interval + ", range:" + range + "\n");
                    String newQuery = "stock/v2/get-chart?interval=" + interval + "&symbol=" + stockSymbol + "&range=" + range + "&region=US";
                    new RetrieveStockInfoTask(DisplayStockActivity.this).execute(newQuery);
                }
            });
        }
    }
}