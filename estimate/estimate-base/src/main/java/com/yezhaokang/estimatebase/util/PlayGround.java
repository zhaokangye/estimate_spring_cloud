package com.yezhaokang.estimatebase.util;

import java.util.*;

public class PlayGround {

    public static String RESOURCE1="resource1";
    public static String RESOURCE2="resource2";

    public static String longestPrefix(String[] strs){
        // 对数组排序
        Arrays.sort(strs);
        // 对第一个和最后一个逐字比较
        String first=strs[0];
        String last=strs[strs.length-1];
        // 共同前缀的字符数量
        int index=0;
        for(int i=0;i<first.length();i++){
            if(first.charAt(i)==last.charAt(i)){
                index++;
            }else {
                break;
            }
        }
        return first.substring(0,index);
    }

    public static int longestPalindrome(String s){
        // 字符串转数组
        char[] chars=s.toCharArray();
        // 初始化set
        HashSet set=new HashSet();
        // 长度
        int count=0;
        for(char letter:chars){
            if(set.contains(letter)){
                count++;
                set.remove(letter);
            }else{
                set.add(letter);
            }
        }
        count*=2;
        if(set.size()!=0){
            count++;
        }
        return count;
    }

    public static boolean isPalindrome(String s){
        char[] chars=s.toCharArray();
        ArrayList list=new ArrayList();
        for(char letter:chars){
            if(Character.isLetterOrDigit(letter)){
                list.add(letter);
            }
        }
        int mid=(list.size()/2)-1;
        StringBuilder prefix=new StringBuilder();
        StringBuilder suffix=new StringBuilder();
        if(list.size()%2==0){
            // 偶数
            for(int i=0;i<=mid;i++){
                prefix.append(list.get(i));
            }
            for(int j=mid+1;j<=list.size()-1;j++){
                suffix.append(list.get(j));
            }
        }else{
            // 奇数
            for(int i=0;i<=mid;i++){
                prefix.append(list.get(i));
            }
            for(int j=mid+2;j<=list.size()-1;j++){
                suffix.append(list.get(j));
            }
        }
        suffix=suffix.reverse();
        if(prefix.toString().toLowerCase().equals(suffix.toString().toLowerCase())){
            return true;
        }else{
            return false;
        }
    }

    public static void main(String[] args) {
//        new Thread(()->{
//            synchronized(RESOURCE1){
//                System.out.println(Thread.currentThread()+RESOURCE1);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                synchronized (RESOURCE2){
//                    System.out.println(Thread.currentThread()+RESOURCE2);
//                }
//            }
//        }).start();
//        new Thread(()->{
//            synchronized (RESOURCE2){
//                System.out.println(Thread.currentThread()+RESOURCE2);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                synchronized (RESOURCE1){
//                    System.out.println(Thread.currentThread()+RESOURCE1);
//                }
//            }
//        }).start();

//        String[] strs={"flower","flow","flight"};
//        System.out.println(longestPrefix(strs));

//        System.out.println(isPalindrome("1234567a@$@$!b!#@#!a7654321"));

    }
}
