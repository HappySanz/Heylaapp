package com.palprotech.heylaapp.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.palprotech.heylaapp.R;
import com.palprotech.heylaapp.bean.support.StoreCity;
import com.palprotech.heylaapp.bean.support.StoreCountry;
import com.palprotech.heylaapp.bean.support.StoreState;
import com.palprotech.heylaapp.helper.AlertDialogHelper;
import com.palprotech.heylaapp.helper.ProgressDialogHelper;
import com.palprotech.heylaapp.interfaces.DialogClickListener;
import com.palprotech.heylaapp.servicehelpers.ServiceHelper;
import com.palprotech.heylaapp.serviceinterfaces.IServiceListener;
import com.palprotech.heylaapp.utils.AndroidMultiPartEntity;
import com.palprotech.heylaapp.utils.CommonUtils;
import com.palprotech.heylaapp.utils.HeylaAppConstants;
import com.palprotech.heylaapp.utils.HeylaAppValidator;
import com.palprotech.heylaapp.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Narendar on 23/10/17.
 */

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = ProfileActivity.class.getName();

    private List<String> mOccupationList = new ArrayList<String>();
    private ArrayAdapter<String> mOccupationAdapter = null;

    private ArrayAdapter<String> mGenderAdapter = null;
    private List<String> mGenderList = new ArrayList<String>();

    ArrayAdapter<StoreCountry> mCountryAdapter = null;
    ArrayList<StoreCountry> countryList;

    ArrayAdapter<StoreState> mStateAdapter = null;
    ArrayList<StoreState> stateList;

    ArrayAdapter<StoreCity> mCityAdapter = null;
    ArrayList<StoreCity> cityList;

    private TextInputLayout inputGender, inputAddress1, inputAddress2, inputAddress3, inputPincode, inputName,
            inputUsername, inputBirthday, inputOccupation, inputCountry, inputState, inputCity;
    EditText mBirthday, mGender, mOccupation, address1, address2, address3, pincode, name,
            username, country, state, city;
    private CheckBox cbSubscription;
    private ImageView mProfileImage = null;
    private Uri outputFileUri;
    static final int REQUEST_IMAGE_GET = 1;
    Button save;
    private String checkProfileState = "";
    private String checkInternalState = "";
    private DatePickerDialog mDatePicker;
    private SimpleDateFormat mDateFormatter;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private String mActualFilePath = null;
    private Uri mSelectedImageUri = null;
    private Bitmap mCurrentUserImageBitmap = null;
    private ProgressDialog mProgressDialog = null;
    private String mUpdatedImageUrl = null;
    private UploadFileToServer mUploadTask = null;
    long totalSize = 0;

    String fullName = "";
    String userName = "";
    String birthDay = "";
    String occupation = "";
    String gender = "";
    String addressLineOne = "";
    String addressLineTwo = "";
    String landMark = "";
    String countryName = "";
    String countryId = "";
    String stateName = "";
    String stateId = "";
    String cityName = "";
    String cityId = "";
    String pinCode = "";
    boolean newsLetter = false;
    String newsLetterStatus = "N";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUI();
    }

    void setUI() {

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

//        Bundle extras = getIntent().getExtras();
//        checkProfileState = extras.getString("profile_state");
        checkProfileState = PreferenceStorage.getCheckFirstTimeProfile(getApplicationContext());

        String url = PreferenceStorage.getUserPicture(this);

        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).placeholder(R.drawable.ic_default_profile).error(R.drawable.ic_default_profile).into(mProfileImage);
        }

        mProfileImage = (ImageView) findViewById(R.id.profile_img);
        mProfileImage.setOnClickListener(this);
        inputName = (TextInputLayout) findViewById(R.id.ti_name);
        name = (EditText) findViewById(R.id.edtName);
        inputUsername = (TextInputLayout) findViewById(R.id.ti_username);
        username = (EditText) findViewById(R.id.edtUsername);
        inputBirthday = (TextInputLayout) findViewById(R.id.ti_birthday);
        mBirthday = (EditText) findViewById(R.id.edtBirthday);
        mBirthday.setFocusable(false);
        inputOccupation = (TextInputLayout) findViewById(R.id.ti_occupation);
        mOccupation = (EditText) findViewById(R.id.occupationlist);
        mOccupation.setFocusable(false);
        inputGender = (TextInputLayout) findViewById(R.id.ti_gender);
        mGender = (EditText) findViewById(R.id.genderList);
        mGender.setFocusable(false);
        inputAddress1 = (TextInputLayout) findViewById(R.id.ti_address_line_one);
        address1 = (EditText) findViewById(R.id.edtAddressLineOne);
        inputAddress2 = (TextInputLayout) findViewById(R.id.ti_address_line_two);
        address2 = (EditText) findViewById(R.id.edtAddressLinetwo);
        inputAddress3 = (TextInputLayout) findViewById(R.id.ti_address_line_three);
        address3 = (EditText) findViewById(R.id.edtAddressLinethree);
        inputCountry = (TextInputLayout) findViewById(R.id.ti_country);
        country = (EditText) findViewById(R.id.countryList);
        country.setOnClickListener(this);
        country.setFocusable(false);
        inputState = (TextInputLayout) findViewById(R.id.ti_state);
        state = (EditText) findViewById(R.id.stateList);
        state.setOnClickListener(this);
        state.setFocusable(false);
        inputCity = (TextInputLayout) findViewById(R.id.ti_city);
        city = (EditText) findViewById(R.id.cityList);
        city.setOnClickListener(this);
        city.setFocusable(false);
        inputPincode = (TextInputLayout) findViewById(R.id.ti_pincode);
        pincode = (EditText) findViewById(R.id.edtPincode);
        cbSubscription = (CheckBox) findViewById(R.id.subscription);
        save = (Button) findViewById(R.id.saveprofile);
        save.setOnClickListener(this);

        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        String birthdayval = PreferenceStorage.getUserBirthday(this);
        if (birthdayval != null) {

            mBirthday.setText(birthdayval);
        }

        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "birthday widget selected");
                showBirthdayDate();

            }
        });

        mGenderList.add("Male");
        mGenderList.add("Female");
        mGenderList.add("Other");
        mGenderList.add("Rather not say");

        mGenderAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mGenderList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mGenderList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        String genderVal = CommonUtils.getGenderVal(PreferenceStorage.getUserGender(this));
        if (genderVal != null) {
            mGender.setText(genderVal);
        }
        mGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderList();
            }
        });

        //occupation related data
        mOccupationList.add("Student");
        mOccupationList.add("Employed");
        mOccupationList.add("Self Employed/Business");
        mOccupationList.add("Home Maker");
        mOccupationList.add("Other");

        mOccupationAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mOccupationList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mOccupationList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        String occupation = PreferenceStorage.getUserOccupation(this);
        if (occupation != null) {
            mOccupation.setText(occupation);
        }

        mOccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOccupationList();
            }
        });

        GetCountry();
    }

    private void GetCountry() {

        checkInternalState = "country";

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(HeylaAppConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = HeylaAppConstants.BASE_URL + HeylaAppConstants.COUNTRY_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);


        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    void saveProfile() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Updating Profile");
        mProgressDialog.show();
        if ((mActualFilePath != null)) {
            Log.d(TAG, "Update profile picture");
            saveUserImage();
        } else {
            saveProfileData();
        }
    }

    private void saveUserImage() {

        mUpdatedImageUrl = null;

        new UploadFileToServer().execute();
    }

    private void saveProfileData() {

        fullName = name.getText().toString();
        userName = username.getText().toString();
        birthDay = mBirthday.getText().toString();
        occupation = mOccupation.getText().toString();
        gender = mGender.getText().toString();
        addressLineOne = address1.getText().toString();
        addressLineTwo = address2.getText().toString();
        landMark = address3.getText().toString();
        countryName = country.getText().toString();
        stateName = state.getText().toString();
        cityName = city.getText().toString();
        pinCode = pincode.getText().toString();
        newsLetter = cbSubscription.isChecked();
        if (newsLetter) {
            newsLetterStatus = "Y";
        }

        String date = mBirthday.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd HH:MM:SS");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd");
        String newFormat = formatter.format(testDate);
        System.out.println(".....Date..."+newFormat);

        if (validateFields()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(HeylaAppConstants.PARAMS_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
                jsonObject.put(HeylaAppConstants.PARAMS_FULL_NAME, fullName);
                jsonObject.put(HeylaAppConstants.PARAMS_USERNAME, userName);
                jsonObject.put(HeylaAppConstants.PARAMS_DATE_OF_BIRTH, newFormat);
                jsonObject.put(HeylaAppConstants.PARAMS_GENDER, gender);
                jsonObject.put(HeylaAppConstants.PARAMS_OCCUPATION, occupation);
                jsonObject.put(HeylaAppConstants.PARAMS_ADDRESS_LINE_1, addressLineOne);
                jsonObject.put(HeylaAppConstants.PARAMS_ADDRESS_LINE_2, addressLineTwo);
                jsonObject.put(HeylaAppConstants.PARAMS_ADRESS_LINE_3, landMark);
                jsonObject.put(HeylaAppConstants.PARAMS_COUNTRY_ID, countryId);
                jsonObject.put(HeylaAppConstants.PARAMS_STATE_ID, stateId);
                jsonObject.put(HeylaAppConstants.PARAMS_CITY_ID, cityId);
                jsonObject.put(HeylaAppConstants.PARAMS_ZIP_CODE, pinCode);
                jsonObject.put(HeylaAppConstants.PARAMS_NEWS_LETTER, newsLetterStatus);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = HeylaAppConstants.BASE_URL + HeylaAppConstants.PROFILE_DATA;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        } else {
            if (mProgressDialog != null) {
                mProgressDialog.cancel();
            }
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        private static final String TAG = "UploadFileToServer";
        private HttpClient httpclient;
        HttpPost httppost;
        public boolean isTaskAborted = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(String.format(HeylaAppConstants.BASE_URL + HeylaAppConstants.PROFILE_IMAGE + Integer.parseInt(PreferenceStorage.getUserId(ProfileActivity.this))));

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {

                            }
                        });
                Log.d(TAG, "actual file path is" + mActualFilePath);
                if (mActualFilePath != null) {

                    File sourceFile = new File(mActualFilePath);

                    // Adding file data to http body
                    //fileToUpload
                    entity.addPart("user_pic", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(ProfileActivity.this)));
