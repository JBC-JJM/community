package com.nowcoder.community.entity;

public class Page {
    //请求参数接收
    private int current = 1;  //当前页面
    private int limit = 10;   //一页面的数据量
    //响应参数
    private int rows;   //总数
    private String path;    //查询的url，当然其实rest风格编程不需要担心这个问题

    /**
     * 当前页的起始行,给sql分页查询使用
     *
     * @return
     */
    public int getOffset() {
        // current * limit - limit
        return (current - 1) * limit;
    }

    /**
     * 总页数,给前端显示页码用
     * @return
     */
    public int getTotal() {
        // rows / limit [+1]
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 起始页码,虽然有点不合理，但是没有框架是这样的了，需要编写getFrom，从而模板可以使用page.from得到值
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }


    /**
     * 结束页码
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        } else System.out.println("current太小了");
        return;
    }

    /**
     * 一页的大小，给sql查询用
     *
     * @return
     */
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (1 <= limit && limit <= 100) {
            this.limit = limit;
        } else System.out.println("limit太小了或者太大了");
        return;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
