package org.mf.bookkeeping.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.mf.bookkeeping.MainActivity;
import org.mf.bookkeeping.R;
import org.mf.bookkeeping.database.DatabaseHelper;
import org.mf.bookkeeping.models.BillRecord;
import org.mf.bookkeeping.util.AppDate;
import org.mf.bookkeeping.util.Number;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AnalysisFragment extends SuperFragment<MainActivity> {
    private int year;
    private int month;

    private List<BillRecord> incomes;
    private List<BillRecord> expenses;

    private TextView navIn;
    private TextView navOut;

    private boolean showOut = true;

    public AnalysisFragment(MainActivity master) {
        super(master);
        AppDate now = AppDate.now();
        this.year = now.getYear();
        this.month = now.getMonth();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_analysis;
    }

    @Override
    public void onViewCreated() {
        this.navIn = findViewById(R.id.nav_in);
        this.navOut = findViewById(R.id.nav_out);

        this.navIn.setOnClickListener(v -> setShowOut(false));
        this.navOut.setOnClickListener(v -> setShowOut(true));
        if (this.showOut) {
            this.navOut.setBackground(ContextCompat.getDrawable(super.master, R.drawable.out_bg));
            this.navIn.setBackground(null);
        } else {
            this.navOut.setBackground(null);
            this.navIn.setBackground(ContextCompat.getDrawable(super.master, R.drawable.in_bg));
        }
        TextView datePicker = findViewById(R.id.analysis_date_picker);
        datePicker.setText(monthToString());
        datePicker.setOnClickListener(v -> {
            DatePickerDialog pickerDialog = new DatePickerDialog(
                    super.master,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        int realMonth = monthOfYear + 1;
                        this.year = year;
                        this.month = realMonth;
                        String txt = year + "-" + realMonth;
                        datePicker.setText(txt);
                        this.updateData(true);
                    },
                    this.year,
                    this.month,
                    1
            );
            pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            pickerDialog.show();
        });
        this.updateData(true);
    }

    private void updateData(boolean updateList) {
        if (updateList) {
            this.incomes = new ArrayList<>();
            this.expenses = new ArrayList<>();
            for (BillRecord record : DatabaseHelper.getRecordHelper().getByMonth(this.year, this.month)) {
                if (record.getCategory().income) {
                    this.incomes.add(record);
                } else {
                    this.expenses.add(record);
                }
            }
        }

        PieChart pieChart = findViewById(R.id.pie_chart);

        HashMap<String, Number> map = new HashMap<>();
        Number totalNum = new Number();

        for (BillRecord record : (this.showOut?this.expenses:this.incomes)) {
            String category = record.getCategory().toString();
            Number number = map.get(category);
            Number amount = record.getAmount();
            if (number == null) {
                map.put(category, amount);
            } else {
                number.plus(amount);
            }
            totalNum.plus(amount);
        }
        ArrayList<PieEntry> entries = new ArrayList<>();
        map.forEach((s, number) -> entries.add(new PieEntry(number.abs().toFloat(), s)));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return String.format(Locale.CHINA, "%.2f%%", (value / totalNum.abs().toFloat()) * 100);
            }
        });

        pieChart.setData(new PieData(dataSet));
        pieChart.setDescription(null);

        pieChart.setDrawCenterText(true);
        if (totalNum.isZero()) {
            pieChart.setCenterText("暂无数据");
        } else {
            pieChart.setCenterText(this.showOut ?"支出比例":"收入比例");
        }
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(16f);

        pieChart.invalidate();

        TextView tvIn = findViewById(R.id.analysis_income);
        TextView tvOut = findViewById(R.id.analysis_expenditure);
        TextView tvBalance = findViewById(R.id.analysis_balance);
        TextView tvAverage = findViewById(R.id.analysis_average);

        Number totalIncome = new Number();
        for (BillRecord record : this.incomes) {
            totalIncome.plus(record.getAmount());
        }
        HashMap<AppDate, Number> ab = new HashMap<>();
        int validDayCount = 0;
        Number totalExpense = new Number();
        for (BillRecord record : this.expenses) {
            totalExpense.plus(record.getAmount());
            Number tmp = ab.get(record.getDate());
            if (tmp == null) {
                ab.put(record.getDate(), record.getAmount());
                validDayCount ++;
            } else {
                tmp.plus(record.getAmount());
            }
        }

        tvIn.setText(totalIncome.toString());
        tvOut.setText(totalExpense.reverse().toString());
        tvBalance.setText(new Number(totalIncome).minus(totalExpense).toString());

        tvAverage.setText(String.format(Locale.CHINA, "%.2f", totalExpense.toFloat() / validDayCount));
    }

    public void setShowOut(boolean showOut) {
        this.showOut = showOut;
        if (showOut) {
            this.navOut.setBackground(ContextCompat.getDrawable(super.master, R.drawable.out_bg));
            this.navIn.setBackground(null);
        } else {
            this.navOut.setBackground(null);
            this.navIn.setBackground(ContextCompat.getDrawable(super.master, R.drawable.in_bg));
        }
        this.updateData(false);
    }

    private String monthToString() {
        return this.year + "-" + this.month;
    }
}
