package com.concurrent.tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapExample {

    public static void main(String[] args) {
        Map<String, Long> map1 = new ConcurrentHashMap<>();
        // computeIfAbsent，如果key不存在，就把mappingFunction返回的值存入，并且返回当前的值，如果key存在就不执行mappingFunction
        // 并且返回当前的值
        Long resNum1 = map1.computeIfAbsent("key1", k -> {
            System.out.println("k = " + k);
            return 1L;
        });
        System.out.println("resNum1 = " + resNum1);
        Long resNum2 = map1.computeIfAbsent("key1", k -> {
            System.out.println("k = " + k);
            return 10L;
        });
        System.out.println("resNum2 = " + resNum2);
        System.out.println("-------------------------------------------------");

        // 当key存在，执行remappingFunction
        // 当remappingFunction返回值不为null，保存指定key和value的映射关系
        // 当remappingFunction返回值为null，remove指定的key
        // 如果计算value的过程抛出了异常，computeIfPresent方法中会再次抛出，key和其对应的值不会改变
        Long resNum3 = map1.computeIfPresent("key2", (k, ov) -> {
            System.out.println("key = " + k + ", oldValue = " + ov);
            return 400L;
        });
        System.out.println("resNum3 = " + resNum3);
        Long resNum4 = map1.computeIfPresent("key1", (k, ov) -> {
            System.out.println("key = " + k + ", oldValue = " + ov);
            return 400L;
        });
        System.out.println("resNum4 = " + resNum4);
        System.out.println("-------------------------------------------------");

        // 当remappingFunction返回值不为null时，就建立映射关系，返回存入的新value值
        // 当remappingFunction返回值为null时，如果key存在，则删除之前的映射关系，返回null，如果key不存在，直接返回null
        Long resNum5 = map1.compute("key3", (k, v) -> {
            System.out.println("key = " + k + ", value = " + v);
            return 666L;
        });
        System.out.println("resNum5 = " + resNum5);
        System.out.println(map1);
        Long resNum6 = map1.compute("key3", (__, ___) -> null);
        System.out.println("resNum6 = " + resNum6);
        System.out.println(map1);
        System.out.println("-------------------------------------------------");

        // 当key不存在时，与传入value建立映射关系
        Long resNum7 = map1.putIfAbsent("key4", 5555L);
        System.out.println("resNum7 = " + resNum7);
        System.out.println(map1);
        Long resNum8 = map1.putIfAbsent("key4", 4580L);
        System.out.println("resNum8 = " + resNum8);
        System.out.println(map1);
    }

}
