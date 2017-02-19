package dailyselfie.part2.coursera.com.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DailySelfieMainActivity extends AppCompatActivity {

    private static final String LOG_TAG = DailySelfieMainActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final long INTERVAL_TWO_MINUTES = 2 *60*1000L;

    private DailySelfieListAdapter dailySelfieListAdapter;
    private String mSelfieName;
    private String mPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie_main);

        ListView selfieList = (ListView) findViewById(R.id.selfie_list);
        dailySelfieListAdapter = new DailySelfieListAdapter(getApplicationContext());
        selfieList.setAdapter(dailySelfieListAdapter);
        selfieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DailySelfieRecord selfieRecord = (DailySelfieRecord) dailySelfieListAdapter.getItem(position);
                Intent intent = new Intent(DailySelfieMainActivity.this, DailySelfieDetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, selfieRecord.getmPath());
                startActivity(intent);
            }
        });
        createSelfieAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera) {
            dispatchTakePictureIntent();
            return true;
        }
        if (id == R.id.action_delete_selected) {
            deleteSelectedSelfies();
            return true;
        }
        if (id == R.id.action_delete_all) {
            deleteAllSelfies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        mSelfieName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imageFile = File.createTempFile(
                mSelfieName,
                ".jpg",
                getExternalFilesDir(null));

        mPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Rename temporary file as yyyyMMdd_HHmmss.jpg
            File photoFile = new File(mPhotoPath);
            File selfieFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), mSelfieName + ".jpg");
            photoFile.renameTo(selfieFile);

            DailySelfieRecord selfieRecord = new DailySelfieRecord(Uri.fromFile(selfieFile).getPath(), mSelfieName);
            Log.d(LOG_TAG, selfieRecord.getmPath() + " - " + selfieRecord.getDisplayName());
            dailySelfieListAdapter.add(selfieRecord);
        }
        else {
            File photoFile = new File(mPhotoPath);
            photoFile.delete();
        }
    }

    private void deleteSelectedSelfies() {
        ArrayList<DailySelfieRecord> selectedSelfies = dailySelfieListAdapter.getSelectedRecords();
        for (DailySelfieRecord selfieRecord : selectedSelfies) {
            File selfieFile = new File(selfieRecord.getmPath());
            selfieFile.delete();
        }
        dailySelfieListAdapter.clearSelected();
    }

    private void deleteAllSelfies() {
        for (DailySelfieRecord selfieRecord : dailySelfieListAdapter.getAllRecords()) {
            File selfieFile = new File(selfieRecord.getmPath());
            selfieFile.delete();
        }
        dailySelfieListAdapter.clearAll();
    }

    private void createSelfieAlarm() {
        Intent intent = new Intent(this, DailySelfieNotificationListener.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INTERVAL_TWO_MINUTES,
                INTERVAL_TWO_MINUTES,
                pendingIntent);
    }
}
