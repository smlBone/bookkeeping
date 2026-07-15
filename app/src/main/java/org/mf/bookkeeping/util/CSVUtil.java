package org.mf.bookkeeping.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.mf.bookkeeping.database.DatabaseHelper;
import org.mf.bookkeeping.models.BillRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class CSVUtil {

    private static final String HEADER = "金额,分类,日期,备注";
    private static final String HEADER_WITH_BOM = "\ufeff金额,分类,日期,备注";
    private static final String TAG = "CSVUtil";

    private CSVUtil() {}

    public static boolean export(ContentResolver resolver, Uri uri) {
        List<BillRecord> all = DatabaseHelper.getRecordHelper().getAll();
        resolver.takePersistableUriPermission(uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try(OutputStream os = resolver.openOutputStream(uri)) {
            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                writer.write(HEADER_WITH_BOM);
                writer.newLine();

                for (BillRecord record : all) {
                    writer.write(record.toCSVRow());
                    writer.newLine();
                }

                writer.flush();
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, "export: 导出失败", e);
        }
        return false;
    }

    public static List<BillRecord> importFromCSV(ContentResolver resolver, Uri uri) {
        try(InputStream is = resolver.openInputStream(uri)) {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                List<BillRecord> records = new ArrayList<>();
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (firstLine) {
                        if (line.equals(HEADER) || line.equals(HEADER_WITH_BOM)) {
                            firstLine = false;
                            continue;
                        } else {
                            Log.e(TAG, "importFromCSV: 文件首行格式错误: " + line);
                            return null;
                        }
                    }
                    records.add(parseLine(line));
                }
                return records;
            }
        } catch (IOException e) {
            Log.e(TAG, "importFromCSV: 导入失败", e);
        }
        return null;
    }

    private static BillRecord parseLine(String line) {
        String[] parts = line.split(",");
        Number amount = new Number(parts[0]);
        Category category = new Category(parts[1]);
        String[] YMD = parts[2].split("-");
        AppDate date = new AppDate(Integer.parseInt(YMD[0]),Integer.parseInt(YMD[1]),Integer.parseInt(YMD[2]));
        String note = parts.length > 3?parts[3]:"";
        return new BillRecord(date, category, note, amount);
    }
}
