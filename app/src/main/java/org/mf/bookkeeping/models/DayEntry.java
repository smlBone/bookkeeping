package org.mf.bookkeeping.models;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import org.mf.bookkeeping.R;
import org.mf.bookkeeping.util.AppDate;
import org.mf.bookkeeping.util.Number;

import java.util.ArrayList;
import java.util.List;

public class DayEntry {
    private final AppDate date;
    private final List<BillRecord> records;
    private final LayoutInflater inflater;

    public DayEntry(AppDate date, Context context) {
        this.date = date;
        this.records = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void add(BillRecord record) {
        this.records.add(record);
    }

    public void addAll(List<BillRecord> records) {
        this.records.addAll(records);
    }

    public boolean remove(BillRecord record) {
        return this.records.remove(record);
    }

    public BillRecord get(int id) {
        for (BillRecord record : records) {
            if (record.getId() == id) {
                return record;
            }
        }
        return null;
    }

    public String getDateString() {
        return this.date.getDateString();
    }

    public AppDate getDate() {
        return this.date;
    }

    public Number getTotalExpenditure() {
        Number retVal = new Number();
        for (BillRecord record : this.records) {
            retVal.plus(record.getAmount());
        }
        return retVal;
    }

    public Number getDayOut() {
        Number retVal = new Number();
        for (BillRecord record : this.records) {
            if (!record.isIncome()) {
                retVal.plus(record.getAmount());
            }
        }
        return retVal;
    }

    public Number getDayIn() {
        Number retVal = new Number();
        for (BillRecord record : this.records) {
            if (record.isIncome()) {
                retVal.plus(record.getAmount());
            }
        }
        return retVal;
    }

    public int getCount() {
        return this.records.size();
    }

    public View getView(int position, ViewGroup parent,
                        View dialogView, AlertDialog dialog,
                        DialogListener copy, DialogListener modify, DialogListener delete) {
        View convertView = this.inflater.inflate(R.layout.internal_list_item, parent, false);

        BillRecord billRecord = this.records.get(position);
        String cateTxt = billRecord.getCategory().toString();
        String noteTxt = billRecord.getSimpleNote();
        String costTxt = billRecord.getAmount().toString();

        convertView.setOnClickListener(view -> {
            TextView tvAmount = dialogView.findViewById(R.id.tv_amount);
            tvAmount.setText(costTxt);
            TextView tvCategory = dialogView.findViewById(R.id.tv_category);
            tvCategory.setText(cateTxt);
            TextView tvDate = dialogView.findViewById(R.id.tv_date);
            tvDate.setText(this.date.toString());
            TextView tvNote = dialogView.findViewById(R.id.tv_note);
            tvNote.setText(noteTxt.isEmpty()?"暂无备注":noteTxt);

            dialogView.findViewById(R.id.btn_copy).setOnClickListener(v -> {
                if (copy != null) {
                    copy.run(billRecord);
                }
                dialog.dismiss();
            });
            dialogView.findViewById(R.id.btn_modify).setOnClickListener(v -> {
                if (modify != null) {
                    modify.run(billRecord);
                }
                dialog.dismiss();
            });
            dialogView.findViewById(R.id.btn_delete).setOnClickListener(v -> {
                if (delete != null) {
                    delete.run(billRecord);
                }
                dialog.dismiss();
            });

            dialog.show();
        });

        TextView category = convertView.findViewById(R.id.category);
        TextView note = convertView.findViewById(R.id.note);
        TextView this_expenditure = convertView.findViewById(R.id.this_expenditure);

        category.setText(cateTxt);
        note.setText(noteTxt);
        this_expenditure.setText(costTxt);

        if (billRecord.isIncome()) {
            this_expenditure.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(parent.getContext(), R.color.in)));
        } else {
//            this_expenditure.setTextColor(convertView.getResources().getColor(R.color.out));
            this_expenditure.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(parent.getContext(), R.color.out)));
        }

        return convertView;
    }

    public interface DialogListener {
        void run(BillRecord record);
    }
}
