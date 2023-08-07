package com.blossom.common.db;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 新增字段时, 生成相关代码块
 *
 * @author xzzz
 */
public class AddColumnUtil {

    private static AddColumnUtil.Field[] column = new AddColumnUtil.Field[]{
            new AddColumnUtil.Field("open_state", "是否开放", Integer.class),
    };

    public static void main(String[] args) {
        genFiled();
        genColumn();
        genSelectWhere();
        genUpdateSet();
    }

    private static void genFiled() {
        System.out.println("\n\n========= field ======================================================\n");
        for (AddColumnUtil.Field f : column) {
            System.out.println(String.format("/** %s */", f.getComment()));
            System.out.println(String.format("private %s %s;", f.getClazz().getSimpleName(), StrUtil.toCamelCase(f.getFiled())));
        }
    }

    private static void genColumn() {
        System.out.println("\n\n========= field ======================================================\n");
        for (AddColumnUtil.Field f : column) {
            System.out.println(String.format("%s,", f.getFiled()));
        }
    }

    private static void genUpdateSet() {
        System.out.println("\n\n========= UPDATE SET ======================================================\n");
        for (AddColumnUtil.Field f : column) {
            if (f.getClazz() == String.class) {
                System.out.println(String.format("<if test=\"%s != null and %s != ''\">%s = #{%s},</if>",
                        StrUtil.toCamelCase(f.getFiled()),
                        StrUtil.toCamelCase(f.getFiled()),
                        f.getFiled(),
                        StrUtil.toCamelCase(f.getFiled())
                ));
            } else {
                System.out.println(String.format("<if test=\"%s != null\">%s = #{%s},</if>", StrUtil.toCamelCase(f.getFiled()), f.getFiled(), StrUtil.toCamelCase(f.getFiled())));
            }
        }
    }

    private static void genSelectWhere() {
        System.out.println("\n\n========= SELECT WHERE ======================================================\n");
        for (AddColumnUtil.Field f : column) {
            if (f.getClazz() == String.class) {
                System.out.println(String.format("<if test=\"%s != null and %s != ''\">and %s = #{%s}</if>",
                        StrUtil.toCamelCase(f.getFiled()),
                        StrUtil.toCamelCase(f.getFiled()),
                        f.getFiled(),
                        StrUtil.toCamelCase(f.getFiled())
                ));
            } else {
                System.out.println(String.format("<if test=\"%s != null\">and %s = #{%s}</if>",
                        StrUtil.toCamelCase(f.getFiled()),
                        f.getFiled(),
                        StrUtil.toCamelCase(f.getFiled())
                ));
            }
        }
    }

    @Data
    private static class Field {
        String filed;
        String comment;
        Class<?> clazz;

        public Field(String filed) {
            this.filed = filed;
            this.clazz = String.class;
        }

        public Field(String filed, String comment, Class<?> clazz) {
            this.filed = filed;
            this.comment = comment;
            this.clazz = clazz;
        }
    }
}
