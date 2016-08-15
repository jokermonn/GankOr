package com.joker.gankor.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joker on 2016/8/14.
 */
public class ZhihuDetails {
    /**
     * body : <div class="main-wrap content-wrap"> <div class="headline"> <div
     * class="img-place-holder"></div> </div> <div class="content-inner"> <div class="question"> <h2
     * class="question-title">考古出土的人类遗骨，在鉴定之后会如何安置？</h2> <div class="answer"> <div class="meta"> <img
     * class="avatar" src="http://pic3.zhimg.com/2af1d69f9893d2187ef3e1bfce989eba_is.jpg"> <span
     * class="author">青箬，</span><span class="bio">不停挖坑的少年。三观特别正。</span> </div> <div class="content">
     *     <p>没有亲手挖出来过人类遗骨，但是摸过看过磨过。</p> <p>以前有一位老师说过，一般没有特别大研究价值的会再找地方掩埋。</p> <p>尸骨遗骸这一类&hellip;&hellip;
     *     其实并不像大家想的那么常见，有机物在我国大部分地区是很难保存下来的，尤其是酸性土壤地区，地下水位高低变化比较频繁的地区。俗话说&ldquo;干千年，湿万年，不干不湿就半年&rdquo;
     *     。遗憾的是，我国大部地区都是季风气候，每年分干湿两季，这些地方的有机物遗存如果不是在防腐、密封特别好的条件下，是非常不容易保留到今天的。而在我国古代，除了王侯将相达官显贵名商巨贾，基本上是没有人有能力给自己修一个符合上述标准的墓。</p> <p>所以，在平民也就是最常见的墓葬中，别说是遗骨，就是棺木一般情况下也都朽没了，能留下几丝遗痕就已经很不容易了。</p> <p>而不被盗扰的大型墓葬，在我国现行政策下，通常是不允许主动发掘的&mdash;&mdash;甭管你要建设什么玩意儿，都得绕道&mdash;&mdash;允许发掘的，一般都是抢救性发掘，比如被盗了呀，要塌了呀这种。这一类型的墓葬因为被扰动、损毁，基本上不会留下太多有机物的痕迹。例如南京上坊孙吴大墓，仅余石棺床。</p> <p>以上这么多其实只是为了论证一个观点：出土遗骨是很少见的。</p> <p>虽然有老师讲过有一些会再次掩埋，但我觉得这种可能性很小。毕竟人类遗骸是有很大研究价值的，绝不会轻易放弃（如果不做任何措施就掩埋的话，很快就会被分解，相当于就是被弃置了。&ldquo;弃置&rdquo;这个词我用的不好，但我不知道应该用什么词）。</p> <p>好的，那么我们继续，既然是这么难得一见的遗存，那一旦发现，必然就是要当作宝贝来研究，把现在的科技水平能达到的研究和测验都做了！然后再好好地保存起来，等到科技进步了继续研究，没错( &acute; ▽ ` )ﾉ就是这么酷炫。</p> <p>不知道到这里还有没有人记得我开头说过我&ldquo;磨过&rdquo;，是的，在考古研究中，有&ldquo;有损分析&rdquo;和&ldquo;无损分析&rdquo;的区别，很多实验中，需要把样品切割、磨薄、磨粉等等，比如做古代人类食谱研究分析，就有一项需要用到人骨粉&hellip;&hellip;然后我就去帮着磨粉了&hellip;&hellip;然后&hellip;&hellip;就没有然后了。用过的实验材料，处理方法大家都知道吧&hellip;&hellip;就那样随着各种液体&hellip;&hellip;送进废液管道&hellip;&hellip;处理掉了。</p> <p>ps.顺便说一句，可能有人说除了季风气候区那我们的新疆甘肃呢？！那我们的楼兰美人小河墓地呢？好吧，我跟你讲～楼兰美人比我们金贵多啦～她是国宝！国宝懂吗？！永生永世住在地价高到我攒八辈子也买不起一个厕所的地方！还有专人二十四小时照顾！控温控湿防尘防火！想要见面得提前预约打报告，然后在多人陪同下才能远远的一睹芳容！就是这么高规格的安置！！！</p> </div> </div> <div class="view-more"><a href="http://www.zhihu.com/question/46578630">查看知乎讨论<span class="js-question-holder"></span></a></div> </div> </div> </div>
     * image_source : Yestone.com 版权图片库
     * title : 考古的时候，不小心挖到古人的遗骨该怎么办？
     * image : http://pic4.zhimg.com/129b34d7b184bdd6313e39610f957bc7.jpg
     * share_url : http://daily.zhihu.com/story/8676596
     * js : []
     * ga_prefix : 081407
     * images : ["http://pic4.zhimg.com/4c20bc1a5d5d937455c45512610befc3.jpg"]
     * type : 0
     * id : 8676596
     * css : ["http://news-at.zhihu.com/css/news_qa.auto.css?v=4b3e3"]
     */

    private String body;
    @SerializedName("image_source")
    private String imageSource;
    private String title;
    private String image;
    @SerializedName("share_url")
    private String shareUrl;
    @SerializedName("ga_prefix")
    private String gaPrefix;
    private int type;
    private int id;
    private List<String> js;
    private List<String> images;
    private List<String> css;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getGaPrefix() {
        return gaPrefix;
    }

    public void setGaPrefix(String gaPrefix) {
        this.gaPrefix = gaPrefix;
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

    public List<String> getJs() {
        return js;
    }

    public void setJs(List<String> js) {
        this.js = js;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getCss() {
        return css;
    }

    public void setCss(List<String> css) {
        this.css = css;
    }
}
