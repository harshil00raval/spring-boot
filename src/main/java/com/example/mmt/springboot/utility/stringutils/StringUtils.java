package com.example.mmt.springboot.utility.stringutils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringUtils {

    /*
     * generates all possible combination of size 2
     * */
    public static List<String[]> combination(Set<String> setOfStrings){
        String[] listOfStrings = setOfStrings.toArray(new String[setOfStrings.size()]);
        return combinationUtil(listOfStrings);
    }
    private static List<String[]> combinationUtil(String arr[])
    {
        List<String[]> combinationList = new ArrayList<>();
        for(String str1 : arr){
            for(String str2 : arr){
                if(!str1.equalsIgnoreCase(str2)){
                    String data[]= {str1, str2};
                    combinationList.add(data);
                }
            }
        }
        return combinationList;
    }
}
