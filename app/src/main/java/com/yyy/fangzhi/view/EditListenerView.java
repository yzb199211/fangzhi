package com.yyy.fangzhi.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;


import com.yyy.fangzhi.interfaces.OnEntryListener;

@SuppressLint("AppCompatCustomView")
public class EditListenerView extends EditText {
    Context context;
    OnEntryListener onEntryListener;

    public void setOnEntryListener(OnEntryListener onEntryListener) {
        this.onEntryListener = onEntryListener;
    }

    public EditListenerView(Context context) {
        this(context, null);
    }

    public EditListenerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    if (onEntryListener != null) {
                        onEntryListener.onEntry(v);
                    }
                }
                return false;
            }
        });
    }
}
