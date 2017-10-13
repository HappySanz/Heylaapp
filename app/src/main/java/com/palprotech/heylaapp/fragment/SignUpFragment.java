package com.palprotech.heylaapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.palprotech.heylaapp.R;

/**
 * Created by Narendar on 06/10/17.
 */

public class SignUpFragment extends Fragment {

    EditText mobile, password, email;
    View rootView;
    Button signup;

    public static SignUpFragment newInstance(int position) {
        SignUpFragment frag = new SignUpFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initializeViews();        return rootView;
    }

    protected void initializeViews() {
        email = rootView.findViewById(R.id.mail);
        mobile = rootView.findViewById(R.id.mobilenumber);
        password = rootView.findViewById(R.id.password);
        signup = rootView.findViewById(R.id.signup);
    }
}
