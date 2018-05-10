package com.example.william.speechpad;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class ListarLembretes extends FragmentActivity implements ViewPager.OnPageChangeListener{

    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private String telaAnteriorTabs;
    private CharSequence Titulos[]={"Expirados","Hoje","Futuros"};
    private int NumTabs=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_lembretes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        telaAnteriorTabs = getIntent().getStringExtra("telaAnterior");
        adapter = new ViewPagerAdapter(this, getSupportFragmentManager(), Titulos, NumTabs);
        pager = (ViewPager) findViewById(R.id.paginas);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);
        pager.setOnPageChangeListener(this);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabSrollColor);
            }
        });

        if(telaAnteriorTabs.equals("lembreteTab1"))
        {
            pager.setCurrentItem(0);
        }
        else if (telaAnteriorTabs.equals("lembreteTab2"))
        {
            pager.setCurrentItem(1);
        }
        else if (telaAnteriorTabs.equals("lembreteTab3"))
        {
            pager.setCurrentItem(2);
        }
        else if(telaAnteriorTabs.equals("menu"))
        {
            pager.setCurrentItem(1);
        }
        else
        {
            pager.setCurrentItem(1);
        }

        tabs.setViewPager(pager);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if(keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            startActivity(new Intent(ListarLembretes.this,MenuInicial.class));
            ListarLembretes.this.finish();
            return true;
        }
        return super.onKeyDown(keycode, event);
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listar_lembretes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
