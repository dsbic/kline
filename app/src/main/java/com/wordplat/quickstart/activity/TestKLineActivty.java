package com.wordplat.quickstart.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wordplat.ikvstockchart.InteractiveKLineView;
import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.entry.StockDataTest;
import com.wordplat.ikvstockchart.render.TimeLineRender;
import com.wordplat.quickstart.R;
import com.wordplat.quickstart.bean.BtcBean;
import com.wordplat.quickstart.mvp.BtcChinaPresenter;
import com.wordplat.quickstart.mvp.LoadingViewListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.InputStream;

/**
 * Created by adminstartor on 2018/4/27.
 */
@ContentView(R.layout.activtiy_test_kline)
public class TestKLineActivty extends BaseActivity {
    @ViewInject(R.id.timeLineView) private InteractiveKLineView timeLineView = null;

    private static final int REQUEST_BTC_DATA = 1;

    private EntrySet entrySet = new EntrySet();

    private final BtcChinaPresenter presenter = new BtcChinaPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.attachView(btcChinaListener);
        presenter.getSimple(REQUEST_BTC_DATA);
    }

    private void loadData()
    {
        String kLineData = "";
        try {
            InputStream in = getResources().getAssets().open("kline1.txt");
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            kLineData = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        entrySet = StockDataTest.parseKLineData(kLineData);
        timeLineView.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();

        presenter.detachView();
    }

    private void initUI() {
        timeLineView.setEntrySet(entrySet);
        timeLineView.setRender(new TimeLineRender());
    }

    private LoadingViewListener btcChinaListener = new LoadingViewListener() {
        @Override
        public void onStartRequest(int requestCode) {

        }

        @Override
        public void onFinishRequest(int requestCode) {

        }

        @Override
        public void onSuccess(int requestCode) {
            for (BtcBean btcBean : presenter.getBtcList()) {
                Entry entry = new Entry(btcBean.getPrice(), (int) btcBean.getAmount(), "");
                entrySet.addEntry(entry);
            }
            entrySet.getEntryList().get(0).setXLabel("09:30");
            entrySet.getEntryList().get(2).setXLabel("11:30/13:00");
            entrySet.getEntryList().get(4).setXLabel("15:00");

            timeLineView.notifyDataSetChanged();
        }

        @Override
        public void onResultEmpty(int requestCode) {
            entrySet.setLoadingStatus(false);
            timeLineView.notifyDataSetChanged();
        }

        @Override
        public void onNoNetworkError(int requestCode) {
            super.onNoNetworkError(requestCode);
            onResultEmpty(requestCode);
        }

        @Override
        public void onNetworkTimeOutError(int requestCode) {
            super.onNetworkTimeOutError(requestCode);
            onResultEmpty(requestCode);
        }
    };

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, Simple_TimeLine_Example_Activity.class);
        return intent;
    }
}
