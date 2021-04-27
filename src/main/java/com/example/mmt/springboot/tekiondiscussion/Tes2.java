package com.example.mmt.springboot.tekiondiscussion;

import java.util.Arrays;

/*
 * we are given an unsorted array size = n all elements>0
 * find : total no of elements for which
 * there should be atleast 1 number which is smaller on left and right side
 * A = {1,2,3,4,5,4,3}
 * z = {0,0,0,1,1,1,0}
 * op = 3
 *
 * l = {1,1,1,1,1,1,1}
 * r = {1,2,3,3,3,3,3}
 *
 * ls = (1,0)
 * rs
 * count = 3
 *
 *
 * for i=1 : i < n-1
 *
 * */
public class Tes2 {
    public static void main(String[] args) {

        int A[] = {3,9,6,4,5,4,3};

        System.out.println(countSpecialElements(A));

    }


    public static int countSpecialElements(int A[]){
        int l[] = new int[A.length];
        int r[] = new int[A.length];

        l[0] = A[0];
        r[A.length-1] = A[A.length-1];
        int leftMin = l[0];
        int rightMin = r[A.length-1];

        for(int i= 1 ; i< A.length ; ++i){
            if(A[i]<leftMin) {
                leftMin = A[i];
            }
            l[i] = leftMin;
        }

        for(int i = A.length-2 ; i>=0 ; --i){
            if(A[i]<rightMin) {
                rightMin = A[i];
            }
            r[i] = rightMin;

        }
        int minCount = 0;
        for(int i=1;i<A.length-1; ++i){
            if(l[i]<A[i] && r[i]<A[i]){
                ++minCount;
            }
        }

        return minCount;
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
