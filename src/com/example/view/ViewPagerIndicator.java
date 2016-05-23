package com.example.view;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.imooc_viewpagerindicator.R;

public class ViewPagerIndicator extends LinearLayout {
	private Paint mPaint;
	private Path mPath;
	private int mTriangleWidth;
	private int mTriangleHeight;
	private static final float RADIO_TRIANGLE_WIDTH = 1 / 6f;
	//三角形底边的最大宽度
	private final int DIMENSION_TRIANGLE_WIDTH_MAX = (int) (getScreenWidth() / 3 * RADIO_TRIANGLE_WIDTH);
	//初始化的偏移位置
	private int mInitTranslationX;
	private int mTranslationX;
	
	private int mTabVisibleCount;
	private static final int COUNT_DEFAULT_TAB = 4;
	private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
	private static final int COLOR_TEXT_HIGHLIGHT = 0xFFFFFFFF;
	
	public List<String> mTitles;

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		//获取可见Tab数量
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicator);
		
		mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_visible_tab_count,
				COUNT_DEFAULT_TAB);
		if (mTabVisibleCount < 0) {
			mTabVisibleCount = COUNT_DEFAULT_TAB;
		}
		
		a.recycle();
		
		//初始化画笔
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.parseColor("#ffffffff"));
		mPaint.setStyle(Style.FILL);
		mPaint.setPathEffect(new CornerPathEffect(3));
	}

	public ViewPagerIndicator(Context context) {
		this(context, null);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//w是控件的宽度
		mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE_WIDTH);
		mTriangleWidth = Math.min(DIMENSION_TRIANGLE_WIDTH_MAX, mTriangleWidth);
		mInitTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;
		
		initTriangle();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();
		
		canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 2);
		canvas.drawPath(mPath, mPaint);
		
		canvas.restore();
		super.dispatchDraw(canvas);
	}
	
	/**
	 * 动态修改可见tab的宽度，从而控制可见tab的数量
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		int cCount = getChildCount();
		if (cCount == 0) 
			return;
		
		for (int i = 0; i < cCount; i++) {
			View v = getChildAt(i);
			LinearLayout.LayoutParams lp = (LayoutParams) v.getLayoutParams();
			lp.weight = 0;
			lp.width = getScreenWidth() / mTabVisibleCount;
			v.setLayoutParams(lp);
		}
		
		setItemClickEvent();
	}
	
	/**
	 * 获得屏幕宽度
	 * @return
	 */
	private int getScreenWidth() {
		WindowManager wm = (WindowManager) getContext().
				getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 初始化三角形
	 */
	private void initTriangle() {
		mTriangleHeight = mTriangleWidth / 2;
		mPath = new Path();
		mPath.moveTo(0, 0);
		mPath.lineTo(mTriangleWidth, 0);
		mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
		mPath.close();
	}

	/**
	 * 指示器跟随手指进行滚动
	 * @param position
	 * @param Offset
	 */
	public void scroll(int position, float Offset) {
		int tabWidth = getWidth() / mTabVisibleCount;
		mTranslationX = (int) (tabWidth  * (position + Offset));
		
		//容器移动，在tab处于移动至最后一个时
		if (position >= (mTabVisibleCount - 2) && Offset > 0 
			&& getChildCount() > mTabVisibleCount) {
			if (mTabVisibleCount != 1) {
				this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth +
						(int)(tabWidth * Offset), 0);
			} else {
				this.scrollTo(position * tabWidth + (int) (Offset * tabWidth), 0); 
			}
		}
		
		invalidate();
	}
	
	public void setTabItemTitles(List<String> titles) {
		if (titles != null && titles.size() > 0) {
			this.removeAllViews();
			mTitles = titles;
			for (String title : mTitles) {
				addView(generateTextView(title));
			}
		}
		
		setItemClickEvent();
	}

	/**
	 * 设置可见tab数量
	 * @param count
	 */
	public void setVisibleTabCount(int count) {
		mTabVisibleCount = count;
	}
	
	/**
	 * 根据title创建Tab
	 * @param title
	 * @return
	 */
	private View generateTextView(String title) {
		TextView tv = new TextView(getContext());
		LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT);
		lp.width = getScreenWidth() / mTabVisibleCount;
		tv.setText(title);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(COLOR_TEXT_NORMAL);
		tv.setLayoutParams(lp);
		return tv;
	}
	
	//习惯这种写法
	private ViewPager mViewPager;
	
	public interface PageOnChangeListener{
		public void onPageSelected(int position);
		public void onPageScrollStateChanged(int state);
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);
	}
	
	public PageOnChangeListener mListener;
	
	public void setOnPageChangeListener(PageOnChangeListener listener) {
		mListener = listener;
	}
	
	/**
	 * 设置关联的viewPager
	 * @param viewPager
	 * @param pos
	 */
	public void setViewPager(ViewPager viewPager, int pos) {
		mViewPager = viewPager;
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if (mListener != null) {
					mListener.onPageSelected(position);
				}
				highLightTextView(position);
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				//tabWidth * positionOffset + position * tabwidth
				scroll(position, positionOffset);
				
				if (mListener != null) {
					mListener.onPageScrolled(position,
							positionOffset, positionOffsetPixels);
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				if (mListener != null) {
					mListener.onPageScrollStateChanged(state);
				}
			}
		});
		
		mViewPager.setCurrentItem(pos);
		highLightTextView(pos);
	}
	
	/**
	 * 高亮某个tab的文本
	 * @param pos
	 */
	public void highLightTextView(int pos) {
		resetTextViewColor();
		View view = getChildAt(pos);
		if (view instanceof TextView) {
			((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHT);
		}
	}
	
	/**
	 * 重置tab的文本颜色
	 */
	public void resetTextViewColor() {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof TextView) {
				((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
			}
		}
	}
	
	/**
	 * 设置tab的点击事件
	 */
	private void setItemClickEvent() {
		int cCount = getChildCount();
		
		for (int i = 0; i < cCount; i++) {
			final int j = i;
			View view = getChildAt(i);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//viewpager动态变化，高亮方法也在动态变化
					mViewPager.setCurrentItem(j);
				}
			});
		}
	}
}
