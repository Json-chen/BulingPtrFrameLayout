package com.robert.mydemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.buling.BulingCoinFrameLayout;

import static com.robert.mydemo.R.id.frame;


public class MainActivity extends FragmentActivity {
    BulingCoinFrameLayout coinFrameLayout;
    ListView listView;
    int i = 3;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr);
        coinFrameLayout = (BulingCoinFrameLayout) findViewById(frame);
        simpleAdapter = new SimpleAdapter(this, getData(i), R.layout.item, new String[]{"text"}, new int[]{R.id.text});
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(simpleAdapter);
        coinFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                i++;
                simpleAdapter = new SimpleAdapter(MainActivity.this, getData(i), R.layout.item, new String[]{"text"}, new int[]{R.id.text});
                listView.setAdapter(simpleAdapter);
                coinFrameLayout.refreshComplete();
            }
        });
    }

    private List<Map<String, Object>> getData(int t) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < t; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("text", String.format("第%s个数",i));
            mapList.add(data);
        }
        return mapList;
    }
}
