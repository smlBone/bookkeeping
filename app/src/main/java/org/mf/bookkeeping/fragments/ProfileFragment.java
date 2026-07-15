package org.mf.bookkeeping.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.mf.bookkeeping.LoginActivity;
import org.mf.bookkeeping.MainActivity;
import org.mf.bookkeeping.R;
import org.mf.bookkeeping.database.DatabaseHelper;
import org.mf.bookkeeping.models.User;

import java.io.File;

public class ProfileFragment extends SuperFragment<MainActivity> {
    private ImageView portraitView;

    public ProfileFragment(MainActivity master) {
        super(master);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onViewCreated() {
        TextView showUsername = findViewById(R.id.show_username);
        TextView showAccount = findViewById(R.id.show_account);
        this.portraitView = findViewById(R.id.portrait);

        showUsername.setText(LoginActivity.getLoginUser().getName());
        showAccount.setText(String.valueOf(LoginActivity.getLoginUser().getAccount()));
        loadPortrait();
        this.portraitView.setOnClickListener(v -> super.master.pickImage());
        findViewById(R.id.username).setOnClickListener(v -> {
            EditText inputEditText = new EditText(super.master);
            inputEditText.setHint("请输入新昵称");
            new AlertDialog.Builder(super.master)
                    .setTitle("重命名昵称")
                    .setView(inputEditText)
                    .setPositiveButton("确定", ((dialog, which) -> {
                        String name = inputEditText.getText().toString().trim();
                        if (name.isEmpty()) {
                            Toast.makeText(super.master, "无效的昵称", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        User loginUser = LoginActivity.getLoginUser();
                        loginUser.setName(name);
                        DatabaseHelper.getUserHelper().update(loginUser.getId(), loginUser);
                        showUsername.setText(name);
                    }))
                    .setNegativeButton("取消", null)
                    .show();
        });
        findViewById(R.id.change_password).setOnClickListener(v -> {
            final EditText inputEditText = new EditText(super.master);
            inputEditText.setHint("请输入新密码");
            inputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            new AlertDialog.Builder(super.master)
                    .setTitle("修改密码")
                    .setView(inputEditText)
                    .setPositiveButton("确定", ((dialog, which) -> {
                        String password = inputEditText.getText().toString().trim();
                        if (password.isEmpty()) {
                            Toast.makeText(super.master, "无效的密码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        User loginUser = LoginActivity.getLoginUser();
                        loginUser.setPassword(password);
                        DatabaseHelper.getUserHelper().update(loginUser.getId(), loginUser);
                    }))
                    .setNegativeButton("取消", null)
                    .show();
        });
        findViewById(R.id.logout).setOnClickListener(v ->
            new AlertDialog.Builder(super.master)
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定", (dialog, which) -> this.logout())
                    .setNegativeButton("取消", null)
                    .show()
        );
        findViewById(R.id.revoke).setOnClickListener(v -> {
            final int[] countdown = {6};
            final TextView messageView = new TextView(super.master);
            messageView.setPadding(48, 32, 48, 32);
            messageView.setTextSize(16);

            AlertDialog dialog = new AlertDialog.Builder(super.master)
                    .setTitle("确认注销")
                    .setView(messageView)
                    .setPositiveButton("确定", null)
                    .setNegativeButton("取消", null)
                    .create();

            dialog.setOnShowListener(d -> {
                final android.widget.Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                final android.content.Context master = super.master;
                final Handler handler = new Handler(android.os.Looper.getMainLooper());
                positiveButton.setEnabled(false);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        countdown[0]--;
                        if (countdown[0] > 0) {
                            String text = "确定要注销该账号吗？此操作不可撤销！( " + countdown[0] + " )";
                            messageView.setText(text);
                            handler.postDelayed(this, 1000);
                        } else {
                            messageView.setText("确定要注销该账号吗？此操作不可撤销！");
                            positiveButton.setEnabled(true);
                            positiveButton.setOnClickListener(click -> {
                                int deleted = DatabaseHelper.getUserHelper().delete(LoginActivity.getLoginUser().getId());
                                if (deleted > 0) {
                                    Toast.makeText(master, "注销成功", Toast.LENGTH_SHORT).show();
                                    logout();
                                } else {
                                    Toast.makeText(master, "注销失败", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            });
                        }
                    }
                });
            });
            dialog.show();
        });
        findViewById(R.id.export_record).setOnClickListener(v -> super.master.exportRecord());
        findViewById(R.id.import_record).setOnClickListener(v -> super.master.importRecord());
    }

    private void logout() {
        MainActivity.getSharedPreferences().edit().clear().apply();
        LoginActivity.getLoginUser().setId(-1);
        startActivity(new Intent(super.master, LoginActivity.class).putExtra("logout", true));
        super.master.finish();
    }

    private void loadPortrait() {
        String portraitPath = LoginActivity.getLoginUser().getPortraitPath();
        if (portraitPath != null && !portraitPath.isEmpty()) {
            File portraitFile = new File(portraitPath);
            if (portraitFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(portraitPath);
                if (bitmap != null) {
                    portraitView.setImageBitmap(bitmap);
                } else {
                    Log.w("头像设置", "读取为图片失败");
                }
            } else {
                Log.w("头像设置", "图片路径为无效路径");
            }
        } else {
            Log.w("头像设置", "图片路径为空: " + portraitPath);
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        portraitView.setImageBitmap(bitmap);
    }
}
