package dailyselfie.part2.coursera.com.dailyselfie;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Naik Junior on 2/18/2017.
 */

public class DailySelfieRecord {

    private String mPath;
    private String mName;
    private boolean mSelected;

    public DailySelfieRecord(String mPath, String mName) {
        this.mPath = mPath;
        this.mName = mName;
        mSelected = false;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public boolean ismSelected() {
        return mSelected;
    }

    public void setmSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getDisplayName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = format.parse(mName, new ParsePosition(0));
        return new SimpleDateFormat("dd MMM, yyyy HH:mm:ss").format(date);
    }

}
