package myproject.spendeefilemanager.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.activity.base.BaseActivity;
import myproject.spendeefilemanager.fragment.DefaultFolderFragment;
import myproject.spendeefilemanager.manager.FileManager;

public class DefaultFolderActivity extends BaseActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_folder);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setUpAppBar();

        if (savedInstanceState == null) {
            Fragment myFragment = DefaultFolderFragment.newInstance(FileManager.getInstance().getStartUrl(this));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_default_folder, myFragment)
                    .commit();
        }
    }

    private void setUpAppBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.default_folder));
    }
}
