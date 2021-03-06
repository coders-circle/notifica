package com.toggle.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.toggle.notifica.database.Client;
import com.toggle.notifica.database.DbHelper;
import com.toggle.notifica.database.NetworkHandler;
import com.toggle.notifica.database.NextPeriodFinder;
import com.toggle.notifica.database.PClass;
import com.toggle.notifica.database.PGroup;
import com.toggle.notifica.database.Profile;
import com.toggle.notifica.database.Student;
import com.toggle.notifica.database.Subject;
import com.toggle.notifica.database.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Menu mMenu;
    private DbHelper mDbHelper;
    private PClass mClass = null;
    private User mUser = null;

    private AppBarLayout mAppBarLayout;

    private RoutineFragment mRoutineFragment;
    private NewsFeedFragment mNewsFeedFragment;

    private ActionBarDrawerToggle mDrawerToggle;

    boolean isVisible = true;
    boolean swap = true;

    private int mCurrentPage = R.id.news_feed;

    public PClass getPClass() { return mClass; }

    NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener;

    public boolean getFirstTimeData() {
        if (!NetworkHandler.isNetworkAvailable(this))
            return false;

        // Also get all student and teacher profiles associated with the account
        Client client = new Client(this);
        client.getAssociated("student", mUser._id, new Client.ClientListener() {
            @Override
            public void refresh(boolean success) {
                if (queue.size() == 0) {
                    if (!success) {
                        ((TextView)findViewById(R.id.wait_textview))
                                .setText("Sorry couldn't connect to server.\nTry again later.");
                        return;

                    }

                    // if still no student data, open class search activity
                    // else reopen main activity
                    Intent intent;
                    if (mUser.getStudent(mDbHelper) == null)
                        intent = new Intent(MainActivity.this, ClassSearchActivity.class);
                    else
                        intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        return true;
    }

    public void initializeApp() {
        // Initialize database

        // Clean up unnecessary cache data
        mDbHelper.clean();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Client.ClientListener refreshCallback = new Client.ClientListener() {
                    @Override
                    public void refresh(boolean success) {
                        if (queue.size() == 0) {
                            if (mMenu != null)
                                refreshMenuItems();
                            // refresh widgets
                            PeriodWidgetProvider.updateAllWidgets(MainActivity.this);
                        }
                    }
                };

                // Download and get new routine
                final Client client = new Client(MainActivity.this);
                client.getRoutine(refreshCallback);

                // Get the latest profile for logged in user
                client.getUser(mUser._id, refreshCallback);
                client.getProfile(mUser.profile, refreshCallback);

                // Also get all student and teacher profiles associated with the account
                client.getAssociated("student", mUser._id, refreshCallback);
                client.getAssociated("teacher", mUser._id, refreshCallback);
            }
        }).run();

    }

    // Set user profile data in header
    private void setHeaderView() {
        // Also set routine header
        fillRoutineHeader();

        // Get views
        View headerView = mNavigationView.getHeaderView(0);
        TextView username = (TextView)headerView.findViewById(R.id.username);
        TextView info = (TextView)headerView.findViewById(R.id.info);
        ImageView avatar = (ImageView)headerView.findViewById(R.id.avatar);

        // Get user profile
        Profile profile = Profile.get(Profile.class, mDbHelper, mUser.profile);
        Student student = mUser.getStudent(mDbHelper);

        // Set header data
        username.setText(mUser.getName());

        // If student set "class (group)" as info text
        if (student != null) {
            PGroup myGroup = student.getGroup(mDbHelper);
            mClass = myGroup.getPClass(mDbHelper);

            String infoText = mClass.class_id + " (" + myGroup.group_id + ")";
            info.setText(infoText);
        }
        // else set email as info text
        else
            info.setText(mUser.email);

        if (profile != null) {
            avatar.setImageBitmap(profile.getAvatar());
            ((GradientDrawable)avatar.getBackground()).setColor(0xFFFFFFFF);
        } else {
            avatar.setBackgroundResource(R.drawable.ic_default_avatar);
        }

    }

    // Add a menu item to a group with given id, name and icon
    private MenuItem addMenuItem(int group, int id, String name, int icon) {
        MenuItem menuItem = mMenu.add(group, id, Menu.NONE, name);
        menuItem.setIcon(icon);
        menuItem.setCheckable(true);
        menuItem.setChecked(false);
        return menuItem;
    }

    private void refreshMenuItems() {
        // Add basic items
        mMenu.clear();
        addMenuItem(R.id.basic_group, R.id.news_feed, "News Feed", R.mipmap.news_feed);
        addMenuItem(R.id.basic_group, R.id.routine, "Routine", R.mipmap.routine);
        addMenuItem(R.id.basic_group, R.id.assignment, "Assignments", R.mipmap.assignment);
        addMenuItem(R.id.settings_group, R.id.settings, "Settings", R.mipmap.settings);

        // Get classes
        List<PClass> classes = PClass.getAll(PClass.class, new DbHelper(this));

        // Add menu item for each class
        for (PClass pClass: classes) {
            addMenuItem(R.id.classes_group, 0, pClass.class_id, R.mipmap.ic_launcher);
        }

        // Set items visibility
        mMenu.setGroupVisible(R.id.basic_group, isVisible);
        mMenu.setGroupVisible(R.id.settings_group, isVisible);
        mMenu.setGroupVisible(R.id.classes_group, !isVisible);

        // Set the drawer header contents from the user profile
        try {
            setHeaderView();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swapMenu() {
        final View headerView = mNavigationView.getHeaderView(0);
        final ImageView swapClasses = (ImageView) headerView.findViewById(R.id.class_select);

        isVisible = !isVisible;
        if (swap) {
            swapClasses.setImageResource(R.mipmap.close);
            swap = false;
        } else {
            swapClasses.setImageResource(R.mipmap.swap_class);
            swap = true;
        }
        mMenu.setGroupVisible(R.id.basic_group, isVisible);
        mMenu.setGroupVisible(R.id.settings_group, isVisible);
        mMenu.setGroupVisible(R.id.classes_group, !isVisible);
    }

    // Prepare the navigation drawer menu
    private void prepareMenu() {
        // Add checkable groups to the menu
        mMenu = mNavigationView.getMenu();
        mMenu.setGroupCheckable(R.id.basic_group, true, true);
        mMenu.setGroupCheckable(R.id.settings_group, true, true);

        // Add menu items
        refreshMenuItems();

        // Initialize the swap button in the drawer
        final View headerView = mNavigationView.getHeaderView(0);
        final ImageView swapClasses = (ImageView) headerView.findViewById(R.id.class_select);

        swapClasses.setImageResource(R.mipmap.swap_class);
        ((View)swapClasses.getParent()).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();

                if (mClass != null) {
                    Intent intent1 = new Intent(MainActivity.this, ClassActivity.class);
                    intent1.putExtra("class_id", mClass.class_id);
                    startActivity(intent1);
                }

                // swapMenu();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DbHelper(this);

        // Get GCM token if not available
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString("gcm_token", "");
        if (token.equals("")) {
            Intent intent = new Intent(this, GcmRegisterService.class);
            startService(intent);
        }

        // Send GCM token to server if not already sent
        token = preferences.getString("gcm_token", "");
        if (!token.equals(""))
            if (!preferences.getBoolean("gcm_token_sent", false))
                GcmRegisterService.sendRegistrationToServer(this,  token);

        // If first time, that is without any class data
        // get some data
        mUser = User.getLoggedInUser(this);
        if (mUser == null || mUser.getStudent(mDbHelper) == null) {
            boolean gotData = getFirstTimeData();
            setContentView(R.layout.activity_main_no_class);
            if (!gotData) {
                ((TextView)findViewById(R.id.wait_textview))
                        .setText("Sorry couldn't connect to internet.\nTry again later.");
            }
            return;
        }

        setContentView(R.layout.activity_main);

        initializeApp();

        // Create the fragments once
        mRoutineFragment = new RoutineFragment();
        mNewsFeedFragment = new NewsFeedFragment();

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        fillRoutineHeader();

        // Initialize the navigation drawer
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        // Prepare the menu items on the drawer
        prepareMenu();

        // Handle navigation item selection
        mNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()){
                    case R.id.news_feed:
                        mCurrentPage = menuItem.getItemId();
                        selectedFragment = mNewsFeedFragment;
                        break;
                    case R.id.routine:
                        mCurrentPage = menuItem.getItemId();
                        selectedFragment = mRoutineFragment;
                        mAppBarLayout.setExpanded(false, true);
                        break;
                    case R.id.assignment:
                        break;
                    case R.id.settings:
                        mDrawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivityForResult(intent, 1);
                        return false;
                    default:
                        swapMenu();
                        mDrawerLayout.closeDrawers();
                        String class_id = menuItem.getTitle().toString();
                        Intent intent1 = new Intent(MainActivity.this, ClassActivity.class);
                        intent1.putExtra("class_id", class_id);
                        startActivity(intent1);
                        return false;
                }

                if(selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,
                            selectedFragment).commit();
                    menuItem.setChecked(true);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        };
        mNavigationView.setNavigationItemSelectedListener(mNavigationItemSelectedListener);

        // Initialize Drawer Layout and ActionBarToggle
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.openDrawer, R.string.closeDrawer);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Sync-ing is necessary to show the hamburger icon
        mDrawerToggle.syncState();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Start from new feed page by default
        mCurrentPage = R.id.news_feed;
        // Start from intended page if passed through intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCurrentPage = extras.getInt("start_page", R.id.news_feed);
        }

        if (mNavigationItemSelectedListener != null && mNavigationView != null)
            mNavigationItemSelectedListener.onNavigationItemSelected(
                    mNavigationView.getMenu().findItem(mCurrentPage));
    }

    @Override
    public void onPause() {
        super.onPause();
        getIntent().putExtra("start_page", mCurrentPage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Settings activity telling us to close
        if(requestCode == 1 && resultCode == -1)
            finish();
    }

    public void fillRoutineHeader() {
        DbHelper dbHelper = new DbHelper(this);
        NextPeriodFinder finder = new NextPeriodFinder(dbHelper);

        View view1 = findViewById(R.id.now);
        View view2 = findViewById(R.id.next);

        if (finder.current != null) {
            Subject sub = finder.currentSubject;
            Utilities.fillProfileView(
                    view1, Color.parseColor(sub.color), null, "Current class",
                    sub.name, finder.current.getPeriodString(),
                    finder.current.period_type == 1 ? "Practical" : null, sub.getShortName()
            );
            view1.setVisibility(View.VISIBLE);
            /*findViewById(R.id.now_practical).setVisibility(
                    finder.current.period_type == 1 ? View.VISIBLE : View.GONE);*/
        } else
            view1.setVisibility(View.GONE);

        if (finder.next != null) {
            Subject sub = finder.nextSubject;
            Utilities.fillProfileView(
                    view2, Color.parseColor(sub.color), null, "Next class", sub.name,
                    finder.nextDay + finder.next.getPeriodString(),
                    finder.next.period_type == 1 ? "Practical":null, sub.getShortName()
            );
            view2.setVisibility(View.VISIBLE);
            /*findViewById(R.id.next_practical).setVisibility(
                    finder.next.period_type == 1 ? View.VISIBLE : View.GONE);*/
        } else
            view2.setVisibility(View.GONE);


        int nextMinute = finder.remaining*60*1000; //(60-cal.get(Calendar.SECOND))*1000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fillRoutineHeader();
            }
        }, nextMinute);
    }
}

