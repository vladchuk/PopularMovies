package net.javango.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Generic single fragment container.
 * <p/>Must be used together with <b>activity_fragment.xml</b> layout:
 * <pre>
 *  < FrameLayout
 *      xmlns:android="http://schemas.android.com/apk/res/android"
 *      android:id="@+id/fragment_container"
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"/>
 * </pre>
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

}
