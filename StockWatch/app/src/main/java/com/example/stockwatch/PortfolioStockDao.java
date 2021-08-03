package com.example.stockwatch;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.stockwatch.PortfolioStock;

import java.util.List;

@Dao
public interface PortfolioStockDao {

    @Query("SELECT * FROM portfoliostock")
    List<PortfolioStock> getAllStocks();

    @Insert
    void insertAll(PortfolioStock... portfolioStock);

}
