package materialcalendarview2.sample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import materialcalendarview2.model.DayTime;
import materialcalendarview2.model.Event;
import materialcalendarview2.sample.presenter.MainPresenter;
import materialcalendarview2.sample.view.MainView;
import materialcalendarview2.sample.R;
import materialcalendarview2.widget.MaterialCalendarView;
import materialcalendarview2.widget.DayView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;

import static materialcalendarview2.widget.MaterialCalendarView.OnDayViewClickListener;
import static materialcalendarview2.widget.MaterialCalendarView.OnDayViewStyleChangeListener;
import static materialcalendarview2.widget.MaterialCalendarView.OnMonthChangeListener;
import static materialcalendarview2.util.CalendarUtil.isSameMonth;
import static materialcalendarview2.sample.util.AnimationUtil.animate;

/**
 * @author jonatan.salas
 */
public class MainActivity extends AppCompatActivity implements MainView, OnNavigationItemSelectedListener,
        OnDayViewClickListener, OnMonthChangeListener, OnDayViewStyleChangeListener {
    private static final String DATE_TEMPLATE = "dd/MM/yyyy";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.textview)
    TextView textView;

    @Bind(R.id.calendar_view)
    MaterialCalendarView calendarView;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @NonNull
    private final MainPresenter presenter = new MainPresenter(this);
    private final SimpleDateFormat formatter = new SimpleDateFormat(DATE_TEMPLATE, Locale.getDefault());
    private String todayDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        presenter.addNavigationDrawer();
        presenter.addCalendarView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.setToday();
        presenter.animate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.dettachView();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void prepareNavigationDrawer() {
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void setTodayDate() {
        todayDate = getString(R.string.today) + " " + formatter.format(new Date(System.currentTimeMillis()));
        textView.setText(todayDate);
    }

    @Override
    public void prepareCalendarView() {
        calendarView.shouldAnimateOnEnter(true);
        calendarView.setOnDayViewClickListener(this);
        calendarView.setOnMonthChangeListener(this);
        calendarView.setOnDayViewStyleChangeListener(this);
    }

    @Override
    public void animateViews() {
        animate(fab, getApplicationContext());
        animate(textView, getApplicationContext());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDayViewClick(@NonNull View view, int year, int month, int dayOfMonth, @Nullable List<Event> eventList) {
        final Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
        final String dateString = formatter.format(calendar.getTime());
        Snackbar.make(view, getString(R.string.selected_date) + " " + dateString, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDayViewStyleChange(@NonNull DayView dayView, @NonNull DayTime dayTime) {
        if (dayTime.isCurrentMonth()) {
            dayView.setTextColor(Color.CYAN);
        }

        if (dayTime.isWeekend() || (!dayTime.isCurrentMonth() && dayTime.isWeekend())) {
            dayView.setTextColor(Color.GREEN);
        }

        if (dayTime.getDay() == 7 && dayTime.getMonth() == 2 && dayTime.getYear() == 2016 && dayTime.isCurrentMonth()) {
            dayView.setTextColor(Color.BLACK);
            dayView.setBackgroundColor(Color.CYAN);
        }

        dayView.drawRipples();
    }

    @Override
    public void onMonthChanged(@NonNull View view, int year, int month) {
        final Calendar calender = new GregorianCalendar(year, month - 1, month);
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final String message = getString(R.string.month_display) + " " + month + " " + getString(R.string.year_is) + " " + year;
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();

        if (!isSameMonth(calender, calendar)) {
            textView.setText(getString(R.string.not_actual_month));
        } else {
            textView.setText(todayDate);
        }
    }
}
