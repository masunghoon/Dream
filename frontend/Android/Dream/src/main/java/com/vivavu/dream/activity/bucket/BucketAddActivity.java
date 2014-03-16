package com.vivavu.dream.activity.bucket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RepeatType;
import com.vivavu.dream.fragment.bucket.option.description.DescriptionViewFragment;
import com.vivavu.dream.fragment.bucket.option.repeat.RepeatViewFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketWrapped;
import com.vivavu.dream.model.bucket.option.OptionDDay;
import com.vivavu.dream.model.bucket.option.OptionDescription;
import com.vivavu.dream.model.bucket.option.OptionRepeat;
import com.vivavu.dream.repository.Connector;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.task.CustomAsyncTask;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.util.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 13.
 */
public class BucketAddActivity extends BaseActionBarActivity {
    @InjectView(R.id.bucket_input_title)
    EditText mBucketInputTitle;
    @InjectView(R.id.btn_input_dday)
    Button mBtnInputDday;
    @InjectView(R.id.text_input_dday)
    TextView mTextInputDday;
    @InjectView(R.id.text_input_remain)
    TextView mTextInputRemain;

    @InjectView(R.id.btn_bucket_option_note)
    Button mBtnBucketOptionNote;
    @InjectView(R.id.btn_bucket_option_repeat)
    Button mBtnBucketOptionRepeat;
    @InjectView(R.id.btn_bucket_option_public)
    Button mBtnBucketOptionPublic;
    @InjectView(R.id.btn_bucket_option_pic)
    Button mBtnBucketOptionPic;
    @InjectView(R.id.btn_bucket_option_gallery)
    Button mBtnBucketOptionGallery;
    @InjectView(R.id.ivCardImage)
    ImageView mIvCardImage;

