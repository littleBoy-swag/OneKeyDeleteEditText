package CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.icu.text.MessageFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

import cn.edu.hhu.panfei.onekeydeleteedittext.R;

/**
 * Created by hasee on 2017/8/26.
 */

public class OneKeyDeleteEditText extends EditText {

    private int deleteResourceId;//删除图标资源ID
    private Drawable deleteIcon;//删除图标
    private int delete_x, delete_y, delete_width, delete_height;//删除图标起点(x,y)，宽高(px)

    private int left_clickResId, left_unclickResId;//左侧图标资源ID
    private Drawable left_click, left_unclick;//
    private int left_x, left_y, left_width, left_height;//

    private int cursor;

    //分隔线
    private int lineColor_click, lineColor_unclick;
    private int color;
    private int linePosition;

    private Paint mPaint;


    public OneKeyDeleteEditText(Context context) {
        super(context);
    }

    public OneKeyDeleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OneKeyDeleteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //获取控件资源
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OneKeyDeleteEditText);

        /**
         * 初始化左侧图标
         */
        //a.点击状态的左侧图标
        //1.获取资源ID
        left_clickResId = typedArray.getResourceId(R.styleable.OneKeyDeleteEditText_ic_left_click, R.drawable.ic_left_click);
        //2.根据资源ID获取图标资源(转化成Drawable对象)
        left_click = getResources().getDrawable(left_clickResId);
        //3.设置图标大小
        left_x = typedArray.getInteger(R.styleable.OneKeyDeleteEditText_left_x, 0);
        left_y = typedArray.getInteger(R.styleable.OneKeyDeleteEditText_left_y, 0);
        left_width = typedArray.getInteger(R.styleable.OneKeyDeleteEditText_left_width, 60);
        left_height = typedArray.getInteger(R.styleable.OneKeyDeleteEditText_left_height, 60);

        left_click.setBounds(left_x, left_y, left_width, left_height);
        // Drawable.setBounds(x,y,width,height) = 设置Drawable的初始位置、宽和高等信息
        // x = 组件在容器X轴上的起点、y = 组件在容器Y轴上的起点、width=组件的长度、height = 组件的高度

        //a.点击状态的左侧图标
        //1.获取资源ID
        left_unclickResId = typedArray.getResourceId(R.styleable.OneKeyDeleteEditText_ic_left_unclick, R.drawable.ic_left_unclick);
        // 2.根据资源ID获取图标资源（转化成Drawable对象）
        // 3.设置图标大小（此处默认左侧图标点解 & 未点击状态的大小相同）
        left_unclick = getResources().getDrawable(left_unclickResId);
        left_unclick.setBounds(left_x, left_y, left_width, left_height);

        /**
         * 初始化删除图标
         */
        //1.获取资源ID
        deleteResourceId = typedArray.getResourceId(R.styleable.OneKeyDeleteEditText_ic_delete, R.drawable.delete);
        //2.根据资源ID获取图标资源（转化成Drawable对象）
        deleteIcon = getResources().getDrawable(deleteResourceId);
        //设置图标大小
        delete_x = typedArray.getInteger(R.styleable.OneKeyDeleteEditText_delete_x, 0);
        delete_y = typedArray.getInteger(R.styleable.OneKeyDeleteEditText_delete_y, 0);
        delete_width = typedArray.getInteger(R.styleable.OneKeyDeleteEditText_delete_width, 60);
        delete_height = typedArray.getInteger(R.styleable.OneKeyDeleteEditText_delete_height, 60);
        deleteIcon.setBounds(delete_x, delete_y, delete_width, delete_height);

        /**
         * 设置EditText左侧和右侧的图片
         */
        setCompoundDrawables(left_unclick, null, null, null);
        // setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom)介绍
        // 作用：在EditText上、下、左、右设置图标（相当于android:drawableLeft=""  android:drawableRight=""）
        // 备注：传入的Drawable对象必须已经setBounds(x,y,width,height)，即必须设置过初始位置、宽和高等信息
        // x:组件在容器X轴上的起点 y:组件在容器Y轴上的起点 width:组件的长度 height:组件的高度
        // 若不想在某个地方显示，则设置为null

        // 另外一个相似的方法：setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom)
        // 作用：在EditText上、下、左、右设置图标
        // 与setCompoundDrawables的区别：setCompoundDrawablesWithIntrinsicBounds（）传入的Drawable的宽高=固有宽高（自动通过getIntrinsicWidth（）& getIntrinsicHeight（）获取）
        // 不需要设置setBounds(x,y,width,height)

        /**
         * 初始化光标
         */
        //原理：通过反射机制，动态设置光标
        //1.获取资源ID
        cursor = typedArray.getResourceId(R.styleable.OneKeyDeleteEditText_cursor, R.drawable.cursor);
        try {
            //2.通过反射获取光标属性
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            //3.传入资源ID
            f.set(this, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 初始化分隔线
         */
        //1.设置画笔
        mPaint = new Paint();
        mPaint.setStrokeWidth(2.0f);

        //2.设置分隔线颜色
        int lineColorClick_default = context.getResources().getColor(R.color.linecolor_click);
        int lineColorUnclick_default = context.getResources().getColor(R.color.linecolor_unclick);
        lineColor_click = typedArray.getColor(R.styleable.OneKeyDeleteEditText_lineColor_click, lineColorClick_default);
        lineColor_unclick = typedArray.getColor(R.styleable.OneKeyDeleteEditText_lineColor_click, lineColorUnclick_default);
        color = lineColor_unclick;

        mPaint.setColor(lineColor_unclick);//分割线默认颜色
        setTextColor(color);//字体默认颜色

        //3.分割线位置
        linePosition = typedArray.getInteger(R.styleable.OneKeyDeleteEditText_linePosition, 1);
        //消除自带下划线
        setBackground(null);

    }

    /**
     * 重写onTextChanged方法
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setDeleteIconVisible(hasFocus() && text.length() > 0, hasFocus());
        // hasFocus()返回是否获得EditTEXT的焦点，即是否选中
        // setDeleteIconVisible（） = 根据传入的是否选中 & 是否有输入来判断是否显示删除图标->>关注1
    }

    /**
     * 重写onFocusChanged方法
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setDeleteIconVisible(focused && length() > 0, focused);
        // focused = 是否获得焦点
        // 同样根据setDeleteIconVisible（）判断是否要显示删除图标->>关注1
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Drawable drawable = deleteIcon;
                if (drawable != null && event.getX() <= (getWidth() - getPaddingRight()) &&
                        event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {
                    // 判断条件说明
                    // event.getX() ：抬起时的位置坐标
                    // getWidth()：控件的宽度
                    // getPaddingRight():删除图标图标右边缘至EditText控件右边缘的距离
                    // 即：getWidth() - getPaddingRight() = 删除图标的右边缘坐标 = X1
                    // getWidth() - getPaddingRight() - drawable.getBounds().width() = 删除图标左边缘的坐标 = X2
                    // 所以X1与X2之间的区域 = 删除图标的区域
                    // 当手指抬起的位置在删除图标的区域（X2=<event.getX() <=X1），即视为点击了删除图标 = 清空搜索框内容
                    setText("");
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 关注1
     * 作用：判断是否显示删除图标 & 设置分割线颜色
     */
    private void setDeleteIconVisible(boolean deleteVisible, boolean leftVisible) {
        setCompoundDrawables(leftVisible ? left_click : left_unclick, null,
                deleteVisible ? deleteIcon : null, null);
        color = leftVisible ? lineColor_click : lineColor_unclick;
        setTextColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(color);
        setTextColor(color);
        // 绘制分割线
        // 需要考虑：当输入长度超过输入框时，所画的线需要跟随着延伸
        // 解决方案：线的长度 = 控件长度 + 延伸后的长度
        int x = this.getScrollX(); // 获取延伸后的长度
        int w = this.getMeasuredWidth(); // 获取控件长度

        // 传入参数时，线的长度 = 控件长度 + 延伸后的长度
        canvas.drawLine(0, this.getMeasuredHeight() - linePosition, w + x,
                this.getMeasuredHeight() - linePosition, mPaint);
    }
}
