package com.hung.kimvan.bluetoothwithmicrobit.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hung.kimvan.bluetoothwithmicrobit.R;

public class BasicFragment extends Fragment implements View.OnClickListener {
    Button sendBtn;
    EditText sendEdt;
    TextView receiveTv;

    TextView status;

    BasicFragmentListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basic_fragment,container,false);

        sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);
        sendEdt = view.findViewById(R.id.send_edt);
        receiveTv = view.findViewById(R.id.receive_tv);
        RelativeLayout statusLayout = view.findViewById(R.id.status_layout);
        status = statusLayout.findViewById(R.id.message);
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

    public void setStatus(Spanned content){
        status.setText(content);
    }

    @Override
    public void onClick(View v) {
        if (v == sendBtn){
            listener.sendContent(sendEdt.getText().toString());
        }
    }

    public void setReceiveContent(String content){
        receiveTv.setText(content);
    }

    public interface BasicFragmentListener{
        void sendContent(String content);
    }
}
