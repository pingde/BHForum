package com.baiheplayer.bbs.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;


import com.onegravity.colorpicker.ColorPickerListener;
import com.onegravity.colorpicker.SetColorPickerListenerEvent;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.RTToolbarListener;
import com.onegravity.rteditor.effects.Effects;
import com.onegravity.rteditor.fonts.RTTypeface;
import com.onegravity.rteditor.toolbar.RTToolbarImageButton;
import com.onegravity.rteditor.toolbar.spinner.ColorSpinnerItem;
import com.onegravity.rteditor.toolbar.spinner.FontSpinnerItem;
import com.onegravity.rteditor.toolbar.spinner.SpinnerItem;
import com.onegravity.rteditor.toolbar.spinner.SpinnerItemAdapter;
import com.onegravity.rteditor.utils.Helper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 富文本类的简约化，去除一些加载慢的选项
 * Created by Administrator on 2016/12/14.
 */

public class SplHorizontalRTToolbar extends LinearLayout implements RTToolbar, View.OnClickListener{
    /*
        * We need a unique id for the toolbar because the RTManager is capable of managing multiple toolbars
        */
    private static AtomicInteger sIdCounter = new AtomicInteger(0);
    private int mId;

    private RTToolbarListener mListener;

    private ViewGroup mToolbarContainer;

    /*
     * The buttons
     */
    private RTToolbarImageButton mBold;
    private RTToolbarImageButton mItalic;
    private RTToolbarImageButton mUnderline;
    private RTToolbarImageButton mStrikethrough;
    private RTToolbarImageButton mAlignLeft;
    private RTToolbarImageButton mAlignCenter;
    private RTToolbarImageButton mAlignRight;
    private RTToolbarImageButton mNumber;

    private int mCustomColorFont = Color.BLACK;
    private int mCustomColorBG = Color.BLACK;

    private int mPickerId = -1;
    private ColorPickerListener mColorPickerListener;

    // ****************************************** Initialize Methods *******************************************

    public SplHorizontalRTToolbar(Context context) {
        super(context);
        init();
    }

