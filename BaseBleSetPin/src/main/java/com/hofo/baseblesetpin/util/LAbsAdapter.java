package com.hofo.baseblesetpin.util;


import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class LAbsAdapter<T> extends BaseAdapter {
    List<T> data;
    Context mContext;
    LayoutInflater inflater;


    public LAbsAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.data = new ArrayList<>();
    }

    public void addElement(T data) {
        this.data.add(data);
    }

    public List<T> getData() {
        return data;
    }

    public void addElementS(List<T> data) {
        this.data.addAll(data);
    }

    public void remove(T data) {
        this.data.remove(data);
    }
    public boolean contains(T data) {
        return this.data.contains(data);
    }


    public void removes(List<T> data) {
        this.data.removeAll(data);
    }

    public void removeAll() {
        this.data.clear();
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = vh.loadConvertView(parent, false);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        T t = getItem(position);
        bindData(position, t, vh);
        return convertView;
    }

    public abstract void bindData(int position, T bean, ViewHolder holder);

    public abstract int getLayoutId();

    public class ViewHolder {
        View convertView;
        HashMap<Integer, View> viewMap = new HashMap<>();

        public ViewHolder() {
        }


        public View loadConvertView(ViewGroup parent, boolean attachToRoot) {
            if (convertView == null) {
                convertView = getInflater().inflate(getLayoutId(), parent, attachToRoot);
            }
            return convertView;
        }

        public View getConvertView() {
            return convertView;
        }

        public void setText(int viewId, String text) {
            View view = getView(viewId);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                tv.setText(text);
            }
        }

        public <V extends View> V getView(int viewId) {
            View view = viewMap.get(viewId);
            if (view == null) {
                if (convertView instanceof ViewGroup) {
                    view = convertView.findViewById(viewId);
                } else {
                    view = convertView;
                }
                viewMap.put(viewId, view);
            }
            return (V) view;
        }

        public void setTextColor(int viewId, @ColorInt int color) {
            View view = getView(viewId);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                tv.setTextColor(color);
            }
        }

        public void setTag(int viewId, Object obj) {
            View view = getView(viewId);
            if (view != null) {
                view.setTag(obj);
            }
        }
    }
}
