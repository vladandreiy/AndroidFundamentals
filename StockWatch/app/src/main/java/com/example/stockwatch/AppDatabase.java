package com.example.stockwatch;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {PortfolioStock.class}, version = 1)
@TypeConverters({DatabaseConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PortfolioStockDao portfolioStockDao();
}
