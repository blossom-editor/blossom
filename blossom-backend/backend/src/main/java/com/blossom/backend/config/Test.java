package com.blossom.backend.config;

import com.blossom.backend.server.folder.pojo.FolderEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Test {
    public static void main(String[] args) {

        Set<FolderEntity> set = new HashSet<>();

        FolderEntity f1 = new FolderEntity();
        f1.setId(1L);
        f1.setName("1");


        FolderEntity f2 = new FolderEntity();
        f2.setId(1L);
        f2.setName("2");

        set.add(f1);
        set.add(f2);

        System.out.println(set.size());
        System.out.println(Arrays.toString(set.toArray()));

    }

}
