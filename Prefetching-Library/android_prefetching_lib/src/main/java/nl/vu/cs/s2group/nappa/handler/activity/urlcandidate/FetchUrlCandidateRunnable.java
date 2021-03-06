package nl.vu.cs.s2group.nappa.handler.activity.urlcandidate;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.room.NappaDB;
import nl.vu.cs.s2group.nappa.room.dao.UrlCandidateDao;

/**
 * Defines a runnable to fetch in the database a object containing the {@link
 * UrlCandidateDao.UrlCandidateToUrlParameter} for the provided node.  After
 * fetching the data, this handler will register the LiveData object for the
 * provide node to ensure consistency with the database.
 */
public class FetchUrlCandidateRunnable implements Runnable {
    private static final String LOG_TAG = FetchUrlCandidateRunnable.class.getSimpleName();

    ActivityNode activity;

    public FetchUrlCandidateRunnable(ActivityNode activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, activity.getActivitySimpleName() + " Fetching URL candidates");

        LiveData<List<UrlCandidateDao.UrlCandidateToUrlParameter>> liveData = NappaDB.getInstance()
                .urlCandidateDao()
                .getCandidatePartsListLiveDataForActivity(activity.getActivityId());

        new Handler(Looper.getMainLooper()).post(() -> activity.setUrlCandidateDbLiveData(liveData));

    }
}
