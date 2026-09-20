package prep;
import java.util.*;
public class ArrOp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int arr[] = {1,30,5,2,4};
		System.out.println(arr[0]);//accessing
		arr[1]=3;  //updation
		int len=0;
		//Array Traversing
		for(int n : arr) {
			System.out.print(n+" ");
			len++;
		}
		System.out.println("\nArray Length : "+len);
		
		//find a element
		int target = 3;
		boolean found=false;
		for(int i=0;i<len;i++) {
			if(arr[i]==target) {
				System.out.println("Element "+target+" found at index : "+i);
				found=true;
				break;
			}
		}
		if(!found) System.out.println("Element "+target+" not found");
		int max=Integer.MIN_VALUE, min=Integer.MAX_VALUE,sum=0;
		for(int i=0;i<len;i++) {
			if(arr[i]>max) max=arr[i];
			if(arr[i]<min) min=arr[i];
			sum+=arr[i];
		}
		System.out.println("Maximum Element : "+max);
		System.out.println("Minimum Element : "+min);
		System.out.println("Sum of the Array : "+sum);
		//sorting
		for(int i=0;i<len;i++) {
			for(int j=i+1;j<len;j++) {
				if(arr[i]>arr[j]) {
					arr[i]=arr[i]^arr[j];
					arr[j]=arr[i]^arr[j];
					arr[i]=arr[i]^arr[j];
				}
			}
		}
		System.out.print("Array After Sorting : ");
		for(int n : arr) {
			System.out.print(n+" ");
		}
		System.out.println();
		int left=0,right=arr.length-1;
		while(left<=right) {
			int temp = arr[left];
			arr[left]=arr[right];
			arr[right]=temp;
			left++;
			right--;
		}
		System.out.print("Array After Reversing : ");
		for(int n : arr) {
			System.out.print(n+" ");
		}
		System.out.println();
		List<Integer> li = new ArrayList<>();
		li.add(1);
		li.add(2);
		li.add(30);
		li.add(4);
		li.add(5);
		li.add(6);
		int s = li.size();
		for(int i=0;i<s;i++) {
			System.out.print(li.get(i)+" ");
		}
		System.out.print("\nList Elements : ");
		li.set(2, 3);
		li.remove(5);
		for(int i=0;i<li.size();i++) {
			System.out.print(li.get(i)+" ");
		}
		System.out.println("\n"+li.contains(3));
		System.out.println(li.contains(30));
	}

}
