package com.palprotech.heylaapp.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.palprotech.heylaapp.R;
import com.palprotech.heylaapp.activity.EventSharingPointActivity;
import com.palprotech.heylaapp.activity.LoginPointsActivity;
import com.palprotech.heylaapp.activity.PointTableActivity;
import com.palprotech.heylaapp.customview.CircleImageView;
import com.palprotech.heylaapp.helper.AlertDialogHelper;
import com.palprotech.heylaapp.helper.ProgressDialogHelper;
import com.palprotech.heylaapp.servicehelpers.ServiceHelper;
import com.palprotech.heylaapp.serviceinterfaces.IServiceListener;
import com.palprotech.heylaapp.utils.HeylaAppConstants;
import com.palprotech.heylaapp.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Narendar on 28/10/17.
 */

public class LeaderboardFragment extends Fragment implements View.OnClickListener, IServiceListener {

    View rootView;
    CircleImageView profileImg;
    Context context;
    Button follow;
    Handler mHandler = new Handler();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    TextView viewFullStatistics, loginCount, loginPoints, shareCount, sharePoints, checkinCount;
    TextView checkinPoints, bookingCount, bookingPoints, reviewCount, reviewPoints, totalPoints;
    TextView name, username;
    RelativeLayout login, share, check_in, booking, reviews, profile, points;
    ImageView userPic;

