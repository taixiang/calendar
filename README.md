>原文链接：[https://mp.weixin.qq.com/s/SmxDiWIidHS2hwVvFcz_hw](https://mp.weixin.qq.com/s/SmxDiWIidHS2hwVvFcz_hw)

项目需要用到日历控件，这是我们的效果图。  
![](https://user-gold-cdn.xitu.io/2018/8/11/165279e215e0e3b4?w=391&h=300&f=jpeg&s=27470)    

去github上搜了一哈，搜到大神写的`CalendarView`，各种炫酷效果，我这种的也只需要自定义效果就可以了，话不多说，直接开撸！   
这里附上github的链接地址：[https://github.com/huanghaibin-dev/CalendarView](https://github.com/huanghaibin-dev/CalendarView)， 里面的api文档说明还是很齐全的，这里就直接记录我的开发历程。  
#### gradle 关联
`implementation 'com.haibin:calendarview:3.4.0' `
#### 使用
刚开始布局中使用的话注意是 `<com.haibin.calendarview.CalendarView />` 有包名路径的，如果直接是 `<CalendarView />` 使用的是系统自带的日历控件。
```
<com.haibin.calendarview.CalendarView
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```
可以直接预览效果，一些属性配置项：   
`app:month_view_show_mode="mode_fix"`  配置月视图的显示模式  
`app:current_month_text_color="#212121"`  当前页月份的月字体颜色  
`app:other_month_text_color="#cccccc"` 当前页其余月份的月字体颜色    
等等一系列的属性配置，文档里都是有的。当然要实现自己的效果，那些属性是不够的，需要我们自定义MonthView 去实现（项目地址里也有demo可下载参考）。  
#### 自定义 MonthView
自定义`MyMonthView` 类继承自 `MonthView`，xml布局里添加属性 `app:month_view="com.calendar.MyMonthView"`，这里的路径是自己实际项目中的monthView的路径，需要我们自己去绘制日历。    
```
//取消日历字体加粗
mCurMonthTextPaint.setFakeBoldText(false);
mOtherMonthTextPaint.setFakeBoldText(false);
```
这里插个题外的知识点tip： `setFakeBoldText(true)`  的加粗效果比 `android:textStyle="bold"` 属性的加粗效果要弱点，就是不会太粗，又比细稍微粗一点的效果~ 了解一下   

在`onDrawText`里进行绘制，正常样式的日历可正常显示时间的
```
@Override
protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
    float baselineY = mTextBaseLine + y;
    int cx = x + mItemWidth / 2;
    int cy = y + mItemHeight / 2;
    canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
}
```
这里要分类下我们需要几种类型的样式：  
![](https://user-gold-cdn.xitu.io/2018/8/9/1651df6342356aa5?w=36&h=29&f=png&s=801) 1、不可完成的   

![](https://user-gold-cdn.xitu.io/2018/8/9/1651df644c4e88ea?w=32&h=31&f=png&s=1319) 2、可以完成的  

![](https://user-gold-cdn.xitu.io/2018/8/9/1651df6537088a64?w=37&h=34&f=png&s=996) 3、今日已完成的  

![](https://user-gold-cdn.xitu.io/2018/8/9/1651df3a53b570cb?w=40&h=28&f=png&s=640) 4、历史已完成的   

模拟数据，通过scheme 标记区分各样式
```
Calendar calendar1 = getSchemeCalendar(2018, 8, 11, "1");
Calendar calendar2 = getSchemeCalendar(2018, 8, 12, "2");
Calendar calendar3 = getSchemeCalendar(2018, 8, 13, "3");
Calendar calendar4 = getSchemeCalendar(2018, 8, 6, "4");

map.put(calendar1.toString(), calendar1);
map.put(calendar2.toString(), calendar2);
map.put(calendar3.toString(), calendar3);
map.put(calendar4.toString(), calendar4);
calendarView.setSchemeDate(map);

private Calendar getSchemeCalendar(int year, int month, int day, String text) {
    Calendar calendar = new Calendar();
    calendar.setYear(year);
    calendar.setMonth(month);
    calendar.setDay(day);
    calendar.setScheme(text);
    return calendar;
}
```
初始化两个paint，有两张图片资源的用Bitmap去绘制
```
paint1.setColor(ContextCompat.getColor(context, R.color.green));
paint1.setTextSize(DensityUtil.spToPx(context, 13));
paint1.setStyle(Paint.Style.STROKE);
paint1.setAntiAlias(true);
paint1.setTextAlign(Paint.Align.CENTER);

paint2.setColor(ContextCompat.getColor(context, R.color.white));
paint2.setTextSize(DensityUtil.spToPx(context, 13));
paint2.setAntiAlias(true);
paint2.setTextAlign(Paint.Align.CENTER);

dayBgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.day_bg);
daySuccessBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.day_success);
```
主要的方法 onDrawText ,根据不同scheme 绘制各view
```

@Override
protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
    //这里的x、y 是每日的起点坐标
    float baselineY = mTextBaseLine + y;
    int cx = x + mItemWidth / 2;
    int cy = y + mItemHeight / 2;
    if ("1".equals(calendar.getScheme())) {
        // 不可完成的，绘制圆
        paint1.setStrokeWidth(0);
        canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, paint1);
        paint1.setStrokeWidth(DensityUtil.dpToPx(context, 1));
        canvas.drawCircle(cx, cy + 3, mItemWidth / 4 - 9, paint1);
    } else if ("2".equals(calendar.getScheme())) {
        //可以完成的，绘制背景图
        canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, paint2);
        canvas.drawBitmap(dayBgBitmap, x + mItemWidth / 4 - 5, y + mItemHeight / 4 + 8, paint2);
    } else if ("3".equals(calendar.getScheme())) {
        //今日已完成的，绘制圆+打勾图片
        paint1.setStrokeWidth(0);
        canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, paint1);
        paint1.setStrokeWidth(DensityUtil.dpToPx(context, 1));
        canvas.drawCircle(cx, cy + 3, mItemWidth / 4 - 9, paint1);
        canvas.drawBitmap(daySuccessBitmap, x + mItemWidth * 3 / 4 - 18, y + mItemHeight * 3 / 4 - 24, paint1);
    } else if ("4".equals(calendar.getScheme())) {
        //历史已完成的，绘制打勾图片
        paint1.setStrokeWidth(0);
        canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, paint1);
        canvas.drawBitmap(daySuccessBitmap, x + mItemWidth * 3 / 4 - 18, y + mItemHeight * 3 / 4 - 40, paint1);
    } else {
        //正常日期的显示
        canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
    }
}
```

至此，视图绘制完成。   

![](https://user-gold-cdn.xitu.io/2018/8/11/16527a6f54df0ad7?w=302&h=300&f=jpeg&s=25791)  
接下来完成基本的api调用。   
#### api 调用
初始化赋值当前的年月，日历切换时，时间对应改变。
```
//初始化当前年月
tvMonth.setText(calendarView.getCurYear() + "年" + calendarView.getCurMonth() + "月");
//月份切换改变事件
calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
    @Override
    public void onMonthChange(int year, int month) {
        tvMonth.setText(year + "年" + month + "月");
    }
});
```

布局里有个时间选择器，用于选择年月的，这里采用的是 Android-PickerView 时间选择器。
```
//时间选择器选择年月，对应的日历切换到指定日期
picker.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        TimePickerView pvTime = new TimePickerBuilder(MainActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.setTime(date);
                int year = c.get(java.util.Calendar.YEAR);
                int month = c.get(java.util.Calendar.MONTH);
                //滚动到指定日期
                calendarView.scrollToCalendar(year, month + 1, 1);
            }
        }).setType(type).build();
        pvTime.show();
    }
});
```
日期的选择监听事件
```
calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
    @Override
    public void onDateSelected(Calendar calendar, boolean isClick) {

    }
});
```
效果完成图，当然不同项目里的效果图是不一样的，只要会canvas的基本绘制都是可以达到各自想要的效果的。  

![](https://user-gold-cdn.xitu.io/2018/8/11/16527b539cb84fdb?w=318&h=469&f=gif&s=353147)   


详细代码见  
github地址：[https://github.com/taixiang/calendar](https://github.com/taixiang/calendar)  

欢迎关注我的博客：[https://blog.manjiexiang.cn/](https://blog.manjiexiang.cn/)  
更多精彩欢迎关注微信号：春风十里不如认识你    

![](https://user-gold-cdn.xitu.io/2018/8/12/1652cd77eaebeb98?w=900&h=540&f=jpeg&s=64949)    


有个「佛系码农圈」，欢迎大家加入畅聊，开心就好！  

![](https://user-gold-cdn.xitu.io/2018/8/12/1652cd9eed546cde?w=188&h=250&f=jpeg&s=40850)   

过期了，可加我微信 tx467220125 拉你入群。
