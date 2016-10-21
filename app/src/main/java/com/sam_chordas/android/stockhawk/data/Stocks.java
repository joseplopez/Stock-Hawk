package com.sam_chordas.android.stockhawk.data;

/**
 * Created by jple on 12/10/16.
 * 
 */

public class Stocks {
    public String id;
    public String symbol;
    public String percent_change;
    public String change;
    public String bid_price;
    public String created;
    public String is_up;
    public String is_current;

    public Stocks(String id, String symbol, String percent_change, String change, String bid_price, String created, String is_up, String is_current) {
        this.id = id;
        this.symbol = symbol;
        this.percent_change = percent_change;
        this.change = change;
        this.bid_price = bid_price;
        this.created = created;
        this.is_up = is_up;
        this.is_current = is_current;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPercent_change() {
        return percent_change;
    }

    public void setPercent_change(String percent_change) {
        this.percent_change = percent_change;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getBid_price() {
        return bid_price;
    }

    public void setBid_price(String bid_price) {
        this.bid_price = bid_price;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getIs_up() {
        return is_up;
    }

    public void setIs_up(String is_up) {
        this.is_up = is_up;
    }

    public String getIs_current() {
        return is_current;
    }

    public void setIs_current(String is_current) {
        this.is_current = is_current;
    }
}
