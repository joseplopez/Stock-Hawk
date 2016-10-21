package com.sam_chordas.android.stockhawk.service;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by jple on 13/10/16.
 */

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsService.RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[] { "Distinct " + QuoteColumns.SYMBOL ,
                                QuoteColumns.BIDPRICE, QuoteColumns.CHANGE, QuoteColumns.ISUP, QuoteColumns._ID},
                        QuoteColumns.SYMBOL+" IS NOT NULL) GROUP BY ("+QuoteColumns.SYMBOL,
                        null, null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.list_item_quote);

                String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
                String bidPrice = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
                String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
                int isUp = data.getInt(data.getColumnIndex(QuoteColumns.ISUP));

                views.setTextViewText(R.id.stock_symbol, symbol);
                views.setTextViewText(R.id.bid_price, bidPrice);
                views.setTextViewText(R.id.change, change);

                if (isUp == 1){
                    views.setInt(R.id.change, "setBackgroundResource", R.color.material_green_700);

                } else{
                    views.setInt(R.id.change, "setBackgroundResource", R.color.material_red_700);
                }

                Bundle extras = new Bundle();
                extras.putString(MyStocksActivity.SYMBOL, symbol);

                final Intent intent = new Intent();
                intent.putExtras(extras);
                views.setOnClickFillInIntent(R.id.item_list, intent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return Long.parseLong(data.getString(data.getColumnIndex(QuoteColumns._ID)));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}