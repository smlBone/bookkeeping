package org.mf.bookkeeping.adapters;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import org.mf.bookkeeping.AddItemActivity;
import org.mf.bookkeeping.MainActivity;
import org.mf.bookkeeping.models.BillRecord;
import org.mf.bookkeeping.util.AppDate;
import org.mf.bookkeeping.R;
import org.mf.bookkeeping.models.DayEntry;
import org.mf.bookkeeping.util.Number;

import java.util.ArrayList;
import java.util.List;

public class MainListAdapter extends BaseAdapter {
    private final List<DayEntry> entries;
    private final LayoutInflater inflater;

    private final MainActivity context;

    private DayEntry.DialogListener copy;
    private DayEntry.DialogListener modify;
    private DayEntry.DialogListener delete;

    public MainListAdapter(MainActivity context) {
        this.entries = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public DayEntry getByDay(AppDate date) {
        if (this.entries.isEmpty()) {
            return null;
        }
        for (DayEntry entry : this.entries) {
            if (entry.getDate().isSameDay(date)) {
                return entry;
            }
        }
        return null;
    }

    public DayEntry get(int index) {
        return this.entries.get(index);
    }

    public void addEntry(DayEntry entry) {
        int insertIndex = 0;
        for (int i = 0; i < this.entries.size(); i++) {
            if (entry.getDate().compareTo(this.entries.get(i).getDate()) > 0) {
                insertIndex = i;
                break;
            }
            insertIndex = i + 1;
        }
        this.entries.add(insertIndex, entry);
        notifyDataSetChanged();
    }

    public List<DayEntry> getCopy() {
        return new ArrayList<>(this.entries);
    }

    public void clear() {
        this.entries.clear();
    }

    public boolean removeRecord(BillRecord record) {
        DayEntry entry = this.getByDay(record.getDate());
        if (entry != null && entry.remove(record)) {
            if (entry.getCount() == 0) {
                this.entries.remove(entry);
            }
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    public int getCount() {
        return this.entries.size();
    }

    @Override
    public Object getItem(int position) {
        return this.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DayEntry entry = this.get(position);

        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.main_list_item, parent, false);
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(this.context, AddItemActivity.class);
                intent.putExtra("date", entry.getDate());
                this.context.launch(intent);
            });
        }

        TextView date = convertView.findViewById(R.id.date);
        TextView expenditure = convertView.findViewById(R.id.expenditure);
        LinearLayout internalList = convertView.findViewById(R.id.internal_list);

        internalList.removeAllViews();
        for (int i = entry.getCount() - 1; i >= 0; i--) {
            View dialogView = this.inflater.inflate(R.layout.dialog_record, null);
            internalList.addView(entry.getView(i, internalList, dialogView,
                    new AlertDialog.Builder(this.context)
                    .setView(dialogView)
                    .create(),
                    this.copy, this.modify, this.delete
            ));
        }

        date.setText(entry.getDateString());
        Number total = entry.getTotalExpenditure();
        if (total.compareTo(Number.ZERO) >= 0) {
            String text = "总计: +" + total;
            expenditure.setText(text);
            expenditure.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(convertView.getContext(), R.color.in)));
        } else {
            String text = "总计: " + total;
            expenditure.setText(text);
//            expenditure.setTextColor(convertView.getResources().getColor(R.color.out));
            expenditure.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(convertView.getContext(), R.color.out)));
        }

        return convertView;
    }

    public Number getMonthTotal() {
        Number retVal = new Number();
        for (DayEntry entry : this.entries) {
            retVal.plus(entry.getTotalExpenditure());
        }
        return retVal;
    }

    public Number getMonthOut() {
        Number retVal = new Number();
        for (DayEntry entry : this.entries) {
            retVal.plus(entry.getDayOut());
        }
        return retVal;
    }

    public Number getMonthIn() {
        Number retVal = new Number();
        for (DayEntry entry : this.entries) {
            retVal.plus(entry.getDayIn());
        }
        return retVal;
    }

    public void setCopy(DayEntry.DialogListener copy) {
        this.copy = copy;
    }

    public void setModify(DayEntry.DialogListener modify) {
        this.modify = modify;
    }

    public void setDelete(DayEntry.DialogListener delete) {
        this.delete = delete;
    }
}
