package org.mf.bookkeeping.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.mf.bookkeeping.AddItemActivity;
import org.mf.bookkeeping.MainActivity;
import org.mf.bookkeeping.database.DatabaseHelper;
import org.mf.bookkeeping.util.AppDate;
import org.mf.bookkeeping.util.Category;
import org.mf.bookkeeping.R;
import org.mf.bookkeeping.adapters.MainListAdapter;
import org.mf.bookkeeping.models.DayEntry;
import org.mf.bookkeeping.models.BillRecord;
import org.mf.bookkeeping.util.Number;
import org.mf.bookkeeping.widgets.DualColorProgressBar;

import java.util.List;

public class HomeFragment extends SuperFragment<MainActivity> {

    private MainListAdapter mainAdapter;
    private List<DayEntry> buffer;
    private final View.OnClickListener clickListener;
    private final View.OnLongClickListener longClickListener;

    private int year;
    private int month;

    private int budget;
    private float monthExpenditure;
    private DualColorProgressBar progressBar;
    private TextView budgetView;

    public HomeFragment(MainActivity master, 
                        View.OnClickListener clickListener,
                        View.OnLongClickListener longClickListener) {
        super(master);
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        AppDate now = AppDate.now();
        this.year = now.getYear();
        this.month = now.getMonth();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void onViewCreated() {
        this.progressBar = findViewById(R.id.budget_progress_bar);
        this.budgetView = findViewById(R.id.budget);

        SharedPreferences sp = MainActivity.getSharedPreferences();
        this.budget = sp.getInt(this.monthToString(), 0);
        if (this.budget != 0) {
            this.progressBar.setRatio(this.monthExpenditure / this.budget);
            this.progressBar.setVisibility(View.VISIBLE);
            String budgetViewTxt = "本月预算: " + budget;
            this.budgetView.setText(budgetViewTxt);
        }

        TextView datePicker = findViewById(R.id.home_date_picker);
        datePicker.setText(this.monthToString());
        datePicker.setOnClickListener(v -> {
            DatePickerDialog pickerDialog = new DatePickerDialog(
                    super.master,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        int realMonth = monthOfYear + 1;
                        this.year = year;
                        this.month = realMonth;
                        String txt = year + "-" + realMonth;
                        datePicker.setText(txt);
                        this.setAdapter();
                    },
                    this.year,
                    this.month,
                    1
            );
            pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            pickerDialog.show();
        });
        this.setAdapter();

        findViewById(R.id.button_add).setOnClickListener(this.clickListener);
        findViewById(R.id.button_add).setOnLongClickListener(this.longClickListener);
        this.budgetView.setOnClickListener(v -> {
            EditText inputEditText = new EditText(super.master);
            inputEditText.setHint("请输入预算");
            inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(super.master)
                    .setTitle("预算设置")
                    .setView(inputEditText)
                    .setPositiveButton("确定", ((dialog, which) -> {
                        String input = inputEditText.getText().toString().trim();
                        if (input.equals("0")) {
                            this.budgetView.setText("设置本月预算");
                            this.progressBar.setVisibility(View.INVISIBLE);
                            return;
                        }
                        if (!input.isEmpty()) {
                            this.budget = Integer.parseInt(input);
                            this.progressBar.setRatio(this.monthExpenditure / this.budget);
                            this.progressBar.setVisibility(View.VISIBLE);
                            String budgetViewTxt = "本月预算: " + input;
                            this.budgetView.setText(budgetViewTxt);
                            sp.edit().putInt(this.monthToString(), this.budget).apply();
                        }
                    }))
                    .setNegativeButton("取消", null)
                    .show();
        });
        findViewById(R.id.more_search).setOnClickListener(v -> {
            EditText inputEditText = new EditText(super.master);
            inputEditText.setHint("请输入关键词");
            new AlertDialog.Builder(super.master)
                    .setTitle("备注搜索")
                    .setView(inputEditText)
                    .setPositiveButton("确定", ((dialog, which) -> {
                        String input = inputEditText.getText().toString().trim();
                        if (this.buffer == null) {
                            this.buffer = this.mainAdapter.getCopy();
                        }
                        if (input.isEmpty()) {
                            this.mainAdapter.clear();
                            for (DayEntry entry : this.buffer) {
                                this.mainAdapter.addEntry(entry);
                            }
                            return;
                        }
                        this.mainAdapter.clear();
                        DayEntry entry = new DayEntry(AppDate.now(), super.master);
                        entry.addAll(DatabaseHelper.getRecordHelper().getByNote(input));
                        this.mainAdapter.addEntry(entry);
                    }))
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    public void loadData() {
        List<BillRecord> records = DatabaseHelper.getRecordHelper().getByMonth(this.year, this.month);
        if (records.isEmpty()) {
            return;
        }
        for (BillRecord record : records) {
            DayEntry entry = this.mainAdapter.getByDay(record.getDate());
            if (entry == null) {
                entry = new DayEntry(record.getDate(), super.master);
                this.mainAdapter.addEntry(entry);
            }
            entry.add(record);
        }
        this.updateImageUI();
    }

    private void saveData(BillRecord record) {
        long id = DatabaseHelper.getRecordHelper().insert(record);
        if (id == -1) {
            System.out.println("未将记录保存到数据库");
        } else {
            System.out.println("新加入的记录ID: " + id);
            record.setId((int) id);
        }
        this.updateImageUI();
    }

    public void addNewRecord(AppDate appDate, Category category, String note, Number cost) {
        DayEntry thatDayEntry = this.mainAdapter.getByDay(appDate);
        BillRecord newRecord = new BillRecord(appDate, category, note, cost);
        if (appDate.getMonth() == this.month) {
            if (thatDayEntry == null) {
                thatDayEntry = new DayEntry(appDate, super.master);
                this.mainAdapter.addEntry(thatDayEntry);
            }
            thatDayEntry.add(newRecord);
            this.mainAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(super.master, "已保存到" + appDate.getYear() + "-" + appDate.getMonth(), Toast.LENGTH_SHORT).show();
        }
        this.buffer = null;
        this.saveData(newRecord);
    }

    private void updateImageUI() {
        TextView month_out = findViewById(R.id.month_out);
        Number out = this.mainAdapter.getMonthOut().reverse();
        this.monthExpenditure = out.toFloat();
        String txtOut = "月支出: " + out;
        month_out.setText(txtOut);
        TextView month_in = findViewById(R.id.month_in);
        String txtIn = "月收入: " + this.mainAdapter.getMonthIn().toString();
        month_in.setText(txtIn);
        TextView month_balance = findViewById(R.id.month_balance);
        String txtBalance = "本月结余: " + this.mainAdapter.getMonthTotal().toString();
        month_balance.setText(txtBalance);
        if (this.progressBar != null) {
            this.progressBar.setRatio(this.monthExpenditure / this.budget);
        }
    }

    public void modifyRecord(int id, AppDate date, Category cate, String note, Number cost) {
        DatabaseHelper.getRecordHelper().update(id,
                date.getYear(), date.getMonth(), date.getDayOfMonth(),
                cate.type, note, cost.toString());
        DayEntry entry = this.mainAdapter.getByDay(date);
        if (entry != null) {
            BillRecord record = entry.get(id);
            record.setDate(date);
            record.setCategory(cate);
            record.setNote(note);
            record.setAmount(cost);
        }
        this.updateImageUI();
        this.buffer = null;
        this.mainAdapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        ListView mainList = findViewById(R.id.main_list);
        this.mainAdapter = new MainListAdapter(super.master);
        this.mainAdapter.setCopy(record ->
                this.addNewRecord(record.getDate(), record.getCategory(), record.getNote(), record.getAmount())
        );
        this.mainAdapter.setModify(record -> {
            Intent intent = new Intent(super.master, AddItemActivity.class);
            intent.putExtra("modifyID", record.getId());
            super.master.launch(intent);
        });
        this.mainAdapter.setDelete(record -> {
            if (DatabaseHelper.getRecordHelper().delete(record.getId()) > 0) {
                if (this.mainAdapter.removeRecord(record)) {
                    Toast.makeText(super.master, "删除成功", Toast.LENGTH_SHORT).show();
                    this.updateImageUI();
                } else {
                    Toast.makeText(super.master, "删除成功，请刷新界面", Toast.LENGTH_SHORT).show();
                    Log.w("删除记录", "UI层删除失败");
                }
            } else {
                Toast.makeText(super.master, "删除失败", Toast.LENGTH_SHORT).show();
                Log.w("删除记录", "数据库层删除失败");
            }
        });
        this.loadData();
        mainList.setAdapter(this.mainAdapter);
        this.mainAdapter.notifyDataSetChanged();
    }

    private String monthToString() {
        return this.year + "-" + this.month;
    }
}