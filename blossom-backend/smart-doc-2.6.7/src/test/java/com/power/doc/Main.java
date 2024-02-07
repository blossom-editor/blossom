package com.power.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private final static Set<String> PREFIX_LIST = new HashSet<>();


    static {
        PREFIX_LIST.add("maven");
        PREFIX_LIST.add("asm");
        PREFIX_LIST.add("tomcat");
        PREFIX_LIST.add("jboss");
        PREFIX_LIST.add("undertow");
        PREFIX_LIST.add("jackson");
        PREFIX_LIST.add("micrometer");
        PREFIX_LIST.add("spring-boot-actuator");
        PREFIX_LIST.add("sharding");
        PREFIX_LIST.add("mybatis-spring-boot-starter");
        PREFIX_LIST.add("flexmark");
    }

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        //结果集
        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        //定长10线程池
        ExecutorService exs = Executors.newFixedThreadPool(10);
        final List<Integer> taskList = Arrays.asList(2, 1, 3, 4, 5, 6, 7, 8, 9, 10);
        try {
            CompletableFuture[] cfs = taskList.stream().map(object -> CompletableFuture.supplyAsync(() -> calc(object), exs)
                .thenApply(h -> Integer.toString(h))
                //如需获取任务完成先后顺序，此处代码即可
                .whenComplete((v, e) -> {
                    System.out.println("任务" + v + "完成!result=" + v + "，异常 e=" + e + "," + new Date());
                    list2.add(v);
                })).toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(cfs).join();
            System.out.println("任务完成先后顺序，结果list2=" + list2 + "；任务提交顺序，结果list=" + list + ",耗时=" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exs.shutdown();
        }
    }

    public static Integer calc(Integer i) {
        try {
            if (i == 1) {
                //任务1耗时3秒
                Thread.sleep(3000);
            } else if (i == 5) {
                //任务5耗时5秒
                Thread.sleep(5000);
            } else {
                //其它任务耗时1秒
                Thread.sleep(1000);
            }
            System.out.println("task线程：" + Thread.currentThread().getName() + "任务i=" + i + ",完成！+" + new Date());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static boolean ignoreArtifactById(String artifactId) {
        if (PREFIX_LIST.stream().anyMatch(artifactId::startsWith)) {
            return true;
        }
        return false;
    }
}
