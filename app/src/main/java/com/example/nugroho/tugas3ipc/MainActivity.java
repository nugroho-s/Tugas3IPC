package com.example.nugroho.tugas3ipc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static int BLACK = Color.rgb(0, 0, 0);
    private static int WHITE = Color.rgb(255, 255, 255);
    private static int RED = Color.rgb(255, 0, 0);

    ImageView imageView;
    ImageView imageView2;
    TextView textView;
    SeekBar seekBarThreshold;
    Bitmap wajahBitmap;
    Bitmap srcBorder;
    Bitmap bitmapBorder;
    int threshold;
    char[][] bwArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        imageView = (ImageView) findViewById(R.id.imageContent);
        textView = (TextView) findViewById(R.id.textContent);
        imageView2 = (ImageView) findViewById(R.id.imageContent2);
        seekBarThreshold = (SeekBar) findViewById(R.id.seekBarThreshold);
        seekBarThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                threshold = seekBar.getProgress();
                if(wajahBitmap!=null){
                    bwArr =new char[wajahBitmap.getHeight()][wajahBitmap.getWidth()];
                    Bitmap bw = convertToBw(threshold);
                    imageView.setImageBitmap(bw);
                    int jumlahKomponen = hitungKomponen();
                    textView.setText("Jumlah komponen = "+jumlahKomponen);
                } else {
                    bitmapBorder = Bitmap.createBitmap(srcBorder.getWidth(), srcBorder.getHeight(), Bitmap.Config.ARGB_8888);
                    photoTracing();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_angka1) {
            srcBorder = BitmapFactory.decodeResource(this.getResources(), R.drawable.gc);
            imageView.setImageBitmap(srcBorder);
            bitmapBorder = Bitmap.createBitmap(srcBorder.getWidth(), srcBorder.getHeight(), Bitmap.Config.ARGB_8888);
            Log.d("sizing","w,h = "+srcBorder.getWidth()+","+srcBorder.getHeight());
            threshold=50;
            photoTracing();
        } else if (id == R.id.nav_angka2) {

        } else if (id == R.id.nav_angka3) {

        } else if (id == R.id.nav_angka4) {

        } else if (id == R.id.nav_wajah1) {
            wajahBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.wajah1);
        } else if (id == R.id.nav_wajah2) {
            wajahBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.square_equalized);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Bitmap convertToBw(int threshold){
        Bitmap bw = Bitmap.createBitmap(wajahBitmap.getWidth(),wajahBitmap.getHeight(), Bitmap.Config.RGB_565);
        int height = wajahBitmap.getHeight();
        int width = wajahBitmap.getWidth();
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                int pixelColor = wajahBitmap.getPixel(x,y);
                int r = Color.red(pixelColor);
                int g = Color.green(pixelColor);
                int b = Color.blue(pixelColor);
                int grey = (r+g+b)/3;
                bw.setPixel(x,y,(grey<threshold)?BLACK:WHITE);
                bwArr[y][x] = (grey<threshold)?(char)0:1;
            }
        }
        return bw;
    }

    public char numberDetection(NumberDetector numberDetector, int x, int y){
        List<Character> directions = numberDetector.getObjectChainCode(x,y,bitmapBorder);
        ArrayList<SimplifiedDirection> simplifiedDirections = new ArrayList<>();
        int[] directionCount = new int[8];
        int prevDir=-1;
        SimplifiedDirection simplifiedDirection = null;
        for(char dir:directions){
            if(dir!=prevDir){
                if(prevDir!=-1){
                    simplifiedDirections.add(simplifiedDirection);
                }
                prevDir=dir;
                simplifiedDirection = new SimplifiedDirection(dir,0);
            }
            simplifiedDirection.count++;
        }
        simplifiedDirections.add(simplifiedDirection);
        for(SimplifiedDirection sd:simplifiedDirections){
            Log.d("direction",(int)(sd.direction)+":"+sd.count);
        }
        return numberDetector.detectChar(simplifiedDirections);
    }

    public int hitungKomponen(){
        int height = bwArr.length;
        int width = bwArr[0].length;
        int ret = 0;
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                if(bwArr[y][x]==0){
                    if (fillArea(bwArr,x,y,(char)0,(char)1))
                        ret++;
//                    Log.d("filling",x+","+y+":"+(int)bwArr[y][x]);
                }
            }
        }
        return ret;
    }

    public boolean fillArea (char[][] array,int x, int y, char original, char fill){
//        if (x != 0)
//            x--;
//        if (y!= 0)
//            y--;
        Queue<Point> queue = new LinkedList<Point>();
        int height = array.length;
        int width = array[0].length;
        if (array[y][x] != original){
            return false;
        }
        queue.add(new Point(x, y));

        int count = 0;
        int totalPixel = height*width;

        while (!queue.isEmpty()){
            Point p = queue.remove();
            if((p.y<height)&&(p.x<width)&&(p.y>=0)&&(p.x>=0)){
                if (array[p.y][p.x] == original){
                    array[p.y][p.x] = fill;
                    count++;
                    queue.add(new Point(p.x-1, p.y));
                    queue.add(new Point(p.x+1, p.y));
                    queue.add(new Point(p.x, p.y-1));
                    queue.add(new Point(p.x, p.y+1));
                    queue.add(new Point(p.x+1,p.y+1));
                    queue.add(new Point(p.x+1,p.y-1));
                    queue.add(new Point(p.x-1,p.y-1));
                    queue.add(new Point(p.x-1,p.y+1));
                }
            }
        }

        int limit = totalPixel/10000;

        return (count>=limit)?true:false;
    }

    public void photoTracing(){
        char[][] array = new char[srcBorder.getHeight()][srcBorder.getWidth()];
        for(int x=0;x<srcBorder.getWidth();x++){
            for(int y=0;y<srcBorder.getHeight();y++){
                if(getGrey(srcBorder.getPixel(x,y))>threshold){
                    array[y][x] = 0;
                } else {
                    array[y][x] = 1;
                }
            }
        }

        boolean stop = false;
        int x=0,y=0;
        NumberDetector numberDetector = new NumberDetector(array);
        int[] result = new int[2];
        for(y=0;(y<array.length)&&!stop;y++){
            for(x=0;(x<array[0].length)&&!stop;x++){
                if(array[y][x]==1){
                    char res = numberDetection(numberDetector,x,y);
                    if((res=='1')||(res=='0'))
                        result[Character.getNumericValue(res)]++;
                    Log.d("resultBorder",""+res);
                }
            }
        }
        textView.setText("0:"+result[0]+"\n1:"+result[1]);
        imageView2.setImageBitmap(bitmapBorder);
    }

    int getGrey(int color){
        return (Color.red(color)+Color.green(color)+Color.blue(color))/3;
    }
}