   /* @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        initializeHelpers();
        initializeViews();
        setHasOptionsMenu(true);

        return rootView;
    }

    protected void initializeViews() {

        name = rootView.findViewById(R.id.name);
        if (PreferenceStorage.getFullName(getActivity()) == null) {
            name.setText("Name");
        } else {
            name.setText(PreferenceStorage.getFullName(getActivity()));
        }
        username = rootView.findViewById(R.id.username);
        if (PreferenceStorage.getUsername(getActivity()) == null) {
            username.setText("Username");
        } else {
            username.setText(PreferenceStorage.getUsername(getActivity()));
        }
        userPic = rootView.findViewById(R.id.leaderboard_profile_img);
        profile = rootView.findViewById(R.id.edit_profile);
//        profile.setOnClickListener(this);
        String url = PreferenceStorage.getUserPicture(getActivity());
        String getSocialUrl = PreferenceStorage.getSocialNetworkProfileUrl(getActivity());
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.get().load(url).placeholder(R.drawable.ic_default_profile).error(R.drawable.ic_default_profile).into(userPic);
        } else if (((getSocialUrl != null) && !(getSocialUrl.isEmpty()))) {
            Picasso.get().load(getSocialUrl).placeholder(R.drawable.ic_default_profile).error(R.drawable.ic_default_profile).into(userPic);
        }

        login = rootView.findViewById(R.id.login_layout);
        login.setOnClickListener(this);
//        loginCount = rootView.findViewById(R.id.login_count);
        loginPoints = rootView.findViewById(R.id.login_star_count);


        share = rootView.findViewById(R.id.sharing_layout);
        share.setOnClickListener(this);
//        shareCount = rootView.findViewById(R.id.sharing_count);
        sharePoints = rootView.findViewById(R.id.share_star_count);


        check_in = rootView.findViewById(R.id.check_in_layout);
        check_in.setOnClickListener(this);
//        checkinCount = rootView.findViewById(R.id.check_in_count);
        checkinPoints = rootView.findViewById(R.id.check_in_star_count);

        reviews = rootView.findViewById(R.id.review_layout);
        reviews.setOnClickListener(this);
//        reviewCount = rootView.findViewById(R.id.review_count);
        reviewPoints = rootView.findViewById(R.id.review_star_count);

        booking = rootView.findViewById(R.id.booking_layout);
        booking.setOnClickListener(this);
//        bookingCount = rootView.findViewById(R.id.booking_count);
        bookingPoints = rootView.findViewById(R.id.booking_star_count);

        totalPoints = rootView.findViewById(R.id.total_points);
        points = rootView.findViewById(R.id.point_table);
        points.setOnClickListener(this);
    }

    protected void initializeHelpers() {
        serviceHelper = new ServiceHelper(getActivity());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());
        makeLeaderBoardServiceCall();
    }

    private void makeLeaderBoardServiceCall() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(HeylaAppConstants.KEY_USER_ID, PreferenceStorage.getUserId(getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = HeylaAppConstants.BASE_URL + HeylaAppConstants.LEADER_BOARD;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View view) {
        if (view == profile) {
//            Intent homeIntent = new Intent(getActivity(), ProfileActivity.class);
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(homeIntent);
//            getActivity().finish();
        } else if (view == login) {
            if (loginPoints.getText().toString().equalsIgnoreCase("(0)")) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Daily Login");
                alertDialogBuilder.setMessage("No points earned yet");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                alertDialogBuilder.show();
            } else {
                Intent homeIntent = new Intent(getActivity(), LoginPointsActivity.class);

//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                homeIntent.putExtra("rule_id", "1");
                startActivity(homeIntent);
//            getActivity().finish();
            }
        } else if (view == share) {
            if (sharePoints.getText().toString().equalsIgnoreCase("(0)")) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Sharing Events");
                alertDialogBuilder.setMessage("No points earned yet");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                alertDialogBuilder.show();
            } else {
                Intent homeIntent = new Intent(getActivity(), EventSharingPointActivity.class);
//                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                homeIntent.putExtra("rule_id", "2");
                startActivity(homeIntent);
//            getActivity().finish();
            }
//
        } else if (view == check_in) {
            if (checkinPoints.getText().toString().equalsIgnoreCase("(0)")) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Event Check-ins");
                alertDialogBuilder.setMessage("No points earned yet");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                alertDialogBuilder.show();
            } else {
                Intent homeIntent = new Intent(getActivity(), EventSharingPointActivity.class);
                homeIntent.putExtra("rule_id", "3");
                startActivity(homeIntent);
            }
/*            Intent homeIntent = new Intent(getActivity(), EventCheckInPointActivity.class);
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
//            getActivity().finish();*/
//            getActivity().finish();
        } else if (view == reviews) {
            if (reviewPoints.getText().toString().equalsIgnoreCase("(0)")) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Event Reviews");
                alertDialogBuilder.setMessage("No points earned yet");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                alertDialogBuilder.show();
            } else {
                Intent homeIntent = new Intent(getActivity(), EventSharingPointActivity.class);
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                homeIntent.putExtra("rule_id", "4");
                startActivity(homeIntent);
//            getActivity().finish();
            }

        } else if (view == booking) {
            if (bookingPoints.getText().toString().equalsIgnoreCase("(0)")) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Event Bookings");
                alertDialogBuilder.setMessage("No points earned yet");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                alertDialogBuilder.show();
            } else {
                Intent homeIntent = new Intent(getActivity(), EventSharingPointActivity.class);
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                homeIntent.putExtra("rule_id", "5");
                startActivity(homeIntent);
//            getActivity().finish();
            }
        } else if (view == points) {
            startActivity(new Intent(getActivity(), PointTableActivity.class));
        }
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu != null) {
            menu.findItem(R.id.action_filter).setVisible(false);
            menu.findItem(R.id.action_search_view).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public void onResponse(final JSONObject response) {

        progressDialogHelper.hideProgressDialog();
        try {

            String lCount, lPoint, cCount, cPoint, bCount, bPoint, sCount, sPoint, tPoint, rCount, rPoint;

            final JSONArray getData = response.getJSONArray("Leaderboard");

            lCount = getData.getJSONObject(0).getString("login_count");
            lPoint = getData.getJSONObject(0).getString("login_points");
            cCount = getData.getJSONObject(0).getString("checkin_count");
            cPoint = getData.getJSONObject(0).getString("checkin_points");
            bCount = getData.getJSONObject(0).getString("booking_count");
            bPoint = getData.getJSONObject(0).getString("booking_points");
            sCount = getData.getJSONObject(0).getString("sharing_count");
            sPoint = getData.getJSONObject(0).getString("sharing_points");
            tPoint = getData.getJSONObject(0).getString("total_points");
            rCount = getData.getJSONObject(0).getString("review_count");
            rPoint = getData.getJSONObject(0).getString("review_points");

//            loginCount.setText("(" + lCount + ")");
            loginPoints.setText("(" + lPoint + ")");
//            shareCount.setText("(" + sCount + ")");
            sharePoints.setText("(" + sPoint + ")");
//            checkinCount.setText("(" + cCount + ")");
            checkinPoints.setText("(" + cPoint + ")");
//            reviewCount.setText("(" + rCount + ")");
            reviewPoints.setText("(" + rPoint + ")");
//            bookingCount.setText("(" + bCount + ")");
            bookingPoints.setText("(" + bPoint + ")");
            totalPoints.setText("(" + tPoint + ")");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(final String error) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                AlertDialogHelper.showSimpleAlertDialog(getApplicationContext(), error);
            }
        });

    }
}
