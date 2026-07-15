package org.mf.bookkeeping.fragments;

import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.mf.bookkeeping.AddItemActivity;
import org.mf.bookkeeping.util.Category;

public abstract class AddItemFragment extends SuperFragment<AddItemActivity> {
    protected LinearLayout[] categoryLayouts;
    protected LinearLayout chosen;
    protected int chosenIndex;
    protected int normalColor;
    protected int chosenColor;
    protected Runnable onViewCreate;

    public AddItemFragment(AddItemActivity master) {
        super(master);
    }

    public abstract Category getCategory();

    public void updateUI(Category category) {
        switch (category.type) {
            case Category.MEAL:
            case Category.SALARY: {
                this.chosenIndex = 0;
                break;
            }
            case Category.SNACK:
            case Category.LIVING_EXPENSES: {
                this.chosenIndex = 1;
                break;
            }
            case Category.CLOTHING:
            case Category.RED_ENVELOPES: {
                this.chosenIndex = 2;
                break;
            }
            case Category.TRANSPORTATION:
            case Category.OTHER_IN: {
                this.chosenIndex = 4;
                break;
            }
            case Category.DAILY_NECESSARIES:
            case Category.PERK: {
                this.chosenIndex = 3;
                break;
            }
            case Category.WATER_ELE_GAS: {
                this.chosenIndex = 5;
                break;
            }
            case Category.INTERNET: {
                this.chosenIndex = 6;
                break;
            }
            case Category.ENTERTAINMENT: {
                this.chosenIndex = 7;
                break;
            }
            case Category.MEDICAL_TREATMENT: {
                this.chosenIndex = 8;
                break;
            }
            case Category.OTHER_OUT: {
                this.chosenIndex = 9;
                break;
            }
        }
        this.choseCategory(this.categoryLayouts[this.chosenIndex], this.chosenIndex);
    }

    protected void choseCategory(LinearLayout layout, int index) {
        if (this.chosen != layout) {
            TextView newOne = (TextView) layout.getChildAt(1);
            TextView oldOne = (TextView) this.chosen.getChildAt(1);

            newOne.setTypeface(null, Typeface.BOLD);
            newOne.setTextColor(this.chosenColor);

            oldOne.setTypeface(null, Typeface.NORMAL);
            oldOne.setTextColor(this.normalColor);

            this.chosen = layout;
            this.chosenIndex = index;
        }
    }

    public void setOnViewCreate(Runnable onViewCreate) {
        this.onViewCreate = onViewCreate;
    }
}
