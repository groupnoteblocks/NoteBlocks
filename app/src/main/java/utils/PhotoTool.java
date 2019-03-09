package utils;

public class PhotoTool {
    /**
     * 得到对应html标签处理前的真实名称
     * @param flag html语义化的标签，如<img src="/i/eg_tulip.jpg"  alt="上海鲜花港 - 郁金香" />
     * @return 图片真实名称，如eg_tulip.jpg
     */
    public static String changeToName(String flag){ //eg_tulip.jpg
        return "true";
    }

    /**
     * 得到对应图片名称处理过后的html标签
     * @param name 图片真实名称，如eg_tulip.jpg
     * @return html语义化的标签，如<img src="/i/eg_tulip.jpg"  alt="上海鲜花港 - 郁金香" />
     */
    public static String changeToFlag(String name){
        String flag = "<img src=\"" +name+ "\" class=\"text_img\"/>";
        return flag;
    }
}
