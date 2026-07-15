package org.mf.bookkeeping.fragments;

import androidx.core.content.ContextCompat;

import android.widget.LinearLayout;

import org.mf.bookkeeping.AddItemActivity;
import org.mf.bookkeeping.R;
import org.mf.bookkeeping.util.Category;

public class IncomeFragment extends AddItemFragment {

    public IncomeFragment(AddItemActivity master) {
        super(master);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_in;
    }

    @Override
    public void onViewCreated() {
        this.normalColor = ContextCompat.getColor(super.master, R.color.black);
        this.chosenColor = ContextCompat.getColor(super.master, R.color.chosen);

        this.categoryLayouts = new LinearLayout[]{
                findViewById(R.id.salary),
                findViewById(R.id.living_expenses),
                findViewById(R.id.red_envelopes),
                findViewById(R.id.perk),
                findViewById(R.id.other_in)
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
            case 0: return new Category(Category.SALARY);
            case 1: return new Category(Category.LIVING_EXPENSES);
            case 2: return new Category(Category.RED_ENVELOPES);
            case 3: return new Category(Category.PERK);
            case 4: return new Category(Category.OTHER_IN);
        }
        throw new ArrayIndexOutOfBoundsException("不该出现的索引值: " + this.chosenIndex);
    }
}