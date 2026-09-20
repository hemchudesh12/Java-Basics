package prep;

public class ExcepHand {

	static void checkAge(int age) throws Exception {

        if (age < 18) {
            throw new Exception("Not eligible");
        }

        System.out.println("Eligible");
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			int a=5/1;
			try {
				int arr[]= {1,2,3};
				arr[5]=5;
			}
			catch(ArrayIndexOutOfBoundsException e) {
				System.out.println("ArrayIndexOutOfBounds : "+e);
				try {
					String str=null;
					int n=str.length();
				}
				catch (NullPointerException obj){
					System.out.println("Nested Catch : "+obj);
				}
			}
			int n=2;
		}
		catch(ArithmeticException e) {
			System.out.println("Exception : "+e);
		}
		catch (Exception e) {
			System.out.println("Final Exception : "+e);
		}
		finally {
			System.out.println("Finally block executed");
		}
		try {
			checkAge(15);
		}
		catch(Exception e){
			System.out.println(e);
		}
		System.out.println();

	}

}