//                    entity.addPart("user_type", new StringBody(PreferenceStorage.getUserType(ProfileActivity.this)));

                    totalSize = entity.getContentLength();
                    httppost.setEntity(entity);

                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);
                        try {
                            JSONObject resp = new JSONObject(responseString);
                            String successVal = resp.getString("status");

                            mUpdatedImageUrl = resp.getString("picture_url");

                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
                            if (successVal.equalsIgnoreCase("success")) {
                                Log.d(TAG, "Updated image succesfully");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            super.onPostExecute(result);
            if ((result == null) || (result.isEmpty()) || (result.contains("Error"))) {
                Toast.makeText(ProfileActivity.this, "Unable to save profile picture", Toast.LENGTH_SHORT).show();
            } else {
                if (mUpdatedImageUrl != null) {
                    PreferenceStorage.saveUserPicture(ProfileActivity.this, mUpdatedImageUrl);
                }
            }
            saveProfileData();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void showGenderList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Gender");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Gender");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mGenderAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mGenderList.get(which);
                        mGender.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showOccupationList() {
        Log.d(TAG, "Show occupation list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Occupation");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mOccupationAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mOccupationList.get(which);
                        mOccupation.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showBirthdayDate() {
        Log.d(TAG, "Show the birthday date");
        Calendar newCalendar = Calendar.getInstance();
        String currentdate = mBirthday.getText().toString();
        Log.d(TAG, "current date is" + currentdate);
        int month = newCalendar.get(Calendar.MONTH);
        int day = newCalendar.get(Calendar.DAY_OF_MONTH);
        int year = newCalendar.get(Calendar.YEAR);
        if ((currentdate != null) && !(currentdate.isEmpty())) {
            //extract the date/month and year
            try {
                Date startDate = mDateFormatter.parse(currentdate);
                Calendar newDate = Calendar.getInstance();

                newDate.setTime(startDate);
                month = newDate.get(Calendar.MONTH);
                day = newDate.get(Calendar.DAY_OF_MONTH);
                year = newDate.get(Calendar.YEAR);
                Log.d(TAG, "month" + month + "day" + day + "year" + year);

            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                mDatePicker = new DatePickerDialog(this, R.style.datePickerTheme, this, year, month, day);
                mDatePicker.show();
            }
        } else {
            Log.d(TAG, "show default date");

            mDatePicker = new DatePickerDialog(this, R.style.datePickerTheme, this, year, month, day);
            mDatePicker.show();
        }
    }

    private boolean validateFields() {
        if (!HeylaAppValidator.checkNullString(this.name.getText().toString().trim())) {
            inputName.setError(getString(R.string.err_name));
            requestFocus(name);
            return false;

        } else if (!HeylaAppValidator.checkNullString(this.username.getText().toString().trim())) {
            inputUsername.setError(getString(R.string.err_username));
            requestFocus(username);
            return false;
        } else if (!HeylaAppValidator.checkStringMinLength(4, this.username.getText().toString().trim())) {
            inputUsername.setError(getString(R.string.err_username));
            requestFocus(username);
            return false;
        }else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        mBirthday.setText(mDateFormatter.format(newDate.getTime()));
    }

    @Override
    public void onClick(View view) {
        if (view == save) {
            if (validateFields()) {
                checkInternalState = "profile_update";
                saveProfile();
                /*Intent homeIntent = new Intent(this.getApplicationContext(), MainActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);*/
//                finish();
            }
        } else if (view == mProfileImage) {
            openImageIntent();
        } else if (view == country) {
            showCountryList();
        } else if (view == state) {
            showStateList();
        } else if (view == city) {
            showCityList();
        }
    }

    private void openImageIntent() {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyDir");

        if (!root.exists()) {
            if (!root.mkdirs()) {
                Log.d(TAG, "Failed to create directory for storing images");
                return;
            }
        }

        final String fname = PreferenceStorage.getUserId(this) + ".png";
        final File sdImageMainDirectory = new File(root.getPath() + File.separator + fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Log.d(TAG, "camera output Uri" + outputFileUri);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Profile Photo");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_IMAGE_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_GET) {
                Log.d(TAG, "ONActivity Result");
                final boolean isCamera;
                if (data == null) {
                    Log.d(TAG, "camera is true");
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    Log.d(TAG, "camera action is" + action);
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;

                if (isCamera) {
                    Log.d(TAG, "Add to gallery");
                    selectedImageUri = outputFileUri;
                    mActualFilePath = outputFileUri.getPath();
                    galleryAddPic(selectedImageUri);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    mActualFilePath = getRealPathFromURI(this, selectedImageUri);
                    Log.d(TAG, "path to image is" + mActualFilePath);

                }
                Log.d(TAG, "image Uri is" + selectedImageUri);
                if (selectedImageUri != null) {
                    Log.d(TAG, "image URI is" + selectedImageUri);
                    setPic(selectedImageUri);
                }
            }
        }
    }

    private void galleryAddPic(Uri urirequest) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(urirequest.getPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String result = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);


            Cursor cursor = loader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
                cursor.close();
            } else {
                Log.d(TAG, "cursor is null");
            }
        } catch (Exception e) {
            result = null;
            Toast.makeText(this, "Was unable to save  image", Toast.LENGTH_SHORT).show();

        } finally {
            return result;
        }

    }

    private void setPic(Uri selectedImageUri) {
        // Get the dimensions of the View
        int targetW = mProfileImage.getWidth();
        int targetH = mProfileImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        mSelectedImageUri = selectedImageUri;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
            mProfileImage.setImageBitmap(bitmap);
            mCurrentUserImageBitmap = bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(HeylaAppConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(JSONObject response) {

        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        progressDialogHelper.hideProgressDialog();

        try {
            if (validateSignInResponse(response)) {

                if (checkInternalState.equalsIgnoreCase("profile_update")) {
                    if (checkProfileState.equalsIgnoreCase("new")) {
                        PreferenceStorage.saveCheckFirstTimeProfile(getApplicationContext(), "reuse");
                        Intent homeIntent = new Intent(getApplicationContext(), SelectCityActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        finish();
                    } else if (checkProfileState.equalsIgnoreCase("reuse")) {
                        Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        finish();
                    }
                } else if (checkInternalState.equalsIgnoreCase("country")) {

                    JSONArray getData = response.getJSONArray("Countries");
                    int getLength = getData.length();
                    String countryId = "";
                    String countryName = "";
                    countryList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        countryId = getData.getJSONObject(i).getString("id");
                        countryName = getData.getJSONObject(i).getString("country_name");

                        countryList.add(new StoreCountry(countryId, countryName));
                    }

                    //fill data in spinner
                    mCountryAdapter = new ArrayAdapter<StoreCountry>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, countryList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(countryList.get(position).getCountryName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };

                } else if (checkInternalState.equalsIgnoreCase("state")) {

                    JSONArray getData = response.getJSONArray("States");
                    int getLength = getData.length();
                    String stateId = "";
                    String stateName = "";
                    stateList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        stateId = getData.getJSONObject(i).getString("id");
                        stateName = getData.getJSONObject(i).getString("state_name");

                        stateList.add(new StoreState(stateId, stateName));
                    }

                    //fill data in spinner
                    mStateAdapter = new ArrayAdapter<StoreState>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, stateList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(stateList.get(position).getStateName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };

                } else if (checkInternalState.equalsIgnoreCase("city")) {

                    JSONArray getData = response.getJSONArray("Cities");
                    int getLength = getData.length();
                    String cityId = "";
                    String cityName = "";
                    cityList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        cityId = getData.getJSONObject(i).getString("id");
                        cityName = getData.getJSONObject(i).getString("city_name");

                        cityList.add(new StoreCity(cityId, cityName));
                    }

                    //fill data in spinner
                    mCityAdapter = new ArrayAdapter<StoreCity>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, cityList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(cityList.get(position).getCityName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showCountryList() {
        Log.d(TAG, "Show country list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Country");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mCountryAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreCountry county = countryList.get(which);
                        country.setText(county.getCountryName());
                        countryId = county.getCountryId();
                        callStates(countryId);
                    }
                });
        builderSingle.show();
    }

    private void showStateList() {
        Log.d(TAG, "Show state list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select State");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mStateAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreState stae = stateList.get(which);
                        state.setText(stae.getStateName());
                        stateId = stae.getStateId();
                        callCity(countryId, stateId);
                    }
                });
        builderSingle.show();
    }

    private void showCityList() {
        Log.d(TAG, "Show city list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select City");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mCityAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreCity cty = cityList.get(which);
                        city.setText(cty.getCityName());
                        cityId = cty.getCityId();
                    }
                });
        builderSingle.show();
    }

    private void callStates(String CountryId) {
        checkInternalState = "state";

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(HeylaAppConstants.PARAMS_COUNTRY_ID, CountryId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = HeylaAppConstants.BASE_URL + HeylaAppConstants.STATE_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);


        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void callCity(String CountryId, String StateId) {
        checkInternalState = "city";

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(HeylaAppConstants.PARAMS_COUNTRY_ID, CountryId);
                jsonObject.put(HeylaAppConstants.PARAMS_STATE_ID, StateId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = HeylaAppConstants.BASE_URL + HeylaAppConstants.CITY_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);


        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    @Override
    public void onError(String error) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, "Error saving your profile. Try again");
    }
}
