package prep;
import java.util.*;
class Stu{
	String name;
	int age;
	Stu(String name,int age){
		this.name=name;
		this.age=age;
	}
}
public class CollOfObj {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		List<Stu> s = new ArrayList<>();
		System.out.println("Enter the no. of Students : ");
		int n=sc.nextInt();
		for(int i=0;i<n;i++) {
			s.add(new Stu(sc.next(),sc.nextInt()));
		}
//		s.add(new Stu("A",20));
//		s.add(new Stu("B",23));
		for(int i=0;i<s.size();i++) {
			System.out.println(s.get(i).name+" "+s.get(i).age);
		}
		s.remove(1);
		for(int i=0;i<s.size();i++) {
			System.out.println(s.get(i).name+" "+s.get(i).age);
		}
	}

}
