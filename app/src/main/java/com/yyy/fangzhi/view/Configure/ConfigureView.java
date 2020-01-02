package com.yyy.fangzhi.view.Configure;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.yyy.fangzhi.R;
import com.yyy.fangzhi.util.PxUtil;
import com.yyy.fangzhi.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ConfigureView extends FrameLayout {
    private final static String TAG = "ListConfigureView";
    Context context;
    TextView tvStyleTitle;
    String styleTitle;
    int styleTitleColor;
    int styleTitleBackground;
    int screenWidth;
    int height = 0;
    int width = 0;
    int w, h;
    float textSize;
    int top;
    int paddingleft;
    int paddingRight;
    int paddingMiddle;
    int paddingTop;
    List<ConfigureInfo> infoList = new ArrayList<>();
    LinearLayout.LayoutParams params;

    public TextView getTvStyleTitle() {
        return tvStyleTitle;
    }

    public ConfigureView(@NonNull Context context) {
        this(context, null);
    }

    public ConfigureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paddingleft = context.getResources().getDimensionPixelOffset(R.dimen.dp_20);
        paddingRight = context.getResources().getDimensionPixelOffset(R.dimen.dp_20);
        setPadding(0, 0, 0, context.getResources().getDimensionPixelOffset(R.dimen.dp_10));
        measureView();
        setBackgroundColor(context.getResources().getColor(R.color.white));
        screenWidth = PxUtil.getWidth(context);

        paddingMiddle = context.getResources().getDimensionPixelOffset(R.dimen.dp_10);
        paddingTop = context.getResources().getDimensionPixelOffset(R.dimen.dp_10);
        setTitleText();
//        setView();
    }

    private void measureView() {
        w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    }

    private void setTitleText() {
        tvStyleTitle = new TextView(context);
//        tvStyleTitle.setText(R.string.review_style_title);
        tvStyleTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp_14));
        tvStyleTitle.setBackgroundColor(context.getResources().getColor(R.color.default_bg_color));
        tvStyleTitle.setTextColor(context.getResources().getColor(R.color.default_text_color));
        tvStyleTitle.setPadding(paddingleft, (int) context.getResources().getDimension(R.dimen.dp_4), paddingRight, (int) context.getResources().getDimension(R.dimen.dp_2));
        tvStyleTitle.setSingleLine();
        addView(tvStyleTitle);
    }

    /**
     * 加载标题和内容
     */
    private void setView() {
        int row = -1;
//        int itemWidth = (int) (screenWidth - tvStyleTitle.getPaddingLeft() * 2 - getPaddingLeft() * 2);
        int itemWidth = (int) (screenWidth);
        for (int i = 0; i < infoList.size(); i++) {

            ConfigureInfo item = infoList.get(i);

            if (row == -1)
                row = item.getRow();
            ConfigureInfoView rivItem = setItem(item);
//            LinearLayout.LayoutParams params;
            LayoutParams params;
//            params.width = context.getResources().getDimensionPixelOffset(R.dimen.dp_40);
//            rivItem.getTvTitle().setLayoutParams(params);
            if (i + 1 < infoList.size() && row != infoList.get(i + 1).getRow())
                params = new LayoutParams((int) (itemWidth - width), ViewGroup.LayoutParams.WRAP_CONTENT);
//                params = new LayoutParams((int) (itemWidth - width), ViewGroup.LayoutParams.WRAP_CONTENT);
            else
//                params = new LinearLayout.LayoutParams((int) (itemWidth * (item.getWidthPercent())), ViewGroup.LayoutParams.WRAP_CONTENT);
                params = new LayoutParams((int) (itemWidth * (item.getWidthPercent())), ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(width, height, 0, 0);
            rivItem.setLayoutParams(params);

            rivItem = setPaddings(rivItem, row, i);
            addView(rivItem);
            rivItem.measure(w, h);
            width = (int) (width + itemWidth * (item.getWidthPercent()));
            if (i + 1 == infoList.size() || (i + 1 < infoList.size() && infoList.get(i + 1).getRow() != row)) {
                height = height + rivItem.getMeasuredHeight();
                width = 0;
                if (i + 1 < infoList.size())
                    row = infoList.get(i + 1).getRow();
            }
        }
    }

    /**
     * 设置间距
     *
     * @param rivItem
     * @param row
     * @param i
     * @return
     */
    private ConfigureInfoView setPaddings(ConfigureInfoView rivItem, int row, int i) {
        int count = 1;
        if (count == 1 && (i + 1 < infoList.size() && row == infoList.get(i + 1).getRow())) {
            rivItem.setPadding(paddingleft, paddingTop, paddingMiddle, 0);
            count++;
        } else if (count == 1 && (i + 1 < infoList.size() && row != infoList.get(i + 1).getRow())) {
            rivItem.setPadding(paddingleft, paddingTop, paddingRight, 0);
            count = 1;
        } else if (count == 1 && i + 1 == infoList.size()) {
            rivItem.setPadding(paddingleft, paddingTop, paddingRight, 0);
//            Log.e(TAG, i + "c");
        } else if ((i + 1 < infoList.size() && row != infoList.get(i + 1).getRow())) {
            rivItem.setPadding(paddingMiddle, paddingTop, paddingRight, 0);
            count = 1;
        } else {
            rivItem.setPadding(paddingMiddle, paddingTop, paddingMiddle, 0);
            count++;
        }
        return rivItem;
    }

    /**
     * 设置item内容
     *
     * @param item
     * @return
     */
    private ConfigureInfoView setItem(ConfigureInfo item) {
        ConfigureInfoView rivItem = new ConfigureInfoView(context);
        rivItem.setBackgroundColor(context.getResources().getColor(R.color.white));
        rivItem.setTitle(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle() + "：");

        if (item.getTitleColor() != 0)
            rivItem.setTitleColor(item.getTitleColor());
        if (item.isTitleBold() == true)
            rivItem.setTitleBold(true);
        rivItem.setContent(item.getContent());
        if (item.getContentColor() != 0)
            rivItem.setContentColor(item.getContentColor());
        if (item.isContentBold() == true)
            rivItem.setContentBold(true);

        if (item.getContentSize() != 0) {
            rivItem.setContentSize(item.getContentSize());
        }
//        else {
//            rivItem.setContentSize(14);
//        }
        if (item.getTitleSize() != 0) {
            rivItem.setTitleSize(item.getTitleSize());
        }
//        else {
//            rivItem.setTitleSize(14);
//        }
        if (item.isProgress() == true && StringUtil.isNotEmpty(item.getContent()) && StringUtil.isInteger(item.getContent())) {
            rivItem.setProgress(true, Integer.parseInt(item.getContent()));
        }
        return rivItem;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        invalidate();
    }


    /**
     * 传入样式List
     *
     * @param infoList
     */
    public void setInfoList(List<ConfigureInfo> infoList) {
        this.infoList = infoList;
        setView();
    }

    public void setTitleVisiable(boolean isShow) {
        if (!isShow) {
            tvStyleTitle.setVisibility(GONE);
        }
    }

}
