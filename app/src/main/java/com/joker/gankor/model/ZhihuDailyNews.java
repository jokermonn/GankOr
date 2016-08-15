package com.joker.gankor.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joker on 2016/8/8.
 */
public class ZhihuDailyNews {

    /**
     * date : 20160808
     * stories : [{"images":["http://pic1.zhimg.com/c27847ba9ac7c11ce195bc5155357bf4.jpg"],"type":0,
     * "id":8656865,"ga_prefix":"080809","title":"从经济学角度来看，「走一步看一步」是不是能达到最优？"},{"images":["http://pic1
     * .zhimg.com/e75c9b74b748ada732ef5301b2c6b518.jpg"],"type":0,"id":8660318,"ga_prefix":"080808",
     * "title":"如果身体里有一个细胞癌变了，就一定会发展成癌症吗？"},{"images":["http://pic2.zhimg
     * .com/497ecbb84920b655151a1142b63b9675.jpg"],"type":0,"id":8660020,"ga_prefix":"080807",
     * "title":"理想中的商业模式，应该是什么样的？"},{"title":"英文阅读中，有哪些值得注意的文化背景知识？","ga_prefix":"080807",
     * "images":["http://pic3.zhimg.com/c551af07c0ecfec4c8137fa1660bd206.jpg"],"multipic":true,"type":0,
     * "id":8660423},{"images":["http://pic3.zhimg.com/41e17842513870249afa6747d192fa26.jpg"],"type":0,
     * "id":8658498,"ga_prefix":"080807","title":"印度人口多，经济增长快，为什么奥运会上表现不太好？"},{"images":["http://pic4
     * .zhimg.com/f84dc12935112fe36dc8d0a2db71a91f.jpg"],"type":0,"id":8660448,"ga_prefix":"080807",
     * "title":"读读日报 24 小时热门 TOP 5 · 地铁公交上班族如何读书？"},{"images":["http://pic3.zhimg
     * .com/29aa1a049b2408bbf4211be4e49c1a7a.jpg"],"type":0,"id":8658968,"ga_prefix":"080806","title":"瞎扯
     * · 如何正确地吐槽"}]
     * top_stories : [{"image":"http://pic1.zhimg.com/988f127baf4dd0885e54994e5c2d8a08.jpg","type":0,
     * "id":8660448,"ga_prefix":"080807","title":"读读日报 24 小时热门 TOP 5 · 地铁公交上班族如何读书？"},
     * {"image":"http://pic2.zhimg.com/782daacef0bc8fb35c00afc45f6d8145.jpg","type":0,"id":8660318,
     * "ga_prefix":"080808","title":"如果身体里有一个细胞癌变了，就一定会发展成癌症吗？"},{"image":"http://pic4.zhimg
     * .com/cc0f6aa9ff5b1dd1f76c9d6abb9d2b63.jpg","type":0,"id":8658538,"ga_prefix":"080718",
     * "title":"整点儿奥运 · 自打看了奥运会，心脏一下就强健了"},{"image":"http://pic4.zhimg
     * .com/b2aa14b50213da6d6d2e5c6a96a07d03.jpg","type":0,"id":8659110,"ga_prefix":"080717",
     * "title":"知乎好问题 · 独处的时候，如何保持自律？"},{"image":"http://pic2.zhimg.com/e15f4d8396a1573928fa510e711046e5
     * .jpg","type":0,"id":8652741,"ga_prefix":"080715","title":"《玩具总动员 3》里还有龙猫彩蛋？这可是迪士尼自己说的"}]
     */

    private String date;
    /**
     * images : ["http://pic1.zhimg.com/c27847ba9ac7c11ce195bc5155357bf4.jpg"]
     * type : 0
     * id : 8656865
     * ga_prefix : 080809
     * title : 从经济学角度来看，「走一步看一步」是不是能达到最优？
     */

    private List<StoriesBean> stories;
    /**
     * image : http://pic1.zhimg.com/988f127baf4dd0885e54994e5c2d8a08.jpg
     * type : 0
     * id : 8660448
     * ga_prefix : 080807
     * title : 读读日报 24 小时热门 TOP 5 · 地铁公交上班族如何读书？
     */

    @SerializedName("top_stories")
    private List<TopStoriesBean> topStories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<StoriesBean> getStories() {
        return stories;
    }

    public void setStories(List<StoriesBean> stories) {
        this.stories = stories;
    }

    public List<TopStoriesBean> getTopStories() {
        return topStories;
    }

    public void setTopStories(List<TopStoriesBean> topStories) {
        this.topStories = topStories;
    }

    public static class StoriesBean implements Parcelable {
        private int type;
        private int id;
        private String title;
        private List<String> images;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.type);
            dest.writeInt(this.id);
            dest.writeString(this.title);
            dest.writeStringList(this.images);
        }

        public StoriesBean() {
        }

        protected StoriesBean(Parcel in) {
            this.type = in.readInt();
            this.id = in.readInt();
            this.title = in.readString();
            this.images = in.createStringArrayList();
        }

        public static final Creator<StoriesBean> CREATOR = new Creator<StoriesBean>() {
            @Override
            public StoriesBean createFromParcel(Parcel source) {
                return new StoriesBean(source);
            }

            @Override
            public StoriesBean[] newArray(int size) {
                return new StoriesBean[size];
            }
        };
    }

    public static class TopStoriesBean implements Parcelable {
        private String image;
        private int type;
        private int id;
        private String title;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.image);
            dest.writeInt(this.type);
            dest.writeInt(this.id);
            dest.writeString(this.title);
        }

        public TopStoriesBean() {
        }

        protected TopStoriesBean(Parcel in) {
            this.image = in.readString();
            this.type = in.readInt();
            this.id = in.readInt();
            this.title = in.readString();
        }

        public static final Creator<TopStoriesBean> CREATOR = new Creator<TopStoriesBean>() {
            @Override
            public TopStoriesBean createFromParcel(Parcel source) {
                return new TopStoriesBean(source);
            }

            @Override
            public TopStoriesBean[] newArray(int size) {
                return new TopStoriesBean[size];
            }
        };
    }
}
