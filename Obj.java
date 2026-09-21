package prep;
class Student{
	String name;
	int age;
	void display() {
		System.out.println("Student  name : "+name);
		System.out.println("Student  age : "+age);
	}
}
class Student2{
	String name;
	int age;
	Student2(String name , int age){
		this.name=name;
		this.age=age;
	}
	void display() {
		System.out.println("Student  name : "+name);
		System.out.println("Student  age : "+age);
	}
}
public class Obj {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Student s1 = new Student();
		Student s2 = new Student();
		s1.name="Hem";
		s1.age=20;
		s2.name="Ram";
		s2.age=16;
		System.out.println("Student 1 name : "+s1.name);
		System.out.println("Student 1 age : "+s1.age);
		System.out.println("Student 2 name : "+s2.name);
		System.out.println("Student 2 age : "+s2.age);
		s1.display();
		s2.display();
		Student2 stu1 = new Student2("Hemchudesh",20);
		stu1.display();
	}

}
