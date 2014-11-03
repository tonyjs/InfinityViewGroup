package com.tonyjs.infinityviewgroup;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tonyjs.infinityviewgroup.lib.InfinityPagerAdapter;
import com.tonyjs.infinityviewgroup.lib.InfinityViewGroup;

import java.util.ArrayList;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InfinityViewGroup viewGroup = (InfinityViewGroup) findViewById(R.id.infinity_viewgroup);
        viewGroup.setAdapter(new ViewPagerAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class ViewPagerAdapter extends InfinityPagerAdapter {
        int colors[] = new int[]{Color.RED, Color.BLACK, Color.BLUE, Color.GREEN, Color.LTGRAY, Color.DKGRAY};
        ArrayList<Colors> items = new ArrayList<Colors>();

        public ViewPagerAdapter() {
            items.add(new Colors(colors[0], "RED"));
            items.add(new Colors(colors[4], "LTGRAY"));
            items.add(new Colors(colors[1], "BLACK"));
            items.add(new Colors(colors[2], "BLUE"));
            items.add(new Colors(colors[3], "GREEN"));
            items.add(new Colors(colors[5], "DKGRAY"));
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = getLayoutInflater().inflate(R.layout.item_pager, container, false);
            TextView tvName = (TextView) v.findViewById(R.id.tv_title);

            Colors item = items.get(position);
            v.setBackgroundColor(item.color);
            tvName.setText(Integer.toString(position + 1));
//            tvName.setText(item.name);
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position, boolean addToFirst) {
            if (addToFirst) {
                View v = getLayoutInflater().inflate(R.layout.item_pager, container, false);
                TextView tvName = (TextView) v.findViewById(R.id.tv_title);

                Colors item = items.get(position);
                v.setBackgroundColor(item.color);
                tvName.setText(Integer.toString(position));
                container.addView(v, 0);
                return v;
            }
            return instantiateItem(container, position);
        }

        private class Colors {
            int color;
            String name;

            private Colors(int color, String name) {
                this.color = color;
                this.name = name;
            }
        }
    }
}
