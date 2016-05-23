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
	//�����εױߵ������
	private final int DIMENSION_TRIANGLE_WIDTH_MAX = (int) (getScreenWidth() / 3 * RADIO_TRIANGLE_WIDTH);
	//��ʼ����ƫ��λ��
	private int mInitTranslationX;
	private int mTranslationX;
	
	private int mTabVisibleCount;
	private static final int COUNT_DEFAULT_TAB = 4;
	private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
	private static final int COLOR_TEXT_HIGHLIGHT = 0xFFFFFFFF;
	
	public List<String> mTitles;

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		//��ȡ�ɼ�Tab����
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicator);
		
		mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_visible_tab_count,
				COUNT_DEFAULT_TAB);
		if (mTabVisibleCount < 0) {
			mTabVisibleCount = COUNT_DEFAULT_TAB;
		}
		
		a.recycle();
		
		//��ʼ������
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
		//w�ǿؼ��Ŀ��
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
	 * ��̬�޸Ŀɼ�tab�Ŀ�ȣ��Ӷ����ƿɼ�tab������
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
	 * �����Ļ���
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
	 * ��ʼ��������
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
	 * ָʾ��������ָ���й���
	 * @param position
	 * @param Offset
	 */
	public void scroll(int position, float Offset) {
		int tabWidth = getWidth() / mTabVisibleCount;
		mTranslationX = (int) (tabWidth  * (position + Offset));
		
		//�����ƶ�����tab�����ƶ������һ��ʱ
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
	 * ���ÿɼ�tab����
	 * @param count
	 */
	public void setVisibleTabCount(int count) {
		mTabVisibleCount = count;
	}
	
	/**
	 * ����title����Tab
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
	
	//ϰ������д��
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
	 * ���ù�����viewPager
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
	 * ����ĳ��tab���ı�
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
	 * ����tab���ı���ɫ
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
	 * ����tab�ĵ���¼�
	 */
	private void setItemClickEvent() {
		int cCount = getChildCount();
		
		for (int i = 0; i < cCount; i++) {
			final int j = i;
			View view = getChildAt(i);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//viewpager��̬�仯����������Ҳ�ڶ�̬�仯
					mViewPager.setCurrentItem(j);
				}
			});
		}
	}
}
