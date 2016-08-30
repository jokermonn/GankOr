package com.joker.gankor.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joker on 2016/8/8.
 */
public class ZhihuHotNews {

    /**
     * news_id : 8649660
     * url : http://news-at.zhihu.com/api/2/news/8649660
     * thumbnail : http://pic4.zhimg.com/67a1d21a65421fe2ab5fcb1ad4ea7087.jpg
     * title : 名字里有「莲」，长得也很像莲，但睡莲真的不是莲
     */

    private List<RecentBean> recent;

    public List<RecentBean> getRecent() {
        return recent;
    }

    public void setRecent(List<RecentBean> recent) {
        this.recent = recent;
    }

    public static class RecentBean {
        @SerializedName("news_id")
        private int newsId;
        private String url;
        private String thumbnail;
        private String title;

        public int getNewsId() {
            return newsId;
        }

        public void setNewsId(int newsId) {
            this.newsId = newsId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
