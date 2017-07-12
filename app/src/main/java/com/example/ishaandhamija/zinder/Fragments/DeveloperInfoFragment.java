package com.example.ishaandhamija.zinder.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishaandhamija.zinder.Activities.DashboardActivity;
import com.example.ishaandhamija.zinder.R;

/**
 * Created by ishaandhamija on 12/07/17.
 */

public class DeveloperInfoFragment extends Fragment {

    Context ctx;
    TextView mail;
    ImageView fb, gh, li;

    public DeveloperInfoFragment(Context ctx){
        this.ctx = ctx;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_developer_info, container, false);

        ((DashboardActivity) getActivity()).setActionBarTitle("Developer's Info");

        mail = (TextView) rootView.findViewById(R.id.mail);
        fb = (ImageView) rootView.findViewById(R.id.fb);
        gh = (ImageView) rootView.findViewById(R.id.gh);
        li = (ImageView) rootView.findViewById(R.id.li);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage("https://www.facebook.com/ishaan.dhamija.5");
            }
        });

        gh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage("https://github.com/ishaandhamija");
            }
        });

        li.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage("https://www.linkedin.com/in/ishaan-dhamija-108ba385/");
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        return rootView;
    }

    void openWebPage(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    void sendEmail(){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL  , new String[]{"ishaandhamija@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Query");
        startActivity(Intent.createChooser(email, "Send mail..."));
    }

}
