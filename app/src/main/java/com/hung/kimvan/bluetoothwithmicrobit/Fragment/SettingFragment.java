package com.hung.kimvan.bluetoothwithmicrobit.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hung.kimvan.bluetoothwithmicrobit.R;
import com.hung.kimvan.bluetoothwithmicrobit.help.Constants;


public class SettingFragment extends Fragment implements View.OnClickListener {
    EditText forward,backward,right,left,stop;
    EditText rightF,leftF,rightB,leftB;
    EditText p0,p1,p2,p3;

    Button save;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment,container,false);
        
        forward = view.findViewById(R.id.up_setting_edt);
        backward = view.findViewById(R.id.down_setting_edt);
        stop = view.findViewById(R.id.stop_setting_edt);

        right = view.findViewById(R.id.right_setting_edt);
        left = view.findViewById(R.id.left_setting_edt);
        rightF = view.findViewById(R.id.right_forward_setting_edt);

        leftF = view.findViewById(R.id.left_forward_setting_edt);
        rightB = view.findViewById(R.id.right_backward_setting_edt);
        leftB = view.findViewById(R.id.left_backward_setting_edt);
        
        p0 = view.findViewById(R.id.p0_setting_edt);
        p1 = view.findViewById(R.id.p1_setting_edt);
        p2 = view.findViewById(R.id.p2_setting_edt);
        p3 = view.findViewById(R.id.p3_setting_edt);

        save = view.findViewById(R.id.save_btn);
        save.setOnClickListener(this);
        Log.d(Constants.TAG,"getActivity()"+getActivity().getClass());

        loadSetting();
        Log.d(Constants.TAG,"onCreateView() "+this.getClass());
        return view;
    }

    private void loadSetting() {
        //getting preferences
        SharedPreferences prefs = this.getActivity().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        if (prefs == null){
            forward.setText("0");
            backward.setText("1");
            stop.setText("2");

            right.setText("3");
            left.setText("4");
            rightF.setText("5");

            leftF.setText("6");
            rightB.setText("7");
            leftB.setText("8");

            p0.setText("9");
            p1.setText("10");
            p2.setText("11");
            p3.setText("12");
        }else {
            forward.setText(prefs.getString(getString(R.string.forward_pref),"0"));
            backward.setText(prefs.getString(getString(R.string.backward_pref),"1"));
            stop.setText(prefs.getString(getString(R.string.stop_pref),"2"));

            right.setText(prefs.getString(getString(R.string.right_pref),"3"));
            left.setText(prefs.getString(getString(R.string.left_pref),"4"));
            rightF.setText(prefs.getString(getString(R.string.rightf_pref),"5"));

            leftF.setText(prefs.getString(getString(R.string.leftf_pref),"6"));
            rightB.setText(prefs.getString(getString(R.string.rightb_pref),"7"));
            leftB.setText(prefs.getString(getString(R.string.leftb_pref),"8"));

            p0.setText(prefs.getString(getString(R.string.p0_pref),"9"));
            p1.setText(prefs.getString(getString(R.string.p1_pref),"10"));
            p2.setText(prefs.getString(getString(R.string.p2_pref),"11"));
            p3.setText(prefs.getString(getString(R.string.p3_pref),"12"));
        }
    }

    private void saveData() {
        String forwards = forward.getText().toString();
        String backwards = backward.getText().toString();
        String stops = stop.getText().toString();

        String rights = right.getText().toString();
        String lefts = left.getText().toString();
        String rightFs = rightF.getText().toString();

        String leftFs = leftF.getText().toString();
        String rightBs = rightB.getText().toString();
        String leftBs = leftB.getText().toString();
        
        String p0s = p0.getText().toString();
        String p1s = p1.getText().toString();
        String p2s = p2.getText().toString();
        String p3s = p3.getText().toString();

        String[] checks = new String[]{forwards,backwards,stops,rights,lefts,rightFs,leftFs,rightBs,leftBs,p0s,p1s,p2s,p3s};

        boolean isRepeated = isRepeated(checks);
        if (isRepeated){
            Toast.makeText(this.getActivity(),"Thông số đã bị trùng!",Toast.LENGTH_SHORT).show();
        }else {
            //setting preferences
            SharedPreferences prefs = this.getActivity().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.forward_pref), forwards);
            editor.putString(getString(R.string.backward_pref),backwards);
            editor.putString(getString(R.string.stop_pref),stops);

            editor.putString(getString(R.string.right_pref),rights);
            editor.putString(getString(R.string.left_pref), lefts);
            editor.putString(getString(R.string.rightf_pref), rightFs);

            editor.putString(getString(R.string.leftf_pref), leftFs);
            editor.putString(getString(R.string.rightb_pref), rightBs);
            editor.putString(getString(R.string.leftb_pref), leftBs);

            editor.putString(getString(R.string.p0_pref), p0s);
            editor.putString(getString(R.string.p1_pref), p1s);
            editor.putString(getString(R.string.p2_pref), p2s);
            editor.putString(getString(R.string.p3_pref), p3s);

            editor.apply();
            Toast.makeText(this.getActivity(),"Thông tin đã được lưu",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isRepeated(String[] checks) {
        for (int i=0;i<checks.length-1;i++){
            for (int j=i+1;j<checks.length;j++){
                if (checks[i].equals(checks[j])){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == save){
            saveData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getActivity() != null){
            if (isVisibleToUser){
                loadSetting();
            }
        }
    }
}
