package com.example.panpan.panpan_android.utils;


public class PagerUtils {
    
    /**
     * 判断当前是否为最后一页
     *
     * @param page
     * @return
     */
    public static boolean isLastPage(PageInfo page) {
        if (page != null) {
            if (getTotalPage(page) == 0) {
                return true;
            }
            if (page.pageNo >= getTotalPage(page)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判断当前是否为第一页
     *
     * @param page
     * @return
     */
    public static boolean isFirstPage(PageInfo page) {
        if (page != null) {
            if (getTotalPage(page) == 0) {
                return true;
            }
            if (page.pageNo == 1) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取下一页码
     *
     * @param page
     * @return
     */
    public static int getNextPageNo(PageInfo page) {
        if (page != null) {
            int currentPage = page.pageNo;
            if (currentPage >= getTotalPage(page)) {
                return getTotalPage(page);
            }
            return ++currentPage;
        }
        return Integer.valueOf(1);
    }
    
    /**
     * 判断是否为空页码
     *
     * @param page
     * @return
     */
    public static boolean isPageEmpty(PageInfo page) {
        if (page != null) {
            if (page.totalCount == 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取全部条目
     *
     * @param page
     * @return
     */
    public static int getTotalCount(PageInfo page) {
        if (page != null) {
            return page.totalCount;
        }
        return 0;
    }
    
    public static int getTotalPage(PageInfo page) {
        if (page.pageSize == 0)
            return 0;
        
        if (page.totalCount % page.pageSize == 0) {
            return page.totalCount / page.pageSize;
        } else {
            return page.totalCount / page.pageSize + 1;
        }
    }
    
    public static class PageInfo {
        public boolean isFirst;
        public int pageNo = 0;
        public int pageSize = 0;
        public int totalCount = 0;
        
        public PageInfo(int pageNo, int pageSize, int totalCount) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.totalCount = totalCount;
        }
    }
}
