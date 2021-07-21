package com.hj.nio.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 9000));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef"));
        System.in.read();
//        User user = new User();
//        user.setId(String.valueOf(1));
//        user.setName("zhangsan");
//        Map<String, Object> dataMap = new LinkedHashMap<>();
//        dataMap.put("age", "12");
//        dataMap.put("city", "杭州");
//        Map<String, Object> map = new HashMap<>();
//        map.put("0", dataMap);
//        user.setMap(map);
//
//        User user1 = new User();
//        user1.setId(String.valueOf(1));
//        user1.setName("lisi");
//        Map<String, Object> dataMap1 = new LinkedHashMap<>();
//        dataMap1.put("age", "20");
//        dataMap1.put("city", "武汉");
//        Map<String, Object> map1 = new HashMap<>();
//        map1.put("0", dataMap1);
//        user1.setMap(map1);
//        List<User> list = new LinkedList<>();
//        list.add(user);
//        list.add(user1);
//        List<Object> collect = list.stream().flatMap(user2 -> {
//            Map<String, Object> dataMap2 = (Map<String, Object>) user2.getMap();
//            return dataMap2.values().stream();
//        }).collect(Collectors.toList());
//        Map<String, Object> map2 = new HashMap<>();
//        for (int i = 0; i < collect.size(); i++) {
//            map2.put(String.valueOf(i), collect.get(i));
//        }
//        map2.entrySet().stream().forEach(System.out::println);
    }
}
