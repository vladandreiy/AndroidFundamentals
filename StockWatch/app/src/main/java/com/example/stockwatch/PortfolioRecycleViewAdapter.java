package com.example.stockwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PortfolioRecycleViewAdapter extends RecyclerView.Adapter<PortfolioRecycleViewAdapter.ViewHolder> {

    private List<PortfolioStock> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    PortfolioRecycleViewAdapter(Context context, List<PortfolioStock> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.portfolio_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PortfolioStock portfolioStock = mData.get(position);
        holder.stockName.setText(portfolioStock.getTickerName());
        holder.price.setText("Average price: " + String.format(Locale.getDefault(), "%,.2f", portfolioStock.getPricePaid()));
        holder.shares.setText("Shares bought: " + String.format(Locale.getDefault(), "%,.2f", portfolioStock.getSharesBought()));

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView stockName;
        TextView shares;
        TextView price;

        ViewHolder(View itemView) {
            super(itemView);
            stockName = itemView.findViewById(R.id.StockNameRV);
            shares = itemView.findViewById(R.id.StockSharesRV);
            price = itemView.findViewById(R.id.StockPriceRV);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    PortfolioStock getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
