package com.hung.kimvan.bluetoothwithmicrobit.Fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hung.kimvan.bluetoothwithmicrobit.R;
import com.hung.kimvan.bluetoothwithmicrobit.help.Constants;

import java.util.ArrayList;

public class AdvanceFragment extends Fragment implements  View.OnTouchListener {
    Button upBt,downBt,rightBt,leftBt,stopBt,p0,p1,p2,p3;

    boolean isCountingDown = false;
    ArrayList<Integer> list ;

    private Handler mHandler;

    AdvancedListener listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.advanced_fragment,container,false);

        upBt = view.findViewById(R.id.up_btn);
        downBt = view.findViewById(R.id.down_btn);
        leftBt = view.findViewById(R.id.left_btn);
        rightBt = view.findViewById(R.id.right_btn);
        stopBt = view.findViewById(R.id.stop_btn);
        p0 = view.findViewById(R.id.p0);
        p1 = view.findViewById(R.id.p1);
        p2 = view.findViewById(R.id.p2);
        p3 = view.findViewById(R.id.p3);


        upBt.setOnTouchListener(this);
        downBt.setOnTouchListener(this);
        leftBt.setOnTouchListener(this);
        rightBt.setOnTouchListener(this);
        stopBt.setOnTouchListener(this);
        p0.setOnTouchListener(this);
        p1.setOnTouchListener(this);
        p2.setOnTouchListener(this);
        p3.setOnTouchListener(this);



        mHandler = new Handler();
        list = new ArrayList<>();
        mSingleChecker.run();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AdvancedListener) context;
        }catch (ClassCastException ex){
            Log.d(Constants.TAG,"onAttach() advancedFragment"+ex.getMessage());
        }
    }
    Runnable mSingleChecker = new Runnable() {
        @Override
        public void run() {
            try {
                setupDataForSending(); //filter data.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mSingleChecker,Constants.minCycle);
            }
        }
    };
    private void setupDataForSending() {
        String logs = "";
        int lastest = list.isEmpty()?13:list.get(0);
        for (int i=0;i<list.size();i++){
            logs+= " "+list.get(i)+",";
            if (list.get(i) != lastest){
                int sum = list.get(i)+lastest;
                int mul = list.get(i)*lastest;
                if(mul == 0){
                    if (sum == 3){
                        lastest = 5; // tiến phải
                    }
                    if (sum == 4){
                        lastest = 6;//tiến trái
                    }
                }else if (mul == 3 ){
                    lastest = 7; //lùi phải
                }else if (mul == 4 && sum == 5){
                    lastest = 8; //lùi trái
                }else {
                    lastest = 13;
                }
                break;
            }

        }
        if (lastest != 13){
            Log.d(Constants.TAG,"onResetStatus() mSingleChecker" +logs);
        }
        sendDataControl(lastest);

        list.clear();
    }

    private void sendDataControl(int lastest) {
        switch (lastest){
            case 0:
                listener.onForward();
                break;
            case 1:
                listener.onBackward();
                break;
            case 2:
                listener.onStoping();
                break;
            case 3:
                listener.onRight();
                break;
            case 4:
                listener.onLeft();
                break;
            case 5:
                listener.onRightForward();
                break;
            case 6:
                listener.onLeftForward();
                break;
            case 7:
                listener.onRightBackward();
                break;
            case 8:
                listener.onLeftBackward();
                break;
            case 9:
                listener.onP0();
                break;
            case 10:
                listener.onP1();
                break;
            case 11:
                listener.onP2();
                break;
            case 12:
                listener.onP3();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(listener != null){
            if (event.getAction() != MotionEvent.ACTION_UP){
                if (v == upBt){
                    Log.d(Constants.TAG,"onTouch() forward");
                    list.add(0);
                }
                if (v == downBt){
                    Log.d(Constants.TAG,"onTouch() backward");
                    list.add(1);
                }
                if (v == rightBt){
                    Log.d(Constants.TAG,"onTouch() right");
                    list.add(3);
                }
                if (v == leftBt){
                    Log.d(Constants.TAG,"onTouch() left");
                    list.add(4);
                }
                if (v == stopBt){
                    Log.d(Constants.TAG,"onTouch() stop");
                    list.add(2);
                }
                if (v == p0){
                    Log.d(Constants.TAG,"onTouch() p0");
                    list.add(9);
                }
                if (v == p1){
                    Log.d(Constants.TAG,"onTouch() p1");
                    list.add(10);
                }
                if (v == p2){
                    Log.d(Constants.TAG,"onTouch() p2");
                    list.add(11);
                }
                if (v == p3){
                    Log.d(Constants.TAG,"onTouch() p3");
                    list.add(12);
                }
            }
            return true;
        }
        Log.d(Constants.TAG,event.getAction()+"");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mSingleChecker);
    }

    public interface AdvancedListener{
        void onForward();
        void onBackward();
        void onStoping();

        void onRight();
        void onLeft();
        void onRightForward();

        void onLeftForward();
        void onRightBackward();
        void onLeftBackward();

        void onP0();
        void onP1();
        void onP2();
        void onP3();
    }


}
