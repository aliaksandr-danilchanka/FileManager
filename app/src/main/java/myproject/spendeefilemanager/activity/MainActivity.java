package myproject.spendeefilemanager.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.activity.base.BaseActivity;
import myproject.spendeefilemanager.fragment.FileManagerFragment;
import myproject.spendeefilemanager.manager.FileManager;

import static myproject.spendeefilemanager.fragment.DefaultFolderFragment.APP_PREFERENCES;
import static myproject.spendeefilemanager.fragment.DefaultFolderFragment.DEFAULT_FOLDER_KEY;


public class MainActivity extends BaseActivity {


    private static final int PERMISSION_REQUEST_CODE = 123;

    private Toolbar mToolbar;
    private FragmentRefreshListener mFragmentRefreshListener;
    private SharedPreferences mSettings;
    private Bundle mBundle;
    private Intent mStarterIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBundle = savedInstanceState;

        mStarterIntent = getIntent();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (hasPermissions()) {
            setFragment();
        } else {
            requestPerms();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults) {
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                allowed = false;
                break;
        }
        if (allowed) {
            finish();
            startActivity(mStarterIntent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showNoStoragePermissionSnackbar();
            } else {
                finish();
            }
        } else {
                finish();
        }
    }

    public void showNoStoragePermissionSnackbar() {
        Snackbar.make(MainActivity.this.findViewById(R.id.activity_view),
                getString(R.string.storage_permission_is_not_granted), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(getApplicationContext(),
                                getString(R.string.open_permission),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(getString(R.string.name_package) + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            finish();
            startActivity(mStarterIntent);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.refresh:
                if (getFragmentRefreshListener() != null) {
                    getFragmentRefreshListener().onRefresh();
                }
                return true;

            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return mFragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.mFragmentRefreshListener = fragmentRefreshListener;
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean hasPermissions() {
        int res = 0;
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void setFragment() {
        String temp;
        if (mSettings.contains(DEFAULT_FOLDER_KEY)) {
            temp = mSettings.getString(DEFAULT_FOLDER_KEY, "");
        } else {
            temp = FileManager.getInstance().getStartUrl(this);
        }

        if (mBundle == null) {
            Fragment fragment = FileManagerFragment.newInstance(temp);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    public interface FragmentRefreshListener {
        void onRefresh();
    }

}
