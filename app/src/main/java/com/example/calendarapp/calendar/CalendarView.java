package com.example.calendarapp.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapp.R;

import org.joda.time.LocalDate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CalendarView extends LinearLayout {

    @IntDef({SINGLE, MULTIPLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChooseMode {
    }

    public static final int SINGLE = 0;
    public static final int MULTIPLE = 1;


    private final String tag = getClass().getName();

    private View headerView;
    private View defaultHeaderView;

    private RecyclerView rvCalendar;

    //日历普通日期的文字颜色和文字大小
    private int calendarTextColor;
    private int calendarTextSize;

    //头部字体大小和颜色
    private int defaultHeaderWeekendTextColor;
    private int defaultHeaderTextColor;
    private int defaultHeaderTextSize;

    //选中日期的背景和文字颜色
    private boolean showChooseUi;
    private Drawable chooseBackGround;
    private int chooseTextColor;
    //当日背景色
    private boolean showTodayUi;
    private Drawable todayBackGround;
    private int todayTextColor;

    //小于最小日期 大于最大日期的背景
    private boolean showUnavailableUi;
    private Drawable unavailableBackGround;
    private int unavailableTextColor;

    //小于最小日期 大于最大日期的背景
    private boolean showNotCurMonthDate;//是否显示非本月日期
    private boolean showNotCurMonthUi;
    private boolean clickNotCurMonthUi;//非当月数据是否可点击
    private Drawable NotCurMonthBackGround;
    private int NotCurMonthTextColor;


    private Context context;

    private LocalDate maxDate;//最大日期 默认当日
    private LocalDate minDate;//最小日期 默认两年前

    private int chooseMode = 0;
    private LocalDate chooseDate;//当前选中日期
    private List<LocalDate> multipleChooseList;//当前多选日期列表

    //用于特殊需求的ui展示
    private boolean showSpecialUi;
    private Drawable specialBackGround;
    private int specialTextColor;

    private List<LocalDate> specialChooseList;//当前多选日期列表

    private int width = -1;
    private int height = -1;
    private int itemHeight;
    private LinkedList<LocalDate> dateList = new LinkedList<>();
    private CalendarAdapter calendarAdapter;
    private int currentYear;
    private int currentMonth;


    private ChooseDateChangeListener chooseDateChangeListener;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.e(tag, "init");
        this.context = context;
        setOrientation(VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        if (typedArray != null) {
            calendarTextColor = typedArray.getColor(R.styleable.CalendarView_calendarTextColor, Color.parseColor("#333333"));
            calendarTextSize = typedArray.getDimensionPixelSize(R.styleable.CalendarView_calendarTextSize, 32);

            defaultHeaderTextColor = typedArray.getColor(R.styleable.CalendarView_defaultHeaderTextColor, Color.BLACK);
            defaultHeaderWeekendTextColor = typedArray.getColor(R.styleable.CalendarView_defaultHeaderWeekendTextColor, Color.parseColor("#f90000"));
            defaultHeaderTextSize = typedArray.getDimensionPixelSize(R.styleable.CalendarView_defaultHeaderTextSize, 32);

            showChooseUi = typedArray.getBoolean(R.styleable.CalendarView_showChooseUi, true);
            chooseTextColor = typedArray.getColor(R.styleable.CalendarView_chooseTextColor, calendarTextColor);
            chooseBackGround = typedArray.getDrawable(R.styleable.CalendarView_chooseBackGround);

            showTodayUi = typedArray.getBoolean(R.styleable.CalendarView_showTodayUi, true);
            todayTextColor = typedArray.getColor(R.styleable.CalendarView_todayTextColor, calendarTextColor);
            todayBackGround = typedArray.getDrawable(R.styleable.CalendarView_todayBackGround);

            showUnavailableUi = typedArray.getBoolean(R.styleable.CalendarView_showUnavailableUi, true);
            unavailableTextColor = typedArray.getColor(R.styleable.CalendarView_unavailableTextColor, calendarTextColor);
            unavailableBackGround = typedArray.getDrawable(R.styleable.CalendarView_unavailableBackGround);

            showSpecialUi = typedArray.getBoolean(R.styleable.CalendarView_showSpecialUi, false);
            specialTextColor = typedArray.getColor(R.styleable.CalendarView_specialTextColor, calendarTextColor);
            specialBackGround = typedArray.getDrawable(R.styleable.CalendarView_specialBackGround);


            showNotCurMonthDate = typedArray.getBoolean(R.styleable.CalendarView_showNotCurMonthDate, true);
            showNotCurMonthUi = typedArray.getBoolean(R.styleable.CalendarView_showNotCurMonthUi, true);
            clickNotCurMonthUi = typedArray.getBoolean(R.styleable.CalendarView_clickNotCurMonthUi, true);
            NotCurMonthTextColor = typedArray.getColor(R.styleable.CalendarView_NotCurMonthTextColor, unavailableTextColor);
            NotCurMonthBackGround = typedArray.getDrawable(R.styleable.CalendarView_NotCurMonthBackGround);
            chooseMode = typedArray.getInt(R.styleable.CalendarView_chooseMode, SINGLE);

            typedArray.recycle();
        }
        initDate();
        initDefaultHeaderView(context);
        initCalendarRecycleView(context);

    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void setChooseDateChangeListener(ChooseDateChangeListener chooseDateChangeListener) {
        this.chooseDateChangeListener = chooseDateChangeListener;
    }
    /**
     * 刷新日历Ui
     * created by dongliang
     *  2020/5/29  14:05
     */
    public void refreshCalendarUi() {
        if (calendarAdapter != null) {
            calendarAdapter.notifyDataSetChanged();
        }
    }

    public LocalDate getChooseDate() {
        return chooseDate;
    }

    public void setChooseDate(LocalDate chooseDate) {
        if (chooseDate.isAfter(maxDate) || chooseDate.isBefore(minDate)) {
            Log.w(tag, "选择日期必须在最小日期和最大日期之间");
            return;
        }
        this.chooseDate = chooseDate;
        refreshCalendarUi();
        if (chooseDateChangeListener != null) {
            chooseDateChangeListener.chooseDateChange(chooseDate);
        }
    }

    public void refreshDefaultHeaderUi() {
        TextView monday = (TextView) defaultHeaderView.findViewById(R.id.tv_monday);
        TextView tuesday = (TextView) defaultHeaderView.findViewById(R.id.tv_tuesday);
        TextView wednesday = (TextView) defaultHeaderView.findViewById(R.id.tv_wednesday);
        TextView thursday = (TextView) defaultHeaderView.findViewById(R.id.tv_thursday);
        TextView friday = (TextView) defaultHeaderView.findViewById(R.id.tv_friday);
        TextView saturday = (TextView) defaultHeaderView.findViewById(R.id.tv_saturday);
        TextView sunday = (TextView) defaultHeaderView.findViewById(R.id.tv_sunday);

        monday.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultHeaderTextSize);
        tuesday.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultHeaderTextSize);
        wednesday.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultHeaderTextSize);
        thursday.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultHeaderTextSize);
        friday.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultHeaderTextSize);
        saturday.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultHeaderTextSize);
        sunday.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultHeaderTextSize);

        monday.setTextColor(defaultHeaderTextColor);
        tuesday.setTextColor(defaultHeaderTextColor);
        wednesday.setTextColor(defaultHeaderTextColor);
        thursday.setTextColor(defaultHeaderTextColor);
        friday.setTextColor(defaultHeaderTextColor);
        saturday.setTextColor(defaultHeaderWeekendTextColor);
        sunday.setTextColor(defaultHeaderWeekendTextColor);

    }


    private void initDefaultHeaderView(Context context) {
        defaultHeaderView = LayoutInflater.from(context).inflate(R.layout.calendar_header, null);
        headerView = defaultHeaderView;
        refreshDefaultHeaderUi();
    }

    private void initCalendarRecycleView(Context context) {
        rvCalendar = new RecyclerView(context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
        rvCalendar.setLayoutManager(gridLayoutManager);
        calendarAdapter = new CalendarAdapter();
        rvCalendar.setAdapter(calendarAdapter);

    }

    private void initDate() {
        maxDate = LocalDate.now();
        minDate = LocalDate.now().minusYears(2);
        currentYear = LocalDate.now().getYear();
        currentMonth = LocalDate.now().getMonthOfYear();

    }

    private void setDate() {

        int days = getDaysNumByMonth(currentYear, currentMonth);
        LocalDate localDate = LocalDate.parse(currentYear + "-" + currentMonth + "-" + "01");
        int weekDay = localDate.getDayOfWeek();
        dateList.clear();
        if (weekDay < 7) {
            for (int i = 1; i <= weekDay; i++) {
                dateList.addFirst(localDate.minusDays(i));
            }
        }
        for (int i = 0; i < days; i++) {
            dateList.add(localDate.plusDays(i));
        }

        for (int i = 42 - dateList.size(); i > 0; i--) {
            dateList.add(dateList.getLast().plusDays(1));
        }
        if (calendarAdapter != null) {
            calendarAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 下个月
     * created by dongliang
     * 2020/5/28  17:04
     */
    public void nextMonth() {
        LocalDate localDate = LocalDate.parse(currentYear + "-" + currentMonth + "-" + "01");
        LocalDate next = localDate.plusMonths(1);
        currentYear = next.getYear();
        currentMonth = next.getMonthOfYear();
        setDate();
    }

    /**
     * 上个月
     * created by dongliang
     * 2020/5/28  17:04
     */
    public void preMonth() {
        LocalDate localDate = LocalDate.parse(currentYear + "-" + currentMonth + "-" + "01");
        LocalDate next = localDate.minusMonths(1);
        currentYear = next.getYear();
        currentMonth = next.getMonthOfYear();
        setDate();
    }

    /**
     * 设置最大日期
     * created by dongliang
     * 2020/5/28  17:06
     */
    public void setMaxDate(LocalDate max) {
        if (max != null) {
            this.maxDate = max;
            if (calendarAdapter != null) {
                calendarAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 设置最小日期
     * created by dongliang
     * 2020/5/28  17:06
     */
    public void setMinDate(LocalDate min) {
        if (min != null) {
            this.maxDate = min;
            if (calendarAdapter != null) {
                calendarAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 当前年份
     * created by dongliang
     * 2020/5/29  11:18
     */
    public int getCurrentYear() {
        return currentYear;
    }

    /**
     * 当前月份
     * created by dongliang
     * 2020/5/29  11:18
     */
    public int getCurrentMonth() {
        return currentMonth;
    }

    /**
     * 根据月份获取当月的天数
     * created by dongliang
     * 2019/9/11  16:39
     */
    private int getDaysNumByMonth(int year, int month) {
        int days;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            days = 31;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            days = 30;
        } else {
            if (((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0)) {
                days = 29;
            } else {
                days = 28;
            }
        }

        return days;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.e(tag, "layout");


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(tag, "onsize");
        width = getWidth();
        height = getHeight();
        addChild();
        setDate();

    }

    private void addChild() {
        this.removeAllViews();
        if (headerView != null) {
            this.addView(headerView, LayoutParams.MATCH_PARENT, height / 7);
            this.addView(rvCalendar, LayoutParams.MATCH_PARENT, height / 7 * 6);

        } else {
            this.addView(rvCalendar, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        itemHeight = height / 7;
    }


    private class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarHolder> {


        @NonNull
        @Override
        public CalendarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, itemHeight);
            view.setLayoutParams(layoutParams);
            CalendarHolder holder = new CalendarHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull CalendarHolder holder, int position) {
            final LocalDate date = dateList.get(position);
            holder.tvDate.setText(date.getDayOfMonth() + "");
            if (showNotCurMonthDate) {
                holder.tvDate.setVisibility(VISIBLE);
            } else {
                if (date.getMonthOfYear() == currentMonth) {
                    holder.tvDate.setVisibility(VISIBLE);
                } else {
                    holder.tvDate.setVisibility(INVISIBLE);
                }

            }
            holder.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarTextSize);
            holder.tvDate.setTextColor(calendarTextColor);
            boolean clickable = true;

            //非当月日期
            if (showNotCurMonthUi && date.getMonthOfYear() != currentMonth) {
                holder.tvDate.setTextColor(NotCurMonthTextColor);
                if (NotCurMonthBackGround != null) {
                    holder.tvDate.setShapeBackGround(NotCurMonthBackGround);
                }
            }

            //超出范围
            if (showUnavailableUi && (date.isBefore(minDate) || date.isAfter(maxDate))) {
                holder.tvDate.setTextColor(unavailableTextColor);
                clickable = false;
                if (unavailableBackGround != null) {
                    holder.tvDate.setShapeBackGround(unavailableBackGround);
                }
            }

            if (showSpecialUi&&specialChooseList!=null&&specialChooseList.size()>0){
                for (LocalDate choose:specialChooseList){
                    if (choose.equals(date)){
                        holder.tvDate.setTextColor(specialTextColor);
                        if (specialBackGround != null) {
                            holder.tvDate.setShapeBackGround(specialBackGround);
                        }
                        break;
                    }
                }

            }
            //当日
            if (showTodayUi && date.equals(LocalDate.now())) {
                holder.tvDate.setTextColor(todayTextColor);
                if (todayBackGround != null) {
                    holder.tvDate.setShapeBackGround(todayBackGround);
                }
            }

            //选中
            if (chooseMode==SINGLE) {
                if (showChooseUi && chooseDate != null && date.equals(chooseDate)) {
                    holder.tvDate.setTextColor(chooseTextColor);
                    if (chooseBackGround != null) {
                        holder.tvDate.setShapeBackGround(chooseBackGround);
                    }
                }
            }else {
                //多选中模式
                if (showChooseUi && multipleChooseList != null && multipleChooseList.size()>0) {
                    for (LocalDate choose:multipleChooseList){
                        if (choose.equals(date)){
                            holder.tvDate.setTextColor(chooseTextColor);
                            if (chooseBackGround != null) {
                                holder.tvDate.setShapeBackGround(chooseBackGround);
                            }
                            break;
                        }
                    }
                }

            }

            if (!clickNotCurMonthUi && date.getMonthOfYear() != currentMonth) {
                clickable = false;
            }

            if (clickable) {
                holder.tvDate.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chooseMode==SINGLE){
                        chooseDate = date;
                        }else {
                            if (multipleChooseList==null){
                                multipleChooseList=new ArrayList<>();
                            }
                            multipleChooseList.add(date);
                        }
                        if (chooseDateChangeListener != null) {
                            chooseDateChangeListener.chooseDateChange(date);

                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                holder.tvDate.setOnClickListener(null);
            }

        }

        @Override
        public int getItemCount() {
            return 42;
        }

        class CalendarHolder extends RecyclerView.ViewHolder {
            private SquareBackGroundTextView tvDate;

            public CalendarHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
            }
        }
    }


    public int getChooseMode() {
        return chooseMode;
    }

    /**
     * 设置选择日期的类型 单选 或者多选
     * created by dongliang
     *  2020/5/29  14:01
     */
    public void setChooseMode(@ChooseMode int chooseMode) {

        if (this.chooseMode != chooseMode) {
            this.chooseMode = chooseMode;
            chooseDate=null;
            if (multipleChooseList!=null){
                multipleChooseList.clear();
            }
            refreshCalendarUi();
        }
    }

    public List<LocalDate> getMultipleChooseList() {
        return multipleChooseList;
    }

    public void setMultipleChooseList(List<LocalDate> multipleChooseList) {
        this.multipleChooseList = multipleChooseList;
        refreshCalendarUi();
    }
    public void clearMultipleChooseList(){
        if (multipleChooseList!=null){
            multipleChooseList.clear();
            refreshCalendarUi();
        }
    }
    public void addMultipleChoose(LocalDate localDate){
        if (multipleChooseList==null){
            multipleChooseList=new ArrayList<>();
        }
        multipleChooseList.add(localDate);
        refreshCalendarUi();

    }

    public List<LocalDate> getSpecialChooseList() {
        return specialChooseList;
    }

    public void setSpecialChooseList(List<LocalDate> specialChooseList) {
        this.specialChooseList = multipleChooseList;
        refreshCalendarUi();
    }
    public void clearSpecialChooseList(){
        if (specialChooseList!=null){
            specialChooseList.clear();
            refreshCalendarUi();
        }
    }
    public void addSpecialChoose(LocalDate localDate){
        if (specialChooseList==null){
            specialChooseList=new ArrayList<>();
        }
            specialChooseList.add(localDate);
            refreshCalendarUi();

    }


    public void setDefaultHeaderWeekendTextColor(int defaultHeaderWeekendTextColor) {
        this.defaultHeaderWeekendTextColor = defaultHeaderWeekendTextColor;
        refreshDefaultHeaderUi();
    }

    public void setDefaultHeaderTextColor(int defaultHeaderTextColor) {
        this.defaultHeaderTextColor = defaultHeaderTextColor;
        refreshDefaultHeaderUi();
    }

    public void setDefaultHeaderTextSize(int defaultHeaderTextSize) {
        this.defaultHeaderTextSize = defaultHeaderTextSize;
        refreshDefaultHeaderUi();
    }

    public View getHeaderView() {
        return headerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public RecyclerView getRvCalendar() {
        return rvCalendar;
    }

    public void setRvCalendar(RecyclerView rvCalendar) {
        this.rvCalendar = rvCalendar;
    }

    public int getCalendarTextColor() {
        return calendarTextColor;
    }

    public void setCalendarTextColor(int calendarTextColor) {
        this.calendarTextColor = calendarTextColor;
        refreshCalendarUi();
    }

    public int getCalendarTextSize() {
        return calendarTextSize;
    }

    public void setCalendarTextSize(int calendarTextSize) {
        this.calendarTextSize = calendarTextSize;
        refreshCalendarUi();
    }


    public boolean isShowChoose() {
        return showChooseUi;
    }

    public void setShowChoose(boolean showChoose) {
        if (this.showChooseUi == showChoose) {
            return;
        }
        this.showChooseUi = showChoose;
        refreshCalendarUi();
    }


    public void setChooseBackGround(Drawable chooseBackGround) {
        this.chooseBackGround = chooseBackGround;
        refreshCalendarUi();

    }


    public void setChooseTextColor(int chooseTextColor) {
        this.chooseTextColor = chooseTextColor;
        refreshCalendarUi();
    }

    public boolean isShowToday() {
        return showTodayUi;
    }

    public void setShowToday(boolean showToday) {
        if (this.showTodayUi == showToday) {
            return;
        }
        this.showTodayUi = showToday;
        refreshCalendarUi();
    }

    public boolean isShowSpecialUi() {
        return showSpecialUi;
    }

    public void setShowSpecialUi(boolean showSpecialUi) {
        this.showSpecialUi = showSpecialUi;
        refreshCalendarUi();
    }

    public Drawable getSpecialBackGround() {
        return specialBackGround;
    }

    public void setSpecialBackGround(Drawable specialBackGround) {
        this.specialBackGround = specialBackGround;
        refreshCalendarUi();
    }

    public int getSpecialTextColor() {
        return specialTextColor;
    }

    public void setSpecialTextColor(int specialTextColor) {
        this.specialTextColor = specialTextColor;
        refreshCalendarUi();
    }

    public Drawable getTodayBackGround() {
        return todayBackGround;
    }

    public void setTodayBackGround(Drawable todayBackGround) {
        this.todayBackGround = todayBackGround;
        refreshCalendarUi();
    }


    public void setTodayTextColor(int todayTextColor) {
        this.todayTextColor = todayTextColor;
        refreshCalendarUi();
    }

    public boolean isShowUnavailable() {
        return showUnavailableUi;
    }

    public void setShowUnavailable(boolean showUnavailable) {
        if (this.showUnavailableUi == showUnavailable) {
            return;
        }
        this.showUnavailableUi = showUnavailable;
        refreshCalendarUi();
    }


    public void setUnavailableBackGround(Drawable unavailableBackGround) {
        this.unavailableBackGround = unavailableBackGround;
        refreshCalendarUi();
    }



    public void setUnavailableTextColor(int unavailableTextColor) {
        this.unavailableTextColor = unavailableTextColor;
        refreshCalendarUi();
    }

    public boolean isShowNotCurMonthDate() {
        return showNotCurMonthDate;
    }

    public void setShowNotCurMonthDate(boolean showNotCurMonthDate) {
        this.showNotCurMonthDate = showNotCurMonthDate;
        refreshCalendarUi();
    }

    public boolean isShowNotCurMonthUi() {
        return showNotCurMonthUi;
    }

    public void setShowNotCurMonthUi(boolean showNotCurMonthUi) {
        this.showNotCurMonthUi = showNotCurMonthUi;
        refreshCalendarUi();
    }

    public boolean isClickNotCurMonthUi() {
        return clickNotCurMonthUi;
    }

    public void setClickNotCurMonthUi(boolean clickNotCurMonthUi) {
        this.clickNotCurMonthUi = clickNotCurMonthUi;
        refreshCalendarUi();
    }

    public Drawable getNotCurMonthBackGround() {
        return NotCurMonthBackGround;
    }

    public void setNotCurMonthBackGround(Drawable notCurMonthBackGround) {
        NotCurMonthBackGround = notCurMonthBackGround;
        refreshCalendarUi();
    }

    public int getNotCurMonthTextColor() {
        return NotCurMonthTextColor;
    }

    public void setNotCurMonthTextColor(int notCurMonthTextColor) {
        NotCurMonthTextColor = notCurMonthTextColor;
        refreshCalendarUi();
    }


}
