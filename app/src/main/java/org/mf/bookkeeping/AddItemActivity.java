package org.mf.bookkeeping;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import org.mf.bookkeeping.database.DatabaseHelper;
import org.mf.bookkeeping.fragments.ExpenditureFragment;
import org.mf.bookkeeping.fragments.IncomeFragment;
import org.mf.bookkeeping.fragments.AddItemFragment;
import org.mf.bookkeeping.models.BillRecord;
import org.mf.bookkeeping.util.AmountBuilder;
import org.mf.bookkeeping.util.AppDate;
import org.mf.bookkeeping.util.Category;
import org.mf.bookkeeping.util.Number;

public class AddItemActivity extends AppCompatActivity {

    public static final int ADD_ITEM_CODE = 1;
    private static final String TAG = "keyBoard";
    private final ExpenditureFragment out;
    private final IncomeFragment in;
    private AddItemFragment currentFragment;
    private TextView datePicker;
    private EditText noteView;
    private int year;
    private int month;
    private int dayOfMonth;

    private TextView tvOut;
    private TextView tvIn;

    private TextView amountView;
    private LinearLayout inputArea;
    private View numberKeyboard;
    private Button saveButton;

    private final AmountBuilder amountBuilder = new AmountBuilder();

    private boolean isKeyboardVisible = false;
    private BillRecord modifyRecord;

