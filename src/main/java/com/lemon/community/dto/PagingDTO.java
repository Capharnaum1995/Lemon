package com.lemon.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面信息类，包括question数组对象，分页等（本来分别也应该是前端来做的，我们只需要传递给前端question的数量，以及question数组）
 */
@Data
public class PagingDTO<T> {
    private List<T> data;
    private boolean showPrevious;    //是否展示上一页
    private boolean showNext;        //是否展示下一页
    private boolean showFirstPage;   //是否展示第一页
    private boolean showEndPage;     //是否展示尾页
    private Integer pageNow;
    private Integer pageCount;
    private List<Integer> pageList = new ArrayList<>();  //形如["1","2","3","4",...]

    public void initPage(Integer pageCount, Integer pageNow) {
        //这里不要忘记给PagingDTO的pageNow，pageCount属性赋值，不然的话页面接收不到当前是第几页的信息/如何直接跳到尾页
        this.pageNow = pageNow;
        this.pageCount = pageCount;

        //处理页码列表
        pageList.add(this.pageNow);
        for (int i = 1; i <= 3; i++) {
            if ((this.pageNow - i) > 0) {
                this.pageList.add(0, this.pageNow - i);//带参数，头部插入
            }
            if ((this.pageNow + i) <= pageCount) {
                this.pageList.add(this.pageNow + i);
            }
        }

        //当前页面是第1页：不显示上一页按钮。当前页面不是第1页：显示上一页按钮
        if (this.pageNow == 1) {
            this.setShowPrevious(false);
        } else {
            this.setShowPrevious(true);
        }

        //当前页面是最后页：不显示下一页按钮。当前页面不是最后一页：显示下一页按钮
        if (this.pageNow == pageCount) {
            this.setShowNext(false);
        } else {
            this.setShowNext(true);
        }

        //当页码列表中包含第一页时，不展示第一页，否则展示
        if (pageList.contains(1)) {
            this.setShowFirstPage(false);
        } else {
            this.setShowFirstPage(true);
        }

        //当前页码列表中包含尾页时，不展示尾页，否则展示
        if (pageList.contains(pageCount)) {
            this.setShowEndPage(false);
        } else {
            this.setShowEndPage(true);
        }
    }
}
