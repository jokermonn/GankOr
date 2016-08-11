package com.joker.gankor.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 2016/8/5.
 */
public class GankWelfare implements Parcelable {
    /**
     * error : false
     * results : [{"_id":"57a159ee421aa91e2606476b","createdAt":"2016-08-03T10:41:50.299Z","desc":"8-3",
     * "publishedAt":"2016-08-03T11:12:47.159Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
     * .cn/large/610dc034jw1f6gcxc1t7vj20hs0hsgo1.jpg","used":true,"who":"代码家"},
     * {"_id":"579ff9d0421aa90d39e709be","createdAt":"2016-08-02T09:39:28.23Z","desc":"8.2",
     * "publishedAt":"2016-08-02T11:40:01.363Z","source":"chrome","type":"福利","url":"http://ww4.sinaimg
     * .cn/large/610dc034jw1f6f5ktcyk0j20u011hacg.jpg","used":true,"who":"代码家"},
     * {"_id":"579eb4b4421aa90d2fc94ba0","createdAt":"2016-08-01T10:32:20.10Z","desc":"8.1",
     * "publishedAt":"2016-08-01T12:00:57.45Z","source":"chrome","type":"福利","url":"http://ww1.sinaimg
     * .cn/large/610dc034jw1f6e1f1qmg3j20u00u0djp.jpg","used":true,"who":"代码家"},
     * {"_id":"579ab0a8421aa90d36e960b4","createdAt":"2016-07-29T09:26:00.838Z","desc":"7.29",
     * "publishedAt":"2016-07-29T09:37:39.219Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
     * .cn/large/610dc034jw1f6aipo68yvj20qo0qoaee.jpg","used":true,"who":"代码家"},
     * {"_id":"57995869421aa90d43bbf042","createdAt":"2016-07-28T08:57:13.293Z","desc":"葛优躺",
     * "publishedAt":"2016-07-28T18:17:20.567Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
     * .cn/large/610dc034jw1f69c9e22xjj20u011hjuu.jpg","used":true,"who":"代码家"},
     * {"_id":"57981ee6421aa90d36e96090","createdAt":"2016-07-27T10:39:34.818Z","desc":"王子文",
     * "publishedAt":"2016-07-27T11:27:16.610Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
     * .cn/large/610dc034jw1f689lmaf7qj20u00u00v7.jpg","used":true,"who":"代码家"},
     * {"_id":"5796b970421aa90d2fc94b4e","createdAt":"2016-07-26T09:14:24.76Z","desc":"今天两个妹子",
     * "publishedAt":"2016-07-26T10:30:11.357Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
     * .cn/large/c85e4a5cjw1f671i8gt1rj20vy0vydsz.jpg","used":true,"who":"代码家"},
     * {"_id":"5794df0e421aa90d39e70939","createdAt":"2016-07-24T23:30:22.399Z","desc":"7.25",
     * "publishedAt":"2016-07-25T11:43:57.769Z","source":"chrome","type":"福利","url":"http://ww2.sinaimg
     * .cn/large/610dc034jw1f65f0oqodoj20qo0hntc9.jpg","used":true,"who":"代码家"},
     * {"_id":"57918b5c421aa90d2fc94b35","createdAt":"2016-07-22T10:56:28.274Z","desc":"恐龙爪子萌妹子",
     * "publishedAt":"2016-07-22T11:04:44.305Z","source":"web","type":"福利","url":"http://ww2.sinaimg
     * .cn/large/c85e4a5cgw1f62hzfvzwwj20hs0qogpo.jpg","used":true,"who":"代码家"},
     * {"_id":"578f93c4421aa90de83c1bf4","createdAt":"2016-07-20T23:07:48.480Z","desc":"7.21",
     * "publishedAt":"2016-07-20T16:09:07.721Z","source":"chrome","type":"福利","url":"http://ww4.sinaimg
     * .cn/large/610dc034jw1f60rw11f5mj20iy0sg0u2.jpg","used":true,"who":"daimajia"}]
     */

    private boolean error;
    /**
     * _id : 57a159ee421aa91e2606476b
     * createdAt : 2016-08-03T10:41:50.299Z
     * desc : 8-3
     * publishedAt : 2016-08-03T11:12:47.159Z
     * source : chrome
     * type : 福利
     * url : http://ww3.sinaimg.cn/large/610dc034jw1f6gcxc1t7vj20hs0hsgo1.jpg
     * used : true
     * who : 代码家
     */

    private List<ResultsBean> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        @SerializedName("_id")
        private String id;

        private String createdAt;
        private String desc;
        private String publishedAt;
        private String source;
        private String type;
        private String url;
        private boolean used;
        private String who;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.error ? (byte) 1 : (byte) 0);
        dest.writeList(this.results);
    }

    public GankWelfare() {
    }

    protected GankWelfare(Parcel in) {
        this.error = in.readByte() != 0;
        this.results = new ArrayList<ResultsBean>();
        in.readList(this.results, ResultsBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<GankWelfare> CREATOR = new Parcelable.Creator<GankWelfare>() {
        @Override
        public GankWelfare createFromParcel(Parcel source) {
            return new GankWelfare(source);
        }

        @Override
        public GankWelfare[] newArray(int size) {
            return new GankWelfare[size];
        }
    };
}
