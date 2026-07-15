package org.mf.bookkeeping;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import org.mf.bookkeeping.database.DatabaseHelper;
import org.mf.bookkeeping.fragments.AnalysisFragment;
import org.mf.bookkeeping.fragments.HomeFragment;
import org.mf.bookkeeping.fragments.ProfileFragment;
import org.mf.bookkeeping.fragments.SuperFragment;
import org.mf.bookkeeping.models.BillRecord;
import org.mf.bookkeeping.models.User;
import org.mf.bookkeeping.util.AppDate;
import org.mf.bookkeeping.util.CSVUtil;
import org.mf.bookkeeping.util.Category;
import org.mf.bookkeeping.util.Number;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private HomeFragment home;
    private AnalysisFragment analysis;
    private ProfileFragment profile;
    private SuperFragment<MainActivity> currentFragment = null;

    private static MainActivity instance;

    public MainActivity() {
        DatabaseHelper.initialize(this);
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.home = new HomeFragment(
                this,
                v -> {
                    Intent intent = new Intent(this, AddItemActivity.class);
                    intent.putExtra("expenditure", true);
                    this.launch(intent);
                },
                v -> {
                    Intent intent = new Intent(this, AddItemActivity.class);
                    intent.putExtra("expenditure", false);
                    this.launch(intent);
                    return true;
                }
        );
        this.analysis = new AnalysisFragment(this);
        this.profile = new ProfileFragment(this);

        int userId = getSharedPreferences().getInt("user_id", -1);
        if (userId == -1) {
            this.launch(new Intent(this, LoginActivity.class));
        } else {
            LoginActivity.setLoginUser(DatabaseHelper.getUserHelper().getByID(userId));
            Log.i("login", "登录用户: " + userId);
        }

        findViewById(R.id.home_nav).setOnClickListener(v -> this.changeFragment(this.home));
        findViewById(R.id.analysis_nav).setOnClickListener(v -> this.changeFragment(this.analysis));
        findViewById(R.id.profile_nav).setOnClickListener(v -> this.changeFragment(this.profile));

        this.changeFragment(this.home);
    }

    public void changeFragment(SuperFragment<MainActivity> target) {
        if (this.currentFragment != target) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, target);
            transaction.commit();
            this.currentFragment = target;
        }
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                int code = result.getResultCode();
                if (code == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        int intentCode = data.getIntExtra("code", -1);
                        if (intentCode == AddItemActivity.ADD_ITEM_CODE) {
                            this.fromAddItem(data);
                        } else if (intentCode == LoginActivity.LOGIN_CODE) {
                            this.fromLogin();
                        } else {
                            Log.w("registerForActivityResult", "未知的返回码: " + intentCode);
                        }
                    } else {
                        Log.w("registerForActivityResult", "返回数据为空", new NullPointerException());
                    }
                } else if (code != RESULT_CANCELED) {
                    Log.w("registerForActivityResult", "异常返回结果: " + code);
                }
            }
    );
    private final ActivityResultLauncher<Intent> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null && CSVUtil.export(getContentResolver(), uri)) {
                        Toast.makeText(this, "导出成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("exportLauncher", "导出失败，uri为空");
                    }
                }
            }
    );
    private final ActivityResultLauncher<Intent> importLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        List<BillRecord> records = CSVUtil.importFromCSV(getContentResolver(), uri);
                        if (records != null) {
                            for (BillRecord record : records) {
                                this.home.addNewRecord(record.getDate(), record.getCategory(), record.getNote(), record.getAmount());
                            }
                            Toast.makeText(this, "导入成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        saveAndSetPortrait(selectedImageUri);
                    }
                }
            }
    );

    private void fromLogin() {
        Log.i("onActivityResult", "从 LoginActivity 返回");
        this.home.loadData();
    }

    private void fromAddItem(Intent intent) {
        Log.i("onActivityResult", "从 AddItemActivity 返回");
        Category cate = (Category) intent.getSerializableExtra("cate");
        AppDate date = (AppDate) intent.getSerializableExtra("date");
        String note = intent.getStringExtra("note");
        Number cost = (Number) intent.getSerializableExtra("cost");
        if (cate == null) {
            throw new NullPointerException("Category 为空");
        }
        if (date == null) {
            throw new NullPointerException("AppDate 为空");
        }
        if (cost == null) {
            throw new NullPointerException("Category 为空");
        }
        if (note == null) {
            note = "";
            Log.e("registerForActivityResult", "从add item activity获取备注信息失败", new NullPointerException());
        }
        int id = intent.getIntExtra("modifyID", -1);
        if (id != -1) {
            this.home.modifyRecord(id, date, cate, note, cost);
        } else {
            this.home.addNewRecord(date, cate, note, cost);
        }
    }

    public static SharedPreferences getSharedPreferences() {
        return instance.getSharedPreferences("login_state", MODE_PRIVATE);
    }

    public void launch(Intent intent) {
        this.launcher.launch(intent);
    }

    public void exportRecord() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "记账_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date()) + ".csv");
        exportLauncher.launch(intent);
    }

    public void importRecord() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        importLauncher.launch(intent);
    }
    
    public void pickImage() {
        imagePickerLauncher.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }

    private void saveAndSetPortrait(Uri imageUri) {
        try {
            InputStream inputStream = this.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }

            File portraitDir = new File(this.getFilesDir(), "portraits");
            if (!portraitDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                portraitDir.mkdirs();
            }

            User loginUser = LoginActivity.getLoginUser();
            File portraitFile = new File(portraitDir, "user_" + loginUser.getId() + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(portraitFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();

            String absPath = portraitFile.getAbsolutePath();
            loginUser.setPortraitPath(absPath);
            if (DatabaseHelper.getUserHelper().update(loginUser.getId(), loginUser) > 0) {
                this.profile.setImageBitmap(bitmap);
                Toast.makeText(this, "头像设置成功", Toast.LENGTH_SHORT).show();
                MainActivity.getSharedPreferences().edit().putString("user_portrait", absPath).apply();
            } else {
                Toast.makeText(this, "头像设置失败", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("头像设置", "头像设置失败", e);
            Toast.makeText(this, "头像设置失败", Toast.LENGTH_SHORT).show();
        }
    }
}