package org.mf.bookkeeping.fragments;

import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import org.mf.bookkeeping.AddItemActivity;
import org.mf.bookkeeping.R;
import org.mf.bookkeeping.util.Category;

public class ExpenditureFragment extends AddItemFragment {

    public ExpenditureFragment(AddItemActivity master) {
        super(master);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_out;
    }

    @Override
    public void onViewCreated() {
        this.normalColor = ContextCompat.getColor(super.master, R.color.black);
        this.chosenColor = ContextCompat.getColor(super.master, R.color.chosen);

        this.categoryLayouts = new LinearLayout[]{
                findViewById(R.id.meal),
                findViewById(R.id.snack),
                findViewById(R.id.clothing),
                findViewById(R.id.daily_necessaries),
                findViewById(R.id.transportation),
                findViewById(R.id.waters),
                findViewById(R.id.internet),
                findViewById(R.id.entertainment),
                findViewById(R.id.medical_treatment),
                findViewById(R.id.other_out)
        };

        this.chosen = categoryLayouts[0];
        this.chosenIndex = 0;

        for (int i = 0; i < categoryLayouts.length; i++) {
            LinearLayout layout = categoryLayouts[i];
            final int finalI = i;
            layout.setOnClickListener(v -> super.choseCategory(layout, finalI));
        }

        if (super.onViewCreate != null) {
            super.onViewCreate.run();
        }
    }

    @Override
    public Category getCategory() {
        switch (this.chosenIndex) {
            case 0: return new Category();
            case 1: return new Category(Category.SNACK);
            case 2: return new Category(Category.CLOTHING);
            case 3: return new Category(Category.DAILY_NECESSARIES);
            case 4: return new Category(Category.TRANSPORTATION);
            case 5: return new Category(Category.WATER_ELE_GAS);
            case 6: return new Category(Category.INTERNET);
            case 7: return new Category(Category.ENTERTAINMENT);
            case 8: return new Category(Category.MEDICAL_TREATMENT);
            case 9: return new Category(Category.OTHER_OUT);
        }
        throw new ArrayIndexOutOfBoundsException("不该出现的索引值: " + this.chosenIndex);
    }
}
