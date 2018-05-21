package net.javango.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class MovieActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MovieFragment();
    }

}