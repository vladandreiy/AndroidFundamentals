package com.example.stockwatch;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class PortfolioStock {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String tickerName;

    @ColumnInfo(name = "price_paid")
    private Double pricePaid;

    @ColumnInfo(name = "shares_bought")
    private Double sharesBought;

    @ColumnInfo(name = "date_bought")
    private Date dateBought;

    public PortfolioStock(String tickerName, Double pricePaid, Double sharesBought, Date dateBought) {
        this.tickerName = tickerName;
        this.pricePaid = pricePaid;
        this.sharesBought = sharesBought;
        this.dateBought = dateBought;
    }

    public PortfolioStock(String tickerName, Double pricePaid, Double sharesBought, String dateBought) {
        this.tickerName = tickerName;
        this.pricePaid = pricePaid;
        this.sharesBought = sharesBought;
        this.dateBought = stringToDate(dateBought, "dd-MM-yy");
    }

    public Double getSharesBought() {
        return sharesBought;
    }

    public void setSharesBought(Double sharesBought) {
        this.sharesBought = sharesBought;
    }

    public String getTickerName() {
        return tickerName;
    }

    public void setTickerName(String tickerName) {
        this.tickerName = tickerName;
    }

    public Double getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(Double pricePaid) {
        this.pricePaid = pricePaid;
    }

    public Date getDateBought() {
        return dateBought;
    }

    public void setDateBought(Date dateBought) {
        this.dateBought = dateBought;
    }

    @Override
    public String toString() {
        return tickerName + '\'' +
                ", pricePaid=" + pricePaid +
                ", dateBought=" + dateBought;
    }

    private Date stringToDate(String date, String format) {
        if (date == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
        Date stringDate = simpledateformat.parse(date, pos);
        return stringDate;
    }
}
