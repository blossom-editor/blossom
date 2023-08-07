package com.blossom.common.iaas.oss;

import lombok.Data;

/**
 * 水印参数
 *
 * @author xzzz
 */
@Data
public class WaterMarkParam {

    /** 透明度, 如果是图片水印，就是让图片变得透明，如果是文字水印，就是让水印变透明。
     默认值：100， 表示 100%（不透明）
     取值范围: [0-100] */
    private String transparency;

    /**
     * 位置，水印打在图的位置
     * 取值范围：[nw（左上）,north（中上）,ne（右上）,west（左中）,center（中间）,east（右中）,sw（左下）,south（中下）,se（右下）]
     */
    private String position;

    /**
     * 水平边距, 就是距离图片边缘的水平距离， 这个参数只有当水印位置是左上，左中，左下， 右上，右中，右下才有意义。
     * 默认值：10
     * 取值范围：[0 – 4096]
     * 单位：像素（px）
     */
    private Integer x;

    /**
     * 垂直边距, 就是距离图片边缘的垂直距离， 这个参数只有当水印位置是左上，中上， 右上，左下，中下，右下才有意义
     * 默认值：10
     * 取值范围：[0 – 4096]
     * 单位：像素(px)
     */
    private Integer y;

    /**
     * 中线垂直偏移，当水印位置在左中，中部，右中时，可以指定水印位置根据中线往上或者往下偏移
     * 默认值：0
     * 取值范围：[-1000, 1000]
     * 单位：像素(px)
     */
    private Integer voffset;
}
