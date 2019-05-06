package com.hung.kimvan.bluetoothwithmicrobit;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class AdvanceFragment extends Fragment implements View.OnClickListener{
    Button upBt,downBt,rightBt,leftBt,stopBt,p0,p1,p2,p3;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.advanced_fragment,container,false);

        upBt = view.findViewById(R.id.up_btn);
        upBt.setOnClickListener(this);
        downBt = view.findViewById(R.id.down_btn);
        downBt.setOnClickListener(this);
        leftBt = view.findViewById(R.id.left_btn);
        leftBt.setOnClickListener(this);
        rightBt = view.findViewById(R.id.right_btn);
        rightBt.setOnClickListener(this);
        stopBt = view.findViewById(R.id.stop_btn);
        stopBt.setOnClickListener(this);
        p0 = view.findViewById(R.id.p0);
        p0.setOnClickListener(this);
        p1 = view.findViewById(R.id.p1);
        p1.setOnClickListener(this);
        p2 = view.findViewById(R.id.p2);
        p2.setOnClickListener(this);
        p3 = view.findViewById(R.id.p3);
        p3.setOnClickListener(this);


        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if (v == upBt){
            Toast.makeText(getContext(),"Up Button is clicked!",Toast.LENGTH_LONG).show();
            upBt.setBackground(getContext().getDrawable(R.drawable.up_selector));
            upBt.setBackground(getContext().getDrawable(R.drawable.up_arrow));
        }
        if (v == downBt){
            Toast.makeText(getContext(),"Down Button is clicked!",Toast.LENGTH_LONG).show();

        }
        if (v == rightBt){
            Toast.makeText(getContext(),"Right Button is clicked!",Toast.LENGTH_LONG).show();
            rightBt.setBackground(getContext().getDrawable(R.drawable.right_selector));
            rightBt.setBackground(getContext().getDrawable(R.drawable.right_arrow));
        }
        if (v == leftBt){
            Toast.makeText(getContext(),"Left Button is clicked!",Toast.LENGTH_LONG).show();
            leftBt.setBackground(getContext().getDrawable(R.drawable.left_selector));
            leftBt.setBackground(getContext().getDrawable(R.drawable.left_arrow));
        }
        if (v == stopBt){
            Toast.makeText(getContext(),"Stop Button is clicked!",Toast.LENGTH_LONG).show();
            stopBt.setBackground(getContext().getDrawable(R.drawable.stop_selector));
            stopBt.setBackground(getContext().getDrawable(R.drawable.stop_btn));
        }
        if (v == p0){
            Toast.makeText(getContext(),"P0 Button is clicked!",Toast.LENGTH_LONG).show();
            p0.setBackground(getContext().getDrawable(R.drawable.p0_pressed));
            p0.setBackground(getContext().getDrawable(R.drawable.p0_btn));
        }
        if (v == p1){
            Toast.makeText(getContext(),"p1 Button is clicked!",Toast.LENGTH_LONG).show();
            p1.setBackground(getContext().getDrawable(R.drawable.p1_pressed));
            p1.setBackground(getContext().getDrawable(R.drawable.p1_btn));
        }
        if (v == p2){
            Toast.makeText(getContext(),"p2 Button is clicked!",Toast.LENGTH_LONG).show();
            p2.setBackground(getContext().getDrawable(R.drawable.p2_pressed));
            p2.setBackground(getContext().getDrawable(R.drawable.p2_btn));
        }
        if (v == p3){
            Toast.makeText(getContext(),"p3 Button is clicked!",Toast.LENGTH_LONG).show();
            p3.setBackground(getContext().getDrawable(R.drawable.p3_pressed));
            p3.setBackground(getContext().getDrawable(R.drawable.p3_btn));
        }
    }

}
