package com.nebulasnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.nebulas.io.account.Account;
import com.nebulas.io.account.AccountManager;
import com.nebulas.io.wallet.ManagerAccountActivity;
import com.nebulas.io.wallet.WalletInfoFragment;
import com.nebulasnews.news.NewsFragment;
import com.nebulasnews.news.PublishAdFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private Fragment newsFragment;
    private Fragment publishFragment;
    private Fragment walletInfoFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_dashboard:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, publishFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_notifications:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, walletInfoFragment).commitAllowingStateLoss();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment();

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        account = AccountManager.instance().getCurrentAccount();
        if (account == null) {
            startActivityForResult(new Intent(this, ManagerAccountActivity.class),0);
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commitAllowingStateLoss();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        account = AccountManager.instance().getCurrentAccount();
        if (account == null) {
            startActivityForResult(new Intent(this, ManagerAccountActivity.class),0);
            Toast.makeText(this, "请先设置帐号", Toast.LENGTH_SHORT).show();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commitAllowingStateLoss();
        }
    }

    private Account account;
    private FragmentTransaction transaction;

    private void initFragment() {
        newsFragment = new NewsFragment();
        publishFragment = new PublishAdFragment();
        walletInfoFragment = new WalletInfoFragment();

    }

}
