package org.mf.bookkeeping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.mf.bookkeeping.database.DatabaseHelper;
import org.mf.bookkeeping.database.UserTable;
import org.mf.bookkeeping.models.User;

public class LoginActivity extends AppCompatActivity {

    public static final int LOGIN_CODE = 2;
    private static User loginUser;

    private boolean createMain = false;

    public static User getLoginUser() {
        return loginUser;
    }

    public static void setLoginUser(User loginUser) {
        LoginActivity.loginUser = loginUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.createMain = getIntent().getBooleanExtra("logout", false);

        EditText accountET = findViewById(R.id.login_account);
        EditText passwordET = findViewById(R.id.login_password);
        ProgressBar loading = findViewById(R.id.loading);
        TextView fail = findViewById(R.id.error_message);
        UserTable userHelper = DatabaseHelper.getUserHelper();

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            fail.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);

            int account = Integer.parseInt(accountET.getText().toString());
            String password = passwordET.getText().toString();
            User user = userHelper.getByPassword(account, password);
            if (user != null) {
                MainActivity.getSharedPreferences().edit().putInt("user_id", user.getId()).apply();
                setLoginUser(user);
                this.toMain();
            } else {
                fail.setText("账号或密码错误");
                loading.setVisibility(View.GONE);
                fail.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_register).setOnClickListener(v -> {
            fail.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);

            int account = Integer.parseInt(accountET.getText().toString());
            String password = passwordET.getText().toString();
            User user = userHelper.getByAccount(account);
            if (user == null) {
                User newUser = new User("暂无", account, password);
                MainActivity.getSharedPreferences().edit().putInt("user_id", (int) userHelper.insert(newUser)).apply();
                setLoginUser(newUser);
                this.toMain();
            } else {
                fail.setText("账号已存在");
                loading.setVisibility(View.GONE);
                fail.setVisibility(View.VISIBLE);
            }
        });
    }

    private void toMain() {
        if (this.createMain) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Intent intent = new Intent();
            intent.putExtra("code", LOGIN_CODE);
            setResult(RESULT_OK, intent);
        }
        finish();
    }
}