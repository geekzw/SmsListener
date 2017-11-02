package com.maihaoche.sms;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.maihaoche.sms.entity.Sms;
import com.maihaoche.sms.receiver.MyJobserver;
import com.maihaoche.sms.receiver.MyService;
import com.maihaoche.sms.utils.NetUtil;
import com.maihaoche.sms.utils.SmsDao;
import com.mylhyl.crlayout.SwipeRefreshAdapterView;
import com.mylhyl.crlayout.SwipeRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static android.app.job.JobInfo.NETWORK_TYPE_NONE;

public class MainActivity extends AppCompatActivity implements SwipeRefreshAdapterView.OnListLoadListener, SwipeRefreshLayout.OnRefreshListener {

    private List<Sms> lists;
    private MyAdapter adapter;
    private SwipeRefreshRecyclerView refreshRecyclerView;
    private TextView deleteBatchBtn;
    private TextView deleteBatch;
    private SmsDao smsDao;
    private int limit = 20;
    private int offset = 0;
    private int myJobId = 1;
    private boolean isSelectModel = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(offset == 0){
                refreshRecyclerView.setRefreshing(false);
            }else{
                refreshRecyclerView.setLoading(false);
            }
            adapter.notifyDataSetChanged();

        }
    };

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        scheduleJob();

    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, MyService.class));
        startService(new Intent(this, MyJobserver.class));
    }

    private void initData() {
        lists = new ArrayList<>();
        smsDao = new SmsDao(this);

        startService(new Intent(this, MyService.class));
    }

    private void initView() {
        deleteBatchBtn = (TextView) findViewById(R.id.delete_batch_btn);
        deleteBatch = (TextView) findViewById(R.id.delete_batch);
        refreshRecyclerView = (SwipeRefreshRecyclerView) findViewById(R.id.swipeRefresh);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        refreshRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        refreshRecyclerView.setOnListLoadListener(this);
        refreshRecyclerView.setOnRefreshListener(this);
        adapter = new MyAdapter(this);
        refreshRecyclerView.setAdapter(adapter);

        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,HelpActivity.class));
            }
        });
        findViewById(R.id.delete_batch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelectModel){
                    deleteBatch.setText("批量删除");
                    isSelectModel = !isSelectModel;
                    deleteBatchBtn.setVisibility(View.GONE);
                    for(int i=0;i<lists.size();i++){
                        lists.get(i).setSelect(false);
                    }
                }else{
                    deleteBatch.setText("退出");
                    isSelectModel = !isSelectModel;
                    deleteBatchBtn.setVisibility(View.VISIBLE);

                }
                adapter.notifyDataSetChanged();
            }
        });

        deleteBatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsDao.deleteBatch(getSelect());
                onRefresh();
                Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
            }
        });

        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
        laodData();

    }

    private void laodData() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Sms> ll = smsDao.select(limit,offset);
                if(offset == 0){
                    lists = ll;
                }else{
                    lists.addAll(ll);
                }
                lists = smsDao.select(20, 0);
                handler.sendEmptyMessage(1);

            }
        });

    }

    @Override
    public void onListLoad() {
        offset+=limit;
        laodData();
    }

    @Override
    public void onRefresh() {
        offset = 0;
        laodData();
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(context).inflate(R.layout.main_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyHolder holder, final int position) {
            final Sms sms = lists.get(position);
            holder.phone.setText(sms.getPhone());
            holder.content.setText(sms.getContent());
            holder.time.setText(sms.getFormatData());
            if(isSelectModel){
                holder.selectArea.setVisibility(View.VISIBLE);
                if(sms.isSelect()){
                    holder.select.setBackgroundResource(R.drawable.select);
                }else{
                    holder.select.setBackgroundResource(R.drawable.un_select);
                }
            }else{
                holder.selectArea.setVisibility(View.GONE);
            }

            if(sms.isSendToServer()){
                holder.update.setText("已同步");
                holder.update.setTextColor(ContextCompat.getColor(context,R.color.green));
                holder.update.setEnabled(false);
            }else{
                holder.update.setText("点击同步");
                holder.update.setTextColor(ContextCompat.getColor(context,R.color.red));
                holder.update.setEnabled(true);
            }

            holder.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    NetUtil.sendToServer(context, sms, new NetUtil.NetCallBack() {
                        @Override
                        public void success() {
                            Toast.makeText(context,"同步成功",Toast.LENGTH_SHORT).show();
                            sms.setIsSendToServer(1);
                            notifyItemChanged(position);
                        }

                        @Override
                        public void failed() {
                            Toast.makeText(context,"同步失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smsDao.delete(sms.getSmsId());
                    Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                    lists.remove(position);
                    notifyItemChanged(position);

                }
            });

            holder.selectArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sms.isSelect()){
                        sms.setSelect(false);
                    }else{
                        sms.setSelect(true);
                    }
                    notifyItemChanged(position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            public TextView phone;
            public TextView content;
            public TextView time;
            public TextView delete;
            public TextView update;
            public LinearLayout selectArea;
            public TextView select;


            public MyHolder(View itemView) {
                super(itemView);
                phone = (TextView) itemView.findViewById(R.id.phone);
                content = (TextView) itemView.findViewById(R.id.content);
                time = (TextView) itemView.findViewById(R.id.time);
                delete = (TextView) itemView.findViewById(R.id.delete);
                update = (TextView) itemView.findViewById(R.id.update);
                select = (TextView) itemView.findViewById(R.id.select);
                selectArea = (LinearLayout) itemView.findViewById(R.id.select_area);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob(){
        JobInfo.Builder builder = new JobInfo.Builder(myJobId,new ComponentName(this, MyJobserver.class));
        builder.setPersisted(true);
        builder.setPeriodic(1000*5);
        builder.setRequiredNetworkType(NETWORK_TYPE_NONE);
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(myJobId);
        tm.schedule(builder.build());
    }

    private List<Integer> getSelect(){
        List<Integer> list = new ArrayList<>();
        for(Sms sms:lists){
            if(sms.isSelect()){
                list.add(sms.getId());
            }
        }
        return list;
    }
}