    private LayoutInflater layoutInflater;
    private Bucket bucket = null;
    protected Uri mImageCaptureUri;
    private String modString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_input_template);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();

        int bucketId = data.getIntExtra("bucketId", -1);
        bucket = DataRepository.getBucket(bucketId);
        int code;
        if (bucketId > 0) {

            code = Code.ACT_MOD_BUCKET_DEFAULT_CARD;
            modString = "수정";
            actionBar.setTitle("Mod Bucket");
        } else {
            code = Code.ACT_ADD_BUCKET_DEFAULT_CARD;
            modString = "등록";
            actionBar.setTitle("Add Bucket");
        }

        ScrollView root = (ScrollView) findViewById(R.id.view_intput_template);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // intent에서 넘겨준 인자에 따라서 기본입력인지 옵션 입력인지 분리하여 UI 구성시킴

        View viewGroup = layoutInflater.inflate(R.layout.bucket_input_default, null, false);
        ButterKnife.inject(this, viewGroup);
        root.addView(viewGroup);

        addEventListener();
        switch (code) {
            case Code.ACT_ADD_BUCKET_DEFAULT_CARD:

                break;
            case Code.ACT_MOD_BUCKET_DEFAULT_CARD:


                bindData();
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bucket_add_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.bucket_add_menu_save:

                if (bucket == null || bucket.getTitle() == null || bucket.getTitle().trim().length() <= 0) {
                    Toast.makeText(this, "필수입력 항목이 입력되지 않았음", Toast.LENGTH_SHORT).show();
                }else{
                    saveBucket();
                }

                return true;
            case R.id.bucket_add_menu_cancel:


                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveBucket() {
        BucketAddTask bucketAddTask = new BucketAddTask(getContext());
        bucketAddTask.execute(bucket);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Code.ACT_ADD_BUCKET_OPTION_DDAY:
                if(resultCode == RESULT_OK){
                    OptionDDay dDay = (OptionDDay) data.getSerializableExtra("option.result");
                    updateUiData(dDay);
                }
                break;
            case Code.ACT_ADD_BUCKET_OPTION_DESCRIPTION:
                if(resultCode == RESULT_OK){
                    OptionDescription description = (OptionDescription) data.getSerializableExtra("option.result");
                    updateUiData(description);
                }
                break;
            case Code.ACT_ADD_BUCKET_OPTION_REPEAT:
                if(resultCode == RESULT_OK){
                    OptionRepeat repeat = (OptionRepeat) data.getSerializableExtra("option.result");
                    updateUiData(repeat);
                }
                break;
            case Code.ACT_ADD_BUCKET_TAKE_CAMERA:
                if(resultCode == RESULT_OK){
                    File f = new File(mImageCaptureUri.getPath());
                    // 찍은 사진을 이미지뷰에 보여준다.
                    if(data != null && data.getExtras() != null) {
                        Bitmap bm = (Bitmap) data.getExtras().getParcelable("data");
                        ImageView ivCardImage = (ImageView) findViewById(R.id.ivCardImage);
                        ivCardImage.setImageBitmap(bm);
                    } else if( f.exists() ){
                        /// http://stackoverflow.com/questions/9890757/android-camera-data-intent-returns-null
                        /// EXTRA_OUTPUT을 선언해주면 해당 경로에 파일을 직접생성하고 썸네일을 리턴하지 않음
                        ImageUtil.setPic(mIvCardImage, mImageCaptureUri.getPath());
                    }
                    if(f.exists()){
                        //f.delete();
                        bucket.setFile(f);
                    }
                }
                break;
            case Code.ACT_ADD_BUCKET_TAKE_GALLERY:
                if(resultCode == RESULT_OK){
                    // 찍은 사진을 이미지뷰에 보여준다.
                    if(data != null && data.getExtras() != null) {
                        mImageCaptureUri = data.getData();
                        File f = new File(mImageCaptureUri.getPath());
                        if(f.exists()){
                            //f.delete();
                            bucket.setFile(f);
                        }
                    }
                    doCropPhoto();
                    /*Uri currImageURI = data.getData( ) ;
                    String path = getRealPathFromURI(currImageURI) ;
                    tempPictuePath = path ;
                    // 찍은 사진을 이미지뷰에 보여준다.
                    ImageView ivCardImage = (ImageView) findViewById(R.id.ivCardImage);
                    ImageUtil.setAlbumImage( path, ivCardImage ) ;*/
                }
                break;
            case Code.ACT_ADD_BUCKET_CROP_FROM_CAMERA:
                if(data != null && data.getExtras() != null){
                    final Bundle extras = data.getExtras();
                    if(extras != null){
                        Bitmap photo = extras.getParcelable("data");
                        ImageView ivCardImage = (ImageView) findViewById(R.id.ivCardImage);
                        ivCardImage.setImageBitmap(photo);
                    }
                }
                break;

        }

    }

    private void updateUiData(OptionRepeat repeat) {
        bucket.setRptType(repeat.getRepeatType().getCode());
        bucket.setRptCndt(repeat.getOptionStat());

        bindData();
    }

    private void updateUiData(OptionDescription description) {
        bucket.setDescription(description.getDescription());

        bindData();
    }

    private void addEventListener() {
        mBucketInputTitle.addTextChangedListener(textWatcherInput);

        mBtnInputDday.setOnClickListener(this);
        mBtnBucketOptionNote.setOnClickListener(this);
        mBtnBucketOptionRepeat.setOnClickListener(this);
        mBtnBucketOptionPublic.setOnClickListener(this);
        mBtnBucketOptionPic.setOnClickListener(this);
        mBtnBucketOptionGallery.setOnClickListener(this);
    }

    private void updateUiData(OptionDDay dday) {
        if (dday.getDeadline() != null) {
            mTextInputDday.setText(dday.getDdayString());
        } else {
            mTextInputDday.setText(dday.getRange());
        }
        if (dday.getDeadline() != null) {
            mTextInputRemain.setText(DateUtils.getRemainDayInString(dday.getDeadline()));
        } else {
            mTextInputRemain.setText("");
        }
        bucket.setTitle(mBucketInputTitle.getText().toString());
        bucket.setDeadline(dday.getDeadline());
    }

    private void bindData() {
        mTextInputDday.setText(bucket.getRange());
        mTextInputRemain.setText(bucket.getRemainDays());
        mBucketInputTitle.setText(bucket.getTitle());
        DescriptionViewFragment descriptionViewFragment = (DescriptionViewFragment) getSupportFragmentManager().findFragmentByTag(DescriptionViewFragment.TAG);
        if(bucket.getDescription() != null ){
            OptionDescription option = new OptionDescription(bucket.getDescription());
            if (descriptionViewFragment == null) {
                descriptionViewFragment = new DescriptionViewFragment(option);
                getSupportFragmentManager().beginTransaction().add(R.id.option_contents, descriptionViewFragment, DescriptionViewFragment.TAG).commit();
            } else {
                descriptionViewFragment.setContents(option);
            }
            mBtnBucketOptionNote.setVisibility(View.GONE);
        }else{
            if (descriptionViewFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(descriptionViewFragment).commit();
            }
            mBtnBucketOptionNote.setVisibility(View.VISIBLE);
        }
        RepeatViewFragment repeatFragment = (RepeatViewFragment) getSupportFragmentManager().findFragmentByTag(RepeatViewFragment.TAG);
        if (bucket.getRptType() != null) {
            OptionRepeat option = new OptionRepeat(RepeatType.fromCode(bucket.getRptType()), bucket.getRptCndt());
            if (repeatFragment == null) {
                repeatFragment = new RepeatViewFragment(option);
                getSupportFragmentManager().beginTransaction().add(R.id.option_contents, repeatFragment, RepeatViewFragment.TAG).commit();
            } else {
                repeatFragment.setContents(option);
            }
            mBtnBucketOptionRepeat.setVisibility(View.GONE);
        } else {
            if (repeatFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(repeatFragment).commit();
            }
            mBtnBucketOptionRepeat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == mBtnInputDday){
            goOptionDday();
        }else if(view == mBtnBucketOptionNote){
            goOptionDescription();

        } else if(view == mBtnBucketOptionRepeat){
            goOptionRepeat();
        } else if(view == mBtnBucketOptionPublic){
            mBtnBucketOptionPublic.setSelected(!mBtnBucketOptionPublic.isSelected());
            bucket.setIsPrivate( mBtnBucketOptionPublic.isSelected() ? 1 : 0 );
        } else if(view == mBtnBucketOptionPic) {
            doTakePhotoAction();
        } else if( view == mBtnBucketOptionGallery ) {
            doTakeAlbumAction();
        }
    }

    public void goOptionRepeat() {
        Intent intent = new Intent();
        intent.setClass(this, BucketOptionActivity.class);
        OptionRepeat repeat = new OptionRepeat(RepeatType.fromCode(bucket.getRptType()), bucket.getRptCndt());
        intent.putExtra("option", repeat);
        startActivityForResult(intent, Code.ACT_ADD_BUCKET_OPTION_REPEAT);
    }

    public void goOptionDescription() {
        Intent intent = new Intent();
        intent.setClass(this, BucketOptionActivity.class);
        OptionDescription description = new OptionDescription(bucket.getDescription());
        intent.putExtra("option", description);
        startActivityForResult(intent, Code.ACT_ADD_BUCKET_OPTION_DESCRIPTION);
    }

    public void goOptionDday() {
        Intent intent = new Intent();
        intent.setClass(this, BucketOptionActivity.class);
        OptionDDay dDay = new OptionDDay(bucket.getRange(), bucket.getDeadline());
        intent.putExtra("option", dDay);
        startActivityForResult(intent, Code.ACT_ADD_BUCKET_OPTION_DDAY);
    }

    TextWatcher textWatcherInput = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().length() < 1){
                bucket.setTitle(null);
            }else {
                bucket.setTitle(s.toString());
            }
        }
    };

    private void doTakePhotoAction(){
        /*
        * 참고 해볼곳
        * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
        * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
        * http://www.damonkohler.com/2009/02/android-recipes.html
        * http://www.firstclown.us/tag/android/
        */

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("dream", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                startActivityForResult(intent, Code.ACT_ADD_BUCKET_TAKE_CAMERA);
            }
        }else{
            Toast.makeText(this, "카메라 앱을 실행할 수 없습니다.", Toast.LENGTH_LONG).show();
        }


    }

    private File createImageFile() throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mImageCaptureUri = Uri.fromFile(image);
        return image;

    }

    private void doTakeAlbumAction(){
        Intent intent = new Intent( Intent.ACTION_PICK ) ;
        intent.setType( MediaStore.Images.Media.CONTENT_TYPE ) ;
        startActivityForResult( intent, Code.ACT_ADD_BUCKET_TAKE_GALLERY ) ;
    }

    private void doCropPhoto(){
        // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
        // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mImageCaptureUri, "image/*");

        intent.putExtra("outputX", 90);
        intent.putExtra("outputY", 90);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, Code.ACT_ADD_BUCKET_CROP_FROM_CAMERA);
    }

    public class BucketAddTask extends CustomAsyncTask<Bucket, Void, ResponseBodyWrapped<BucketWrapped>>{
        private DreamApp context;

        public BucketAddTask(DreamApp context) {
            this.context = context;
        }

        @Override
        protected ResponseBodyWrapped<BucketWrapped> doInBackground(Bucket... params) {
            Connector connector = new Connector();
            ResponseBodyWrapped<BucketWrapped> responseBodyWrapped = new ResponseBodyWrapped<BucketWrapped>();

            if(params != null && params.length > 0){
                Bucket param = params[0];
                if(param.getId() != null && param.getId() > 0) {
                    responseBodyWrapped = connector.updateBucketInfo(params[0]);
                }else{
                    responseBodyWrapped = connector.postBucketDefault(params[0]);
                }
            }

            return responseBodyWrapped;
        }

        @Override
        protected void onPostExecute(ResponseBodyWrapped<BucketWrapped> bucketWrappedResponseBodyWrapped) {
            if(bucketWrappedResponseBodyWrapped.getData() != null){
                Bucket bucket = bucketWrappedResponseBodyWrapped.getData().getBucket();
                if(bucket != null){
                    DataRepository.saveBucket(bucket);

                    if (bucket != null) {
                        Toast.makeText(BucketAddActivity.this, modString + "성공", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra("bucketId", (Integer) bucket.getId());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }else {
                Toast.makeText(BucketAddActivity.this, modString + "실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        confirm();
    }

    public void confirm(){
        Bucket compare = DataRepository.getBucket(bucket.getId());
        if(!compare.equals(bucket)) {
            AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
            alertConfirm.setTitle("내용 변경 확인");
            alertConfirm.setMessage("변경한 내용을 저장하시겠습니까?").setCancelable(false).setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(BucketAddActivity.this, "예", Toast.LENGTH_SHORT).show();
                        }
                    }
            ).setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            return;
                        }
                    }
            );
            AlertDialog alert = alertConfirm.create();
            alert.show();
        } else {
            finish();
        }
    }
}