    public SplHorizontalRTToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplHorizontalRTToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        synchronized (sIdCounter) {
            mId = sIdCounter.getAndIncrement();
        }
        SetColorPickerListenerEvent.setListener(mPickerId, mColorPickerListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // configure regular action buttons
        mBold = initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_bold);
        mItalic = initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_italic);
        mUnderline = initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_underline);
        mStrikethrough = initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_strikethrough);
        mAlignLeft = initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_align_left);
        mAlignCenter = initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_align_center);
        mAlignRight = initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_align_right);
        mNumber = initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_number);

        initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_inc_indent);
        initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_dec_indent);
        initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_link);
        initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_image);
        initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_undo);
        initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_redo);
        initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_clear);

        // enable/disable capture picture depending on whether the device
        // has a camera or not
        PackageManager packageMgr = getContext().getPackageManager();
        if (packageMgr.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            initImageButton(com.onegravity.rteditor.toolbar.R.id.toolbar_image_capture);
        } else {
            View imageCapture = findViewById(com.onegravity.rteditor.toolbar.R.id.toolbar_image_capture);
            if (imageCapture != null) imageCapture.setVisibility(View.GONE);
        }
    }

    private RTToolbarImageButton initImageButton(int id) {
        RTToolbarImageButton button = (RTToolbarImageButton) findViewById(id);
        if (button != null) {
            button.setOnClickListener(this);
        }
        return button;
    }









    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mColorPickerListener != null && mPickerId != -1) {
            SetColorPickerListenerEvent.setListener(mPickerId, mColorPickerListener);
        }
    }
    // ****************************************** RTToolbar Methods *******************************************
    @Override
    public void setToolbarContainer(ViewGroup toolbarContainer) {
        mToolbarContainer = toolbarContainer;
    }

    @Override
    public ViewGroup getToolbarContainer() {
        return mToolbarContainer == null ? this : mToolbarContainer;
    }

    @Override
    public void setToolbarListener(RTToolbarListener listener) {
        mListener = listener;
    }

    @Override
    public void removeToolbarListener() {
        mListener = null;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void setBold(boolean enabled) {
        if (mBold != null) mBold.setChecked(enabled);
    }

    @Override
    public void setItalic(boolean enabled) {
        if (mItalic != null) mItalic.setChecked(enabled);
    }

    @Override
    public void setUnderline(boolean enabled) {
        if (mUnderline != null) mUnderline.setChecked(enabled);
    }

    @Override
    public void setStrikethrough(boolean enabled) {
        if (mStrikethrough != null) mStrikethrough.setChecked(enabled);
    }

    @Override
    public void setSuperscript(boolean enabled) {
//        if (mSuperscript != null) mSuperscript.setChecked(enabled);
    }

    @Override
    public void setSubscript(boolean enabled) {
//        if (mSubscript != null) mSubscript.setChecked(enabled);
    }

    @Override
    public void setBullet(boolean enabled) {
//        if (mBullet != null) mBullet.setChecked(enabled);
    }

    @Override
    public void setNumber(boolean enabled) {
        if (mNumber != null) mNumber.setChecked(enabled);
    }

    @Override
    public void setAlignment(Layout.Alignment alignment) {
        if (mAlignLeft != null) mAlignLeft.setChecked(alignment == Layout.Alignment.ALIGN_NORMAL);
        if (mAlignCenter != null)
            mAlignCenter.setChecked(alignment == Layout.Alignment.ALIGN_CENTER);
        if (mAlignRight != null)
            mAlignRight.setChecked(alignment == Layout.Alignment.ALIGN_OPPOSITE);
    }

    @Override
    public void setFont(RTTypeface typeface) {
//        if (mFont != null) {
//            if (typeface != null) {
//                for (int pos = 0; pos < mFontAdapter.getCount(); pos++) {
//                    FontSpinnerItem item = mFontAdapter.getItem(pos);
//                    if (typeface.equals(item.getTypeface())) {
//                        mFontAdapter.setSelectedItem(pos);
//                        mFont.setSelection(pos);
//                        break;
//                    }
//                }
//            }
//            else {
//                mFontAdapter.setSelectedItem(0);
//                mFont.setSelection(0);
//            }
//        }
    }

    /**
     * Set the text size.
     *
     * @param size the text size, if -1 then no text size is set (e.g. when selection spans more than one text size)
     */
    @Override
    public void setFontSize(int size) {
//        if (mFontSize != null) {
//            if (size <= 0) {
//                mFontSizeAdapter.updateSpinnerTitle("");
//                mFontSizeAdapter.setSelectedItem(0);
//                mFontSize.setSelection(0);
//            } else {
//                size = Helper.convertSpToPx(size);
//                mFontSizeAdapter.updateSpinnerTitle(Integer.toString(size));
//                for (int pos = 0; pos < mFontSizeAdapter.getCount(); pos++) {
//                    FontSizeSpinnerItem item = mFontSizeAdapter.getItem(pos);
//                    if (size == item.getFontSize()) {
//                        mFontSizeAdapter.setSelectedItem(pos);
//                        mFontSize.setSelection(pos);
//                        break;
//                    }
//                }
//            }
//        }
    }

    @Override
    public void setFontColor(int color) {
//        if (mFontColor != null) setFontColor(color, mFontColor, mFontColorAdapter);
    }

    @Override
    public void setBGColor(int color) {
//        if (mBGColor != null) setFontColor(color, mBGColor, mBGColorAdapter);
    }

    @Override
    public void removeFontColor() {
//        if (mFontColor != null) {
//            mFontColorAdapter.setSelectedItem(0);
//            mFontColor.setSelection(0);
//        }
    }

    @Override
    public void removeBGColor() {
//        if (mBGColor != null) {
//            mBGColorAdapter.setSelectedItem(0);
//            mBGColor.setSelection(0);
//        }
    }

    private void setFontColor(int color, Spinner spinner, SpinnerItemAdapter<? extends ColorSpinnerItem> adapter) {
        int color2Compare = color & 0xffffff;
        for (int pos = 0; pos < adapter.getCount(); pos++) {
            ColorSpinnerItem item = adapter.getItem(pos);
            if (!item.isEmpty() && color2Compare == (item.getColor() & 0xffffff)) {
                adapter.setSelectedItem(pos);
                spinner.setSelection(pos);
                break;
            }
        }
    }

    // ****************************************** Item Selected Methods *******************************************

    interface DropDownNavListener<T extends SpinnerItem> {
        void onItemSelected(T spinnerItem, int position);
    }

    private DropDownNavListener<FontSpinnerItem> mFontListener = new DropDownNavListener<FontSpinnerItem>() {
        @Override
        public void onItemSelected(FontSpinnerItem spinnerItem, int position) {
            RTTypeface typeface = spinnerItem.getTypeface();
            mListener.onEffectSelected(Effects.TYPEFACE, typeface);
        }
    };

//    private SplHorizontalRTToolbar.DropDownNavListener<FontSizeSpinnerItem> mFontSizeListener = new SplHorizontalRTToolbar.DropDownNavListener<FontSizeSpinnerItem>() {
//        @Override
//        public void onItemSelected(FontSizeSpinnerItem spinnerItem, int position) {
//            int size = spinnerItem.getFontSize();
//            mFontSizeAdapter.updateSpinnerTitle(spinnerItem.isEmpty() ? "" : Integer.toString(size));
//            size = Helper.convertPxToSp(size);
//            mListener.onEffectSelected(Effects.FONTSIZE, size);
//        }
//    };

//    private SplHorizontalRTToolbar.DropDownNavListener<FontColorSpinnerItem> mFontColorListener = new SplHorizontalRTToolbar.DropDownNavListener<FontColorSpinnerItem>() {
//        @Override
//        public void onItemSelected(final FontColorSpinnerItem spinnerItem, int position) {
//            if (spinnerItem.isCustom()) {
//                mColorPickerListener = new ColorPickerListener() {
//                    @Override
//                    public void onColorChanged(int color) {
//                        mCustomColorFont = color;
//                        spinnerItem.setColor(color);
//                        mFontColorAdapter.notifyDataSetChanged();
//                        if (mListener != null) {
//                            mListener.onEffectSelected(Effects.FONTCOLOR, color);
//                        }
//                    }
//                    @Override
//                    public void onDialogClosing() {
//                        mPickerId = -1;
//                    }
//                };
//                mPickerId = new ColorPickerDialog(getContext(), mCustomColorFont, false).show();
//                SetColorPickerListenerEvent.setListener(mPickerId, mColorPickerListener);
//            } else if (mListener != null) {
//                Integer color = spinnerItem.isEmpty() ? null : spinnerItem.getColor();
//                mListener.onEffectSelected(Effects.FONTCOLOR, color);
//            }
//        }
//    };

//    private SplHorizontalRTToolbar.DropDownNavListener<BGColorSpinnerItem> mBGColorListener = new SplHorizontalRTToolbar.DropDownNavListener<BGColorSpinnerItem>() {
//        @Override
//        public void onItemSelected(final BGColorSpinnerItem spinnerItem, int position) {
//            if (spinnerItem.isCustom()) {
//                mColorPickerListener = new ColorPickerListener() {
//                    @Override
//                    public void onColorChanged(int color) {
//                        mCustomColorBG = color;
//                        spinnerItem.setColor(color);
//                        mBGColorAdapter.notifyDataSetChanged();
//                        if (mListener != null) {
//                            mListener.onEffectSelected(Effects.BGCOLOR, color);
//                        }
//                    }
//                    public void onDialogClosing() {
//                        mPickerId = -1;
//                    }
//                };
//                mPickerId = new ColorPickerDialog(getContext(), mCustomColorBG, false).show();
//                SetColorPickerListenerEvent.setListener(mPickerId, mColorPickerListener);
//            } else if (mListener != null) {
//                Integer color = spinnerItem.isEmpty() ? null : spinnerItem.getColor();
//                mListener.onEffectSelected(Effects.BGCOLOR, color);
//            }
//        }
//    };

    @Override
    public void onClick(View v) {
        if (mListener != null) {

            int id = v.getId();
            if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_bold) {
                mBold.setChecked(!mBold.isChecked());
                mListener.onEffectSelected(Effects.BOLD, mBold.isChecked());
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_italic) {
                mItalic.setChecked(!mItalic.isChecked());
                mListener.onEffectSelected(Effects.ITALIC, mItalic.isChecked());
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_underline) {
                mUnderline.setChecked(!mUnderline.isChecked());
                mListener.onEffectSelected(Effects.UNDERLINE, mUnderline.isChecked());
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_strikethrough) {
                mStrikethrough.setChecked(!mStrikethrough.isChecked());
                mListener.onEffectSelected(Effects.STRIKETHROUGH, mStrikethrough.isChecked());
            }

//            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_superscript) {
//                mSuperscript.setChecked(!mSuperscript.isChecked());
//                mListener.onEffectSelected(Effects.SUPERSCRIPT, mSuperscript.isChecked());
//                if (mSuperscript.isChecked() && mSubscript != null) {
//                    mSubscript.setChecked(false);
//                    mListener.onEffectSelected(Effects.SUBSCRIPT, mSubscript.isChecked());
//                }
//            }
//
//            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_subscript) {
//                mSubscript.setChecked(!mSubscript.isChecked());
//                mListener.onEffectSelected(Effects.SUBSCRIPT, mSubscript.isChecked());
//                if (mSubscript.isChecked() && mSuperscript != null) {
//                    mSuperscript.setChecked(false);
//                    mListener.onEffectSelected(Effects.SUPERSCRIPT, mSuperscript.isChecked());
//                }
//            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_align_left) {
                if (mAlignLeft != null) mAlignLeft.setChecked(true);
                if (mAlignCenter != null) mAlignCenter.setChecked(false);
                if (mAlignRight != null) mAlignRight.setChecked(false);
                mListener.onEffectSelected(Effects.ALIGNMENT, Layout.Alignment.ALIGN_NORMAL);
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_align_center) {
                if (mAlignLeft != null) mAlignLeft.setChecked(false);
                if (mAlignCenter != null) mAlignCenter.setChecked(true);
                if (mAlignRight != null) mAlignRight.setChecked(false);
                mListener.onEffectSelected(Effects.ALIGNMENT, Layout.Alignment.ALIGN_CENTER);
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_align_right) {
                if (mAlignLeft != null) mAlignLeft.setChecked(false);
                if (mAlignCenter != null) mAlignCenter.setChecked(false);
                if (mAlignRight != null) mAlignRight.setChecked(true);
                mListener.onEffectSelected(Effects.ALIGNMENT, Layout.Alignment.ALIGN_OPPOSITE);
            }
//
//            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_bullet) {
//                mBullet.setChecked(!mBullet.isChecked());
//                boolean isChecked = mBullet.isChecked();
//                mListener.onEffectSelected(Effects.BULLET, isChecked);
//                if (isChecked && mNumber != null) {
//                    mNumber.setChecked(false);    // numbers will be removed by the NumberEffect.applyToSelection
//                }
//            }

//            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_number) {
//                mNumber.setChecked(!mNumber.isChecked());
//                boolean isChecked = mNumber.isChecked();
//                mListener.onEffectSelected(Effects.NUMBER, isChecked);
//                if (isChecked && mBullet != null) {
//                    mBullet.setChecked(false);    // bullets will be removed by the BulletEffect.applyToSelection
//                }
//            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_inc_indent) {
                mListener.onEffectSelected(Effects.INDENTATION, Helper.getLeadingMarging());
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_dec_indent) {
                mListener.onEffectSelected(Effects.INDENTATION, -Helper.getLeadingMarging());
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_link) {
                mListener.onCreateLink();
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_image) {
                mListener.onPickImage();
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_image_capture) {
                mListener.onCaptureImage();
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_clear) {
                mListener.onClearFormatting();
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_undo) {
                mListener.onUndo();
            }

            else if (id == com.onegravity.rteditor.toolbar.R.id.toolbar_redo) {
                mListener.onRedo();
            }
        }
    }

}
