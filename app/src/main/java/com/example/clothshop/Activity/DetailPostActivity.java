package com.example.clothshop.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.clothshop.Activity.ScrollView.DetailRefreshLayout;
import com.example.clothshop.Activity.ScrollView.DetailScrollView;
import com.example.clothshop.Fragment.HomeFragment;
import com.example.clothshop.Info.CommentsInfo;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.adapter.ImagePagerAdapter;
import com.example.clothshop.utils.HttpPostUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DetailPostActivity extends AppCompatActivity{

    private ViewPager mImageViewPager;
    private DetailScrollView mDetailScrollView;
    private String[] imageList;
    private ImageView imageView;
    private ImageView[] imageViews;
    private PostInfo mPostInfo;
    private ArrayList<CommentsInfo> mCommentsInfoList;

    private DetailRefreshLayout mSwipeRefreshLayout;

    //包裹点点的LinearLayout
    private ViewGroup mPointGroup;

    private ImagePagerAdapter mImagePagerAdapter;

    private TextView mDetailTitle;
    private TextView mDetailContent;
    private TextView mDetailUname;
    private TextView mDetailUage;
    private TextView mDetailUweight;
    private TextView mDetailUheight;
    private TextView mDetailDateTime;
    private TextView mDetailUsex;

    private RecyclerView mDetailCommentRecyclerView;

    private GetDataHandler handler;
    private GetDataThread mGetDataThread;

    private GetCommentHanlder mCommentHandler;
    private GetCommentThread mGetCommentThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_detail_post);
        initToolbar();
        getData();
        initLayout();
    }

    public void initStatusBar(){
        //状态栏透明
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }


    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        lp.setMargins(0,getStatusBarHeight(), 0, 0);
        toolbar.setLayoutParams(lp);
        setSupportActionBar(toolbar);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initLayout(){
        //refreshlayout
        mSwipeRefreshLayout= (DetailRefreshLayout)findViewById(R.id.detail_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetDataThread getDataThread=new GetDataThread();
                getDataThread.start();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        //viewPager
        mImageViewPager= (ViewPager) findViewById(R.id.detail_view_pager);
        imageList=new String[]{};
        mImagePagerAdapter=new ImagePagerAdapter(imageList,DetailPostActivity.this);
        mImageViewPager.setAdapter(mImagePagerAdapter);
        mPointGroup= (ViewGroup) findViewById(R.id.detail_point_view_Group);
        //text
        mDetailTitle= (TextView) findViewById(R.id.detail_title);
        mDetailContent= (TextView) findViewById(R.id.detail_content);
        mDetailUname= (TextView) findViewById(R.id.detail_uname);
        mDetailUage= (TextView) findViewById(R.id.detail_uage);
        mDetailUweight= (TextView) findViewById(R.id.detail_uweight);
        mDetailUheight= (TextView) findViewById(R.id.detail_uheight);
        mDetailDateTime= (TextView) findViewById(R.id.detail_date_time);
        mDetailUsex=(TextView) findViewById(R.id.detail_usex);
        //scrollView
        mDetailScrollView= (DetailScrollView) findViewById(R.id.detail_scroll_view);
        mDetailScrollView.setmViewPager(mImageViewPager);
        //commentList
        mDetailCommentRecyclerView= (RecyclerView) findViewById(R.id.detail_comment_recycler_view);

    }


    private void setPostInfoView(){
        mDetailTitle.setText(mPostInfo.getPtitle());
        mDetailContent.setText(mPostInfo.getPcontent());
        mDetailUweight.setText(mPostInfo.getUweight());
        mDetailUheight.setText(mPostInfo.getUheight());
        mDetailDateTime.setText(mPostInfo.getPdaytime());
        mDetailUage.setText(mPostInfo.getUage());
        mDetailUsex.setText(mPostInfo.getUsex());
    }

    private void setViewPager(){
        imageList = mPostInfo.getPimage().split("@");
        mImagePagerAdapter=new ImagePagerAdapter(imageList,DetailPostActivity.this);
        mImageViewPager.setAdapter(mImagePagerAdapter);
        mImageViewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    /**
     * 初始化导航小白点，根据getData的图片数目设置小白点数目
     */
    private void initPointer() {
        //有多少个界面就new多长的数组
        mPointGroup.removeAllViews();
        imageViews = new ImageView[imageList.length];
        for (int i = 0; i < imageViews.length; i++) {
            imageView = new ImageView(this);
            //设置控件的宽高
            //imageView.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
            //设置控件的padding属性
            LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(20, 20);
            lp.leftMargin=10;
            lp.rightMargin=10;
            imageView.setLayoutParams(lp);
            imageViews[i] = imageView;
                         //初始化第一个page页面的图片的原点为选中状态
            if (i == 0) {
                //表示当前图片
                imageViews[i].setBackgroundResource(R.drawable.point_focused);
                /**
                 * 在java代码中动态生成ImageView的时候
                 * 要设置其BackgroundResource属性才有效
                 * 设置ImageResource属性无效
                 */
            } else {
                imageViews[i].setBackgroundResource(R.drawable.point_not_focused);
            }
            mPointGroup.addView(imageViews[i]);
        }
    }

    /**
     * 获取帖子（post）的数据
     */
    private void getData(){
        mPostInfo=new PostInfo();
        Intent intent=getIntent();
        mPostInfo.setPid(intent.getStringExtra("pid"));
        handler=new GetDataHandler();
        mGetDataThread=new GetDataThread();
        mSwipeRefreshLayout.setRefreshing(true);
        mGetDataThread.start();
    }

    /**
     * 获取评论数据
     */
    private void getCommentData(){
        mCommentsInfoList=new ArrayList<CommentsInfo>();
        if (mPostInfo.getPid().isEmpty()){
            // TODO: 2017/4/6 other oeration? 
            return;
        }
        mCommentHandler=new GetCommentHanlder();
        mGetCommentThread=new GetCommentThread();
        // TODO: 2017/4/6 refresh?
        mGetCommentThread.start();

    }

    class GetCommentThread extends Thread{
        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String, String>();
            params.put(Model.POST_ID_ATTR,mPostInfo.getPid());
            String result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.DETAIL_POST_PATH);
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (!jsonObject.getString("status").equals("0")){
                    showMessage(jsonObject.getString("mes"), GetCommentHanlder.FAILURE);
                }
                JSONArray jsonArray=jsonObject.getJSONArray("post");
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jo= (JSONObject) jsonArray.get(i);
                    CommentsInfo commentsInfo=new CommentsInfo();
                    commentsInfo.setCcontent(jo.getString(Model.COMMENT_CONTENT));
                    commentsInfo.setCtime(jo.getString(Model.COMMENT_TIME));
                    mCommentsInfoList.add(commentsInfo);
                }
                showMessage(jsonObject.getString("mes"),GetCommentHanlder.SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
                showMessage(e.toString(),GetCommentHanlder.FAILURE);
            }
        }
        private void showMessage(String message,int status){
            Message msg=Message.obtain(mCommentHandler,status);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(mCommentHandler);
        }
    }

    public class GetCommentHanlder extends Handler{
        public static final int FAILURE=0x0002;
        public static final int SUCCESS=0x0001;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    break;
                case FAILURE:
                    break;
                default:
                    break;
            }
        }
    }



    class GetDataThread extends Thread{
        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String, String>();
            params.put(Model.POST_ID_ATTR,mPostInfo.getPid());
            String result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.DETAIL_POST_PATH);
            try {
                JSONObject jsonObject=new JSONObject(result);
                mPostInfo.setPimage(jsonObject.getString(Model.POST_IMAGE_ATTR));
                mPostInfo.setPtitle(jsonObject.getString(Model.POST_TITLE_ATTR));
                mPostInfo.setUid(jsonObject.getString(Model.POST_UID_ATTR));
                mPostInfo.setPdaytime(jsonObject.getString(Model.POST_DAY_TIME_ATTR));
                mPostInfo.setPcontent(jsonObject.getString(Model.POST_CONTENT_ATTR));
                mPostInfo.setUheight(jsonObject.getString(Model.POST_UHEIGHT_ATTR));
                mPostInfo.setUweight(jsonObject.getString(Model.POST_UWEIGHT_ATTR));
                mPostInfo.setUsex(jsonObject.getString(Model.POST_USEX_ATTR));
                mPostInfo.setUage(jsonObject.getString(Model.POST_UAGE_ATTR));
                if (jsonObject.getString("status").equals("0")){
                    showMessage(jsonObject.getString("mes"), GetDataHandler.SUCCESS);
                }else {
                    showMessage(jsonObject.getString("mes"), GetDataHandler.FAILURE);
                }
            } catch (JSONException e) {
                showMessage(e.toString(), GetDataHandler.FAILURE);
                e.printStackTrace();
            }


        }

        /**
         * 获取帖子数据的thread
         * @param message
         * @param status
         */
        private void showMessage(String message,int status){
            Message msg=Message.obtain(handler,status);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(handler);
        }
    }

    /**
     * 帖子数据的handler
     */
    public class GetDataHandler extends Handler {

        public static final int FAILURE=0x0002;
        public static final int SUCCESS=0x0001;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    mSwipeRefreshLayout.setRefreshing(false);
                    setPostInfoView();
                    setViewPager();
                    initPointer(); //获取数据后初始化小白点
                    mSwipeRefreshLayout.setmViewPager(mImageViewPager);
                    mSwipeRefreshLayout.setmDetailScrollView(mDetailScrollView);
                    break;
                case FAILURE:
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(DetailPostActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    }

    public class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        //页面滑动完成后执行
        @Override
        public void onPageSelected(int position) {
            //判断当前是在那个page，就把对应下标的ImageView原点设置为选中状态的图片
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[position].setBackgroundResource(R.drawable.point_focused);
                if (position != i) {
                    imageViews[i].setBackgroundResource(R.drawable.point_not_focused);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
