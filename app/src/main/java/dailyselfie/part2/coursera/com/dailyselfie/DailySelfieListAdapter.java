package dailyselfie.part2.coursera.com.dailyselfie;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by Naik Junior on 2/18/2017.
 */

public class DailySelfieListAdapter extends BaseAdapter{

    private ArrayList<DailySelfieRecord> mSelfieRecordList = new ArrayList<>();
    private static LayoutInflater inflater = null;
    private Context mContext;

    public DailySelfieListAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);

        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            File[] selfieFiles = storageDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return name.endsWith(".jpg");
                }
            });

            for (File file : selfieFiles) {
                DailySelfieRecord selfieRecord = new DailySelfieRecord(file.getAbsolutePath(), file.getName());
                mSelfieRecordList.add(selfieRecord);
            }
        }
    }

    @Override
    public int getCount() {
        return mSelfieRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSelfieRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View newView = convertView;
        final SelfieRecordView selfieRecordView;
        final DailySelfieRecord currentRecord = mSelfieRecordList.get(position);

        if (null == convertView) {
            selfieRecordView = new SelfieRecordView();
            newView = inflater.inflate(R.layout.daily_selfie_list_item, parent, false);
            selfieRecordView.checkBoxSelected = (CheckBox) newView.findViewById(R.id.checkbox_selected);
            selfieRecordView.thumbnail = (ImageView) newView.findViewById(R.id.thumbnail);
            selfieRecordView.selfieDate = (TextView) newView.findViewById(R.id.selfie_date);
            newView.setTag(selfieRecordView);
        }else {
            selfieRecordView = (SelfieRecordView) newView.getTag();
        }
        selfieRecordView.checkBoxSelected.setChecked(currentRecord.ismSelected());
        selfieRecordView.checkBoxSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentRecord.setmSelected(isChecked);
            }
        });

        DailySelfieImageHelper.setImageFromFilePath(currentRecord.getmPath(), selfieRecordView.thumbnail);
        selfieRecordView.selfieDate.setText(currentRecord.getDisplayName());

        return newView;
    }

    static class SelfieRecordView {
        CheckBox checkBoxSelected;
        ImageView thumbnail;
        TextView selfieDate;
    }

    public void add(DailySelfieRecord selfieRecord) {
        mSelfieRecordList.add(selfieRecord);
        notifyDataSetChanged();
    }

    public ArrayList<DailySelfieRecord> getAllRecords() {
        return mSelfieRecordList;
    }

    public ArrayList<DailySelfieRecord> getSelectedRecords() {
        ArrayList<DailySelfieRecord> mSelectedRecordList = new ArrayList<>();
        for (DailySelfieRecord record : mSelfieRecordList) {
            if (record.ismSelected()) {
                mSelectedRecordList.add(record);
            }
        }
        return mSelectedRecordList;
    }

    public void clearAll() {
        mSelfieRecordList.clear();
        notifyDataSetChanged();
    }

    public void clearSelected() {
        mSelfieRecordList.removeAll(getSelectedRecords());
        notifyDataSetChanged();
    }
}
