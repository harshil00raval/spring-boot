package com.example.mmt.springboot.tekiondiscussion;

import java.util.Arrays;

/*
* we are given an unsorted array size = n all elements>0
* k>0
* k<=n
*
* find k numbers from the array
* diff (max and min )  = minimum
*
* A = {1,3,5,7,11, 12, 13},  n=7  k=3
* {1,3,5}  diff(5,1) = 4
* {11,12,13} diff(11,13) = 2
*
*
* op : 2
* min = INTEGER>MAX
* 135 = 5-1 = 4    , if min>diff   min = 4
* 357 7-3 = 4      min 4
* 71112 12-7 = 4   min 4
* 111213    13-11 = 2  min 2
*  1 11 2 5 7 12 3 4 13
* */
public class Test {
    public static void main(String[] args) {

        int A[] = {11,1,2,7,3,13,15};


        System.out.println(findMinimumDiff(A, 3));

    }

    public static int findMinimumDiff(int A[], int k){
        int minDiff = Integer.MAX_VALUE;
        Arrays.sort(A);

        for(int i=0; i<= A.length-k;++i){
            int d = A[i+k-1] - A[i];
            if(d< minDiff){
                minDiff = d;
            }
        }

        return minDiff;
    }
}
