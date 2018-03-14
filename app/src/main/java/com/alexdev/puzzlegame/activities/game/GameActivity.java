package com.alexdev.puzzlegame.activities.game;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.alexdev.puzzlegame.R;
import com.alexdev.puzzlegame.api.ApiUtils;
import com.alexdev.puzzlegame.api.GameApi;
import com.alexdev.puzzlegame.manager.SessionManager;
import com.alexdev.puzzlegame.models.GamePost;
import com.alexdev.puzzlegame.models.GameResponse;
import com.alexdev.puzzlegame.utils.Fonts;
import com.dolby.dap.DolbyAudioProcessing;
import com.dolby.dap.OnDolbyAudioProcessingEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.MessageFormat;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, OnDolbyAudioProcessingEventListener {

    protected static final int MENU_SCRAMBLE = 0;
    protected static final int MENU_SELECT_IMAGE = 1;
    protected static final int MENU_TAKE_PHOTO = 2;

    protected static final int RESULT_SELECT_IMAGE = 0;
    protected static final int RESULT_TAKE_PHOTO = 1;

    protected static final String KEY_SHOW_NUMBERS = "showNumbers";
    protected static final String KEY_IMAGE_URI = "imageUri";
    protected static final String KEY_PUZZLE = "slidePuzzle";
    protected static final String KEY_PUZZLE_SIZE = "puzzleSize";

    protected static final String FILENAME_DIR = "com.dolby.DolbyPuzzle";
    protected static final String FILENAME_PHOTO_DIR = FILENAME_DIR + "/photo";
    protected static final String FILENAME_PHOTO = "photo.jpg";

    protected static final int DEFAULT_SIZE = 3;

    private SlidePuzzleView view;
    private SlidePuzzle slidePuzzle;
    private BitmapFactory.Options bitmapOptions;
    private int puzzleWidth = 1;
    private int puzzleHeight = 1;
    private Uri imageUri;
    private boolean portrait;
    private boolean expert;
    private Chronometer chronometer;

    MediaPlayer mPlayer;
    DolbyAudioProcessing mDolbyAudioProcessing;
    private final java.util.List<String> mActList = new java.util.ArrayList<String>();
    LinearLayout mGameLatout;
    private Activity activity;
    private GameApi gameApi;
    private Button  mOpenMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        activity = this;
        gameApi = ApiUtils.getGameAPI();
        mGameLatout = findViewById(R.id.gameLayout);
        chronometer = findViewById(R.id.chronometer);
        chronometer.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mOpenMenuButton = findViewById(R.id.openMenuButton);
        mOpenMenuButton.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mOpenMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });
        // Layout inflater
        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;

        slidePuzzle = new SlidePuzzle();

        view = new SlidePuzzleView(this, slidePuzzle);

        mGameLatout.addView(view);
        shuffle();

        if(!loadPreferences())
        {
            setPuzzleSize(DEFAULT_SIZE, true);
        }

        Uri path = Uri.parse("android.resource://com.alexdev.puzzlegame/" + R.drawable.stopwatch);

        loadBitmap(path);
        initChronometer();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menuSelectImage:
                        selectImage();
                        return true;
                    case R.id.menuTakePhoto:
                        takePicture();
                        return true;
                    case R.id.menuShuffle:
                        shuffle();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.menu_pop);
        popup.show();
    }


    private void initChronometer() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you ready?")
                .setContentText("Time runs when you want!")
                .setConfirmText("Yeah!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        sDialog.dismissWithAnimation();
                        chronometer.start();
                    }
                })
                .show();
    }
    private void shuffle() {
        slidePuzzle.init(puzzleWidth, puzzleHeight);
        slidePuzzle.shuffle();
        view.invalidate();
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        expert = view.getShowNumbers() == SlidePuzzleView.ShowNumbers.NONE;
    }

    protected void loadBitmap(Uri uri) {
        try
        {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BitmapFactory.decodeStream(imageStream, null, o);

            int targetWidth = view.getTargetWidth();
            int targetHeight = view.getTargetHeight();

            if(o.outWidth > o.outHeight && targetWidth < targetHeight)
            {
                int i = targetWidth;
                targetWidth = targetHeight;
                targetHeight = i;
            }

            if(targetWidth < o.outWidth || targetHeight < o.outHeight)
            {
                double widthRatio = (double) targetWidth / (double) o.outWidth;
                double heightRatio = (double) targetHeight / (double) o.outHeight;
                double ratio = Math.max(widthRatio, heightRatio);

                o.inSampleSize = (int) Math.pow(2, (int) Math.round(Math.log(ratio) / Math.log(0.5)));
            }
            else
            {
                o.inSampleSize = 1;
            }

            o.inScaled = false;
            o.inJustDecodeBounds = false;

            imageStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, o);

            if(bitmap == null)
            {
                Toast.makeText(this, getString(R.string.alexdev), Toast.LENGTH_LONG).show();
                return;
            }

            int rotate = 0;

            Cursor cursor = getContentResolver().query(uri, new String[] {MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

            if(cursor != null)
            {
                try
                {
                    if(cursor.moveToFirst())
                    {
                        rotate = cursor.getInt(0);

                        if(rotate == -1)
                        {
                            rotate = 0;
                        }
                    }
                }
                finally
                {
                    cursor.close();
                }
            }

            if(rotate != 0)
            {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            setBitmap(bitmap);
            imageUri = uri;
        }
        catch(FileNotFoundException ex)
        {
            Toast.makeText(this, MessageFormat.format(getString(R.string.alexdev), ex.getMessage()), Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void setBitmap(Bitmap bitmap) {
        portrait = bitmap.getWidth() < bitmap.getHeight();

        view.setBitmap(bitmap);
        setPuzzleSize(Math.min(puzzleWidth, puzzleHeight), true);

        setRequestedOrientation(portrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void selectImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_SELECT_IMAGE);
    }

    private void takePicture()
    {


        Intent photoPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getOutputMediaFile()));
        startActivityForResult(photoPickerIntent, RESULT_TAKE_PHOTO);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator +FILENAME_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode)
        {
            case RESULT_SELECT_IMAGE:
            {
                if(resultCode == RESULT_OK)
                {
                    Uri selectedImage = imageReturnedIntent.getData();
                    loadBitmap(selectedImage);
                }

                break;
            }

            case RESULT_TAKE_PHOTO:
            {
                if(resultCode == RESULT_OK)
                {
                    File file =  getOutputMediaFile();

                    if(file.exists())
                    {
                        Uri uri = Uri.fromFile(file);

                        if(uri != null)
                        {
                            loadBitmap(uri);
                        }
                    }
                }

                break;
            }
        }
    }

    private float getImageAspectRatio()
    {
        Bitmap bitmap = view.getBitmap();

        if(bitmap == null)
        {
            return 1;
        }

        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        return width / height;
    }

    protected void setPuzzleSize(int size, boolean scramble)
    {
        float ratio = getImageAspectRatio();

        if(ratio < 1)
        {
            ratio = 1f /ratio;
        }

        int newWidth;
        int newHeight;

        if(portrait)
        {
            newWidth = size;
            newHeight = (int) (size * ratio);
        }
        else
        {
            newWidth = (int) (size * ratio);
            newHeight = size;
        }

        if(scramble || newWidth != puzzleWidth || newHeight != puzzleHeight)
        {
            puzzleWidth = newWidth;
            puzzleHeight = newHeight;
            shuffle();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case MENU_SCRAMBLE:
                shuffle();
                return true;

            case MENU_SELECT_IMAGE:
                selectImage();
                return true;

            case MENU_TAKE_PHOTO:
                takePicture();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected SharedPreferences getPreferences()
    {
        return getSharedPreferences(GameActivity.class.getName(), Activity.MODE_PRIVATE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mPlayer != null)
        {
            mPlayer.start();
        }
    }

    protected boolean loadPreferences()
    {
        SharedPreferences prefs = getPreferences();

        try
        {

            String s = prefs.getString(KEY_IMAGE_URI, null);

            if(s == null)
            {
                imageUri = null;
            }
            else
            {
                loadBitmap(Uri.parse(s));
            }

            int size = prefs.getInt(KEY_PUZZLE_SIZE, 0);
            s = prefs.getString(KEY_PUZZLE, null);

            if(size > 0 && s != null)
            {
                String[] tileStrings = s.split("\\;");

                if(tileStrings.length / size > 1)
                {
                    setPuzzleSize(size, false);
                    slidePuzzle.init(puzzleWidth, puzzleHeight);

                    int[] tiles = new int[tileStrings.length];

                    for(int i = 0; i < tiles.length; i++)
                    {
                        try
                        {
                            tiles[i] = Integer.parseInt(tileStrings[i]);
                        }
                        catch(NumberFormatException ex)
                        {
                        }
                    }

                    slidePuzzle.setTiles(tiles);
                }
            }

            return prefs.contains(KEY_SHOW_NUMBERS);
        }
        catch(ClassCastException ex)
        {
            // ignore broken settings
            return false;
        }
    }

    public void playSound()
    {
        if(mPlayer == null) {
            mPlayer = MediaPlayer.create(
                    GameActivity.this,
                    R.raw.slide);
            mPlayer.start();
        } else {
            mPlayer.release();
            mPlayer = null;
            mPlayer = MediaPlayer.create(
                    GameActivity.this,
                    R.raw.slide);
            mPlayer.start();
        }

        mDolbyAudioProcessing = DolbyAudioProcessing.getDolbyAudioProcessing(this, DolbyAudioProcessing.PROFILE.GAME, this);
        if (mDolbyAudioProcessing == null) {
            return;
        }
    }

    public void onFinish()
    {
        if(mPlayer == null) {
            mPlayer = MediaPlayer.create(
                    GameActivity.this,
                    R.raw.fireworks);
            mPlayer.start();
        } else {
            mPlayer.release();
            mPlayer = null;
            mPlayer = MediaPlayer.create(
                    GameActivity.this,
                    R.raw.fireworks);
            mPlayer.start();
        }

        mDolbyAudioProcessing = DolbyAudioProcessing.getDolbyAudioProcessing(this, DolbyAudioProcessing.PROFILE.GAME, this);
        if (mDolbyAudioProcessing == null) {
            chronometer.stop();
            new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Congratulations")
                    .setContentText("your time was! "+chronometer.getText())
                    .setConfirmText("Yeah!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                           // activity.finish();
                            saveGame();
                            sDialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void saveGame(){
        GamePost game = new GamePost();
        game.setTime(chronometer.getText().toString());
        SharedPreferences sharedpreferences = this.getSharedPreferences(SessionManager.REFERENCE, Context.MODE_PRIVATE);
        game.setPlayer(sharedpreferences.getString("idKey","fail"));
        game.setUsername(sharedpreferences.getString("nameKey","fail"));
        game.setGender(sharedpreferences.getString("genderKey","fail"));
        gameApi.saveGame(game).enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                Log.d("success game",response.body().getGame().getPlayer());
                activity.finish();
            }
            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Log.d("error game",t.getMessage());
                new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...Error")
                        .setContentText("check your internet!")
                        .show();
            }
        });

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onDolbyAudioProcessingClientConnected() {
        mDolbyAudioProcessing.setEnabled(true);
    }

    @Override
    public void onDolbyAudioProcessingClientDisconnected() {
        mDolbyAudioProcessing.setEnabled(false);
    }

    @Override
    public void onDolbyAudioProcessingEnabled(boolean b) {
    }

    @Override
    public void onDolbyAudioProcessingProfileSelected(DolbyAudioProcessing.PROFILE profile) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("Dolby processing", "onDestroy()");

        // Release Media Player instance
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

        this.releaseDolbyAudioProcessing();

    }

    @Override
    protected void onResume() {
        super.onResume();
        restartSession();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Dolby processing", "The application is in background, supsendSession");
        //
        // If audio playback is not required while your application is in the background, restore the Dolby audio processing system
        // configuration to its original state by suspendSession().
        // This ensures that the use of the system-wide audio processing is sandboxed to your application.
        suspendSession();
    }

    public void releaseDolbyAudioProcessing() {
        if (mDolbyAudioProcessing != null) {
            try {
                mDolbyAudioProcessing.release();
                mDolbyAudioProcessing = null;
            } catch (IllegalStateException ex) {
                handleIllegalStateException(ex);
            } catch (RuntimeException ex) {
                handleRuntimeException(ex);
            }
        }

    }

    // Backup the system-wide audio effect configuration and restore the application configuration
    public void restartSession() {
        if (mDolbyAudioProcessing != null) {
            try{
                mDolbyAudioProcessing.restartSession();
            } catch (IllegalStateException ex) {
                handleIllegalStateException(ex);
            } catch (RuntimeException ex) {
                handleRuntimeException(ex);
            }
        }
    }

    // Backup the application Dolby Audio Processing configuration and restore the system-wide configuration
    public void suspendSession() {

        if (mDolbyAudioProcessing != null) {
            try{
                mDolbyAudioProcessing.suspendSession();
            } catch (IllegalStateException ex) {
                handleIllegalStateException(ex);
            } catch (RuntimeException ex) {
                handleRuntimeException(ex);
            }
        }
    }

    /** Generic handler for IllegalStateException */
    private void handleIllegalStateException(Exception ex)
    {
        Log.e("Dolby processing", "Dolby Audio Processing has a wrong state");
        handleGenericException(ex);
    }

    /** Generic handler for IllegalArgumentException */
    private void handleIllegalArgumentException(Exception ex)
    {
        Log.e("Dolby processing","One of the passed arguments is invalid");
        handleGenericException(ex);
    }

    /** Generic handler for RuntimeException */
    private void handleRuntimeException(Exception ex)
    {
        Log.e("Dolby processing", "Internal error occured in Dolby Audio Processing");
        handleGenericException(ex);
    }

    private void handleGenericException(Exception ex)
    {
        Log.e("Dolby processing", Log.getStackTraceString(ex));
    }

}
