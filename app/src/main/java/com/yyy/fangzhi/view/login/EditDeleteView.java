package com.yyy.fangzhi.view.login;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditDeleteView extends FrameLayout {
    Context context;
    EditText etText;
    ImageView IvDelete;

    public EditDeleteView(@NonNull Context context) {
        this(context, null);
    }

    public EditDeleteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        initText();

    }

    private void initText() {
        etText = new EditText(context);

    }


}
