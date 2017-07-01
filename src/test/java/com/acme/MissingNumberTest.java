package com.acme;

/**
 * Created by kobzev on 30.06.17.
 */
public class MissingNumberTest {

	//XOR of all numbers from 1 to n
	// n%4 == 0 ---> n
	// n%4 == 1 ---> 1
	// n%4 == 2 ---> n + 1
	// n%4 == 3 ---> 0
	public static void main(String[] args){
		int [] a = new int[]{ 0, 1, 2, 3, 10, 11, 12, 13 , 14, 15};
//		int n = a.length-1;
//		System.out.println(n);
//		System.out.println(a.length);
		System.out.println(a[4]);
//		System.out.println(a[a.length]);

		//Missing number
//		int miss = getMissingNo(a,a.length);
		int miss = findFirstMissing(a, 0, 15);
		System.out.println("missing="+miss);
	}

	// Function to ind missing number
	static int getMissingNo (int a[], int n)
	{
		int i, total;
		total  = (n+1)*(n+2)/2;
		for ( i = 0; i< n; i++)
			total -= a[i];
		return total;
	}

	static int getMissingNoXor (int a[], int n){
		int xor = 0;
		for(int i=0; i<n; i++) {
			if (a[i] != 0)
				xor ^= a[i];
			xor ^= (i + 1);
		}
		return xor;
	}

	static int findFirstMissing(int array[], int start, int end)
	{
		if (start > end)
			return end + 1;

		if (start != array[start])
			return start;

		int mid = (start + end) / 2;

		// Left half has all elements from 0 to mid
		if (array[mid] == mid)
			return findFirstMissing(array, mid+1, end);

		return findFirstMissing(array, start, mid);
	}
}
