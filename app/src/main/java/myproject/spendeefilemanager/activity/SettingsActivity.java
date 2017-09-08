package myproject.spendeefilemanager.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.activity.base.BaseActivity;
import myproject.spendeefilemanager.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setUpAppBar();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_settings, new SettingsFragment())
                    .commit();
        }
    }

    private void setUpAppBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.settings));
    }
}
