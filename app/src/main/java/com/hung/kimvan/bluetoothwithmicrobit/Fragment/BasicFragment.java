package com.hung.kimvan.bluetoothwithmicrobit.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hung.kimvan.bluetoothwithmicrobit.R;

public class BasicFragment extends Fragment implements View.OnClickListener {
    Button sendBtn;
    EditText sendEdt;
    TextView receiveTv;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basic_fragment,container,false);

        sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);
        sendEdt = view.findViewById(R.id.send_edt);
        receiveTv = view.findViewById(R.id.receive_tv);
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == sendBtn){
            receiveTv.setText(sendEdt.getText());
        }
    }
}
