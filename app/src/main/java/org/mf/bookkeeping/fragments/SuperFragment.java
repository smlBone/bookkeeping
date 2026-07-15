package org.mf.bookkeeping.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class SuperFragment<T extends AppCompatActivity> extends Fragment {
    protected final T master;
    private View view;

    public SuperFragment(T master) {
        this.master = master;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @LayoutRes
    protected abstract int getLayout();

    /** @noinspection NullableProblems*/
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.view = view;
        this.onViewCreated();
    }

    public abstract void onViewCreated();

    protected <V extends View> V findViewById(@IdRes int id) {
        return this.view.findViewById(id);
    }
}