    public AddItemActivity() {
        this.out = new ExpenditureFragment(this);
        this.in = new IncomeFragment(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_item);

        this.tvIn = findViewById(R.id.fragment_in);
        this.tvOut = findViewById(R.id.fragment_out);
        this.amountView = findViewById(R.id.amount);
        this.inputArea = findViewById(R.id.input_area);
        this.numberKeyboard = findViewById(R.id.number_keyboard);
        this.datePicker = findViewById(R.id.pick_date);
        this.saveButton = findViewById(R.id.btn_save);
        this.noteView = findViewById(R.id.edit_for_note);

        this.initialize();
        this.keyBoardListeners();
        this.setupKeyboardDetection();

        this.tvIn.setOnClickListener(v -> this.changeFragment(this.in));
        this.tvOut.setOnClickListener(v -> this.changeFragment(this.out));

        super.findViewById(R.id.back_btn).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void initialize() {
        Intent intent = getIntent();
        this.modifyRecord = DatabaseHelper.getRecordHelper().getByID(intent.getIntExtra("modifyID", -1));
        if (this.modifyRecord != null) {
            AppDate date = this.modifyRecord.getDate();
            if (date != null) {
                this.year = date.getYear();
                this.month = date.getMonth();
                this.dayOfMonth = date.getDayOfMonth();
                String text;
                if (date.isSameDay(AppDate.now())) {
                    text = "今天";
                } else if (date.isSameDay(AppDate.yesterday())) {
                    text = "昨天";
                } else {
                    text = this.year + "-" + this.month + "-" + this.dayOfMonth;
                }
                this.datePicker.setText(text);
            }
            Category cate = this.modifyRecord.getCategory();
            if (cate != null) {
                this.changeFragment(cate.income?this.in:this.out);
                this.currentFragment.setOnViewCreate(() -> this.currentFragment.updateUI(cate));
                Number amount = this.modifyRecord.getAmount();
                if (amount != null) {
                    this.amountBuilder.setBase(cate.income?amount:amount.reverse());
                    this.freshText();
                }
            }
            this.noteView.setText(this.modifyRecord.getNote());
        } else {
            AppDate date = (AppDate) intent.getSerializableExtra("date");
            if (date != null) {
                this.year = date.getYear();
                this.month = date.getMonth();
                this.dayOfMonth = date.getDayOfMonth();

                String text;
                if (date.isSameDay(AppDate.now())) {
                    text = "今天";
                } else if (date.isSameDay(AppDate.yesterday())) {
                    text = "昨天";
                } else {
                    text = this.year + "-" + this.month + "-" + this.dayOfMonth;
                }
                this.datePicker.setText(text);
            }
            this.changeFragment(intent.getBooleanExtra("expenditure", true)?this.out:this.in);
        }
    }

    private void setupKeyboardDetection() {
        final View rootView = findViewById(android.R.id.content);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);

            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            boolean keyboardNowVisible = keypadHeight > screenHeight * 0.15;

            if (keyboardNowVisible && !this.isKeyboardVisible) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.inputArea.getLayoutParams();
                this.isKeyboardVisible = true;
                this.numberKeyboard.setVisibility(View.GONE);

                params.removeRule(RelativeLayout.ABOVE);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.bottomMargin = keypadHeight;
                this.inputArea.setLayoutParams(params);
            } else if (!keyboardNowVisible && this.isKeyboardVisible) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.inputArea.getLayoutParams();
                this.isKeyboardVisible = false;
                this.numberKeyboard.setVisibility(View.VISIBLE);

                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.ABOVE, R.id.number_keyboard);
                params.bottomMargin = 0;
                this.inputArea.setLayoutParams(params);
            }
        });
    }

    private void changeFragment(AddItemFragment target) {
        if (this.currentFragment != target) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.f_container, target);
            transaction.commit();
            this.currentFragment = target;
            if (target == this.out) {
                this.tvOut.setBackground(ContextCompat.getDrawable(this, R.drawable.out_bg));
                this.tvIn.setBackground(null);
                int color = ContextCompat.getColor(this, R.color.out);
                this.saveButton.setBackgroundTintList(ColorStateList.valueOf(color));
                this.amountView.setTextColor(color);
            } else {
                this.tvOut.setBackground(null);
                this.tvIn.setBackground(ContextCompat.getDrawable(this, R.drawable.in_bg));
                int color = ContextCompat.getColor(this, R.color.in);
                this.saveButton.setBackgroundTintList(ColorStateList.valueOf(color));
                this.amountView.setTextColor(color);
            }
        }
    }

    private void keyBoardListeners() {
        super.findViewById(R.id.btn_1).setOnClickListener(v -> this.addDigit(1));
        super.findViewById(R.id.btn_2).setOnClickListener(v -> this.addDigit(2));
        super.findViewById(R.id.btn_3).setOnClickListener(v -> this.addDigit(3));
        super.findViewById(R.id.btn_4).setOnClickListener(v -> this.addDigit(4));
        super.findViewById(R.id.btn_5).setOnClickListener(v -> this.addDigit(5));
        super.findViewById(R.id.btn_6).setOnClickListener(v -> this.addDigit(6));
        super.findViewById(R.id.btn_7).setOnClickListener(v -> this.addDigit(7));
        super.findViewById(R.id.btn_8).setOnClickListener(v -> this.addDigit(8));
        super.findViewById(R.id.btn_9).setOnClickListener(v -> this.addDigit(9));
        super.findViewById(R.id.btn_0).setOnClickListener(v -> this.addDigit(0));
        super.findViewById(R.id.btn_clear).setOnClickListener(v -> {
            this.amountBuilder.clear();
            this.freshText();
            Log.i(TAG, "清空数据");
        });
        super.findViewById(R.id.btn_backward).setOnClickListener(v -> {
            if (this.amountBuilder.backward()) {
                Log.i(TAG, "退格");
                this.freshText();
            } else {
                Log.w(TAG, "退格失败");
            }
        });
        super.findViewById(R.id.btn_plus).setOnClickListener(v -> {
            if (this.amountBuilder.addSymbol(true)) {
                Log.i(TAG, "添加 +");
                this.freshText();
            } else {
                Log.w(TAG, "添加 '+' 失败");
            }
        });
        super.findViewById(R.id.btn_minus).setOnClickListener(v -> {
            if (this.amountBuilder.addSymbol(false)) {
                Log.i(TAG, "添加 -");
                this.freshText();
            } else {
                Log.w(TAG, "添加 '-' 失败");
            }
        });
        super.findViewById(R.id.btn_dot).setOnClickListener(v -> {
            if (this.amountBuilder.addDot()) {
                Log.i(TAG, "添加 '.'");
                this.freshText();
            } else {
                Log.w(TAG, "添加 '.' 失败");
            }
        });
        this.saveButton.setOnClickListener(v -> {
            Number sum = this.amountBuilder.getSum();
            if (sum.compareTo(Number.ZERO) <= 0) {
                Toast.makeText(this, "保存的金额必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }
            Category category = this.currentFragment.getCategory();
            if (!category.income) {
                sum.reverse();
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("code", ADD_ITEM_CODE);
            resultIntent.putExtra("cate", category);
            resultIntent.putExtra("date", new AppDate(this.year, this.month, this.dayOfMonth));
            resultIntent.putExtra("note", this.noteView.getText().toString());
            resultIntent.putExtra("cost", sum);
            if (this.modifyRecord != null) {
                resultIntent.putExtra("modifyID", modifyRecord.getId());
            }
            setResult(RESULT_OK, resultIntent);
            finish();
            Log.i(TAG, "保存");
        });

        if (this.year == 0) {
            AppDate now = AppDate.now();
            this.year = now.getYear();
            this.month = now.getMonth();
            this.dayOfMonth = now.getDayOfMonth();
        }
        DatePickerDialog pickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    int realMonth = monthOfYear + 1;
                    Log.i(TAG, "选择日期: " + year + "-" + realMonth + "-" + dayOfMonth);
                    this.year = year;
                    this.month = realMonth;
                    this.dayOfMonth = dayOfMonth;

                    String text;
                    AppDate selected = new AppDate(year, realMonth, dayOfMonth);
                    if (selected.isSameDay(AppDate.now())) {
                        text = "今天";
                    } else if (selected.isSameDay(AppDate.yesterday())) {
                        text = "昨天";
                    } else {
                        text = this.year + "-" + this.month + "-" + this.dayOfMonth;
                    }
                    this.datePicker.setText(text);
                },
                this.year,
                this.month,
                this.dayOfMonth
        );
        pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        this.datePicker.setOnClickListener(v -> pickerDialog.show());
    }

    private void addDigit(int num) {
        if (this.amountBuilder.addDigit(num)) {
            Log.i(TAG, "添加: " + num);
            this.freshText();
        } else {
            Log.w(TAG, "填入数字失败: " + num);
        }
    }

    private void freshText() {
        this.amountView.setText(this.amountBuilder.makeString());
    }
}