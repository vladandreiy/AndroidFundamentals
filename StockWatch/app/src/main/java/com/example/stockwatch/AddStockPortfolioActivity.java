package com.example.stockwatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddStockPortfolioActivity extends AppCompatActivity {
    private final String TAG = AddStockPortfolioActivity.this.getClass().getSimpleName();

    TextInputLayout ticker;
    TextInputLayout price;
    TextInputLayout shares;
    TextInputLayout date;
    AppCompatButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock_portfolio);
        ticker = (TextInputLayout) findViewById(R.id.TickerField);
        price = (TextInputLayout) findViewById(R.id.PriceField);
        shares = (TextInputLayout) findViewById(R.id.SharesField);
        date = (TextInputLayout) findViewById(R.id.DateField);
        addButton = (AppCompatButton) findViewById(R.id.addButton);

        final Calendar myCalendar = Calendar.getInstance();
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        date.getEditText().setText(materialDatePicker.getHeaderText());
                        // in the above statement, getHeaderText
                        // is the selected date preview from the
                        // dialog
                    }
                });
    }

    public void addStockToPortfolio(View view) throws ParseException {
        String tickerName = ticker.getEditText().getText().toString();
        Double pricePaid = Double.parseDouble(price.getEditText().getText().toString());
        Double sharesBought = Double.parseDouble(shares.getEditText().getText().toString());
        Log.d(TAG, date.getEditText().getText().toString());
        Date dateBought = stringToDate(date.getEditText().getText().toString(), "MMM dd, yyyy");
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();
        db.portfolioStockDao().insertAll(new PortfolioStock(tickerName, pricePaid, sharesBought, dateBought));
        startActivity(new Intent(AddStockPortfolioActivity.this, MainActivity.class));

    }

    private Date stringToDate(String date, String format) throws ParseException {
        if (date == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
        Date stringDate = simpledateformat.parse(date);
        return stringDate;

    }

}