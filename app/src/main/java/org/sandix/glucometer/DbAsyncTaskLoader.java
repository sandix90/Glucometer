package org.sandix.glucometer;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.sandix.glucometer.beans.Bean;

import java.util.List;

/**
 * Created by sandakov.a on 19.04.2016.
 */
public class DbAsyncTaskLoader extends AsyncTaskLoader<List<Bean>> {

    public DbAsyncTaskLoader(Context context){
        super(context);
    }
    @Override
    public List<Bean> loadInBackground() {
        return null;

    }

}
