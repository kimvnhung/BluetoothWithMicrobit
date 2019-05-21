package com.hung.kimvan.bluetoothwithmicrobit.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hung.kimvan.bluetoothwithmicrobit.R;
import com.hung.kimvan.bluetoothwithmicrobit.activity.ControlActivity;
import com.hung.kimvan.bluetoothwithmicrobit.help.Constants;
import com.hung.kimvan.bluetoothwithmicrobit.help.Utility;

public class BasicFragment extends Fragment implements View.OnClickListener{
    Button sendBtn;
    EditText sendEdt;
    TextView receiveTv;


    int range = 10;
    int count =0;

    BasicFragmentListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basic_fragment,container,false);

        sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);
        sendEdt = view.findViewById(R.id.send_edt);
        receiveTv = view.findViewById(R.id.receive_tv);
        Log.d(Constants.TAG,"getActivity()"+getActivity().getClass());

        mHandler = new Handler();


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (BasicFragmentListener) context;
        }catch (ClassCastException ex){

        }
    }


    private int mInterval = 25;
    Handler mHandler ;
    Runnable autoSend = new Runnable() {
        @Override
        public void run() {
            try{
                int value = (int) (Math.random()*range);
                Log.d(Constants.TAG,"AutoSend : "+value);
                listener.sendContent(""+value);
                count++;
            }finally {
                if (count <10){
                    mHandler.postDelayed(autoSend,mInterval);
                }else {
                    count = 0;
                }
            }
        }
    };


    @Override
    public void onClick(View v) {
        if (v == sendBtn){
            listener.sendContent(sendEdt.getText().toString());
            //autoSend.run();
        }
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(autoSend);
    }

    public void setReceiveContent(String content){
        receiveTv.setText(content);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    public interface BasicFragmentListener{
        void sendContent(String content);
    }

}
