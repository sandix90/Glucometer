package org.sandix.glucometer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineDataSet;

import org.sandix.glucometer.adapters.ViewPagerAdapter;
import org.sandix.glucometer.beans.UserBean;
import org.sandix.glucometer.tabFragments.LineDiagram;


public class ActivityChart extends AppCompatActivity {

//    private LineChart mChart;
    private UserBean userBean;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;

//    private int[] tabIcons = {
//            R.drawable.ic_tab_favourite,
//            R.drawable.ic_tab_call,
//            R.drawable.ic_tab_contacts
//    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Динамика пациента mmol/l");

        if(getIntent().hasExtra("userBean")){
            userBean = (UserBean)getIntent().getSerializableExtra("userBean");

        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPages(viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //tabLayout.getTabAt(0).setIcon(tabIcons[0]);


    }

    private void setupViewPages(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle(); //Готовим объект к передаче
        bundle.putSerializable("userBean", userBean);
        LineDiagram lineDiagram = new LineDiagram();
        lineDiagram.setArguments(bundle); //Устанавливаем объект к передаче в фрагмент
        adapter.addFragment(lineDiagram,"Линейная диаграмма");
        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.replace(R.id.view_pager, lineDiagram, "userBean");
        //transaction.commit();

        //Нужно добавлять аргументы к фрагментам, потому во фрагменте они проверяются


        adapter.addFragment(new LineDiagram(),"Тестовая диаграмма");
        adapter.addFragment(new LineDiagram(),"Круговая диаграмма");
        viewPager.setAdapter(adapter);
    }

}


