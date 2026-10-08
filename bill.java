package prep;
import java.util.*;
class Reading {
	String id,time;
	double voltage,usage;
	Reading(String id,String time,double voltage,double usage) {
		this.id=id;
		this.time=time;
		this.voltage=voltage;
		this.usage=usage;
	}
}
class Plug {
	String id;
	double vt,vw,vc;
	double ut,uw;
	double[] va=new double[4];
	double[] ua=new double[120];
	int vi=0,ui=0;
	ArrayList<Double> vData=new ArrayList<>();
	ArrayList<Double> uData=new ArrayList<>();
	double total=0;
	boolean power=true;
	Plug(String id,double vt,double vw,double vc,double ut,double uw) {
		this.id=id;
		this.vt=vt;
		this.vw=vw;
		this.vc=vc;
		this.ut=ut;
		this.uw=uw;
	}
	void add(Reading r) {
		va[vi++]=r.voltage;
		ua[ui++]=r.usage;
		if(vi==4) {
			voltageCheck();
			vi=0;
		}
		if(ui==120) {
			usageCheck();
			ui=0;
		}
	}
	void voltageCheck() {
		double max=va[0];
		for(int i=1;i<4;i++) {
			if(va[i]>max)
				max=va[i];
		}
		vData.add(max);
		double wl=vt-(vt*vw/100);
		double wh=vt+(vt*vw/100);
		double cl=vt-(vt*vc/100);
		double ch=vt+(vt*vc/100);
		System.out.println("\n"+id+" - 2 minute window");
		System.out.println("Max Voltage = "+max);
		System.out.println("Warning Range = "+wl+" - "+wh);
		System.out.println("Critical Range = "+cl+" - "+ch);
		if(max<cl||max>ch) {
			power=false;
			System.out.println("Voltage = CRITICAL");
			System.out.println("Power = OFF");
		}
		else if(max<wl||max>wh) {
			System.out.println("Voltage = WARNING");
		}
		else {
			System.out.println("Voltage = NORMAL");
		}
	}
	void usageCheck() {
		double sum=0;
		for(int i=0;i<120;i++)
			sum+=ua[i];
		double avg=sum/120;
		uData.add(avg);
		total+=sum*0.5/1000;
		double low=ut-(ut*uw/100);
		double high=ut+(ut*uw/100);
		System.out.println("\n"+id+" - 1 hour window");
		System.out.println("Average Usage = "+avg);
		System.out.println("Usage Range = "+low+" - "+high);
		if(avg<low||avg>high)
			System.out.println("Usage = WARNING");
		else
			System.out.println("Usage = NORMAL");
	}
}
public class bill {
	static ArrayList<Plug> plugs=new ArrayList<>();
	static Plug find(String id) {
		for(Plug p:plugs) {
			if(p.id.equals(id))
				return p;
		}
		return null;
	}
	static void usage() {
		double home=0;
		for(Plug p:plugs) {
			System.out.println(p.id+" = "+p.total+" kWh");
			home+=p.total;
		}
		System.out.println("Home Total = "+home+" kWh");
		for(Plug p:plugs) {
			double share=p.total*100/home;
			System.out.println(p.id+" Share = "+share+"%");
		}
	}
	static void sort() {
		for(int i=0;i<plugs.size()-1;i++) {
			for(int j=0;j<plugs.size()-i-1;j++) {
				if(plugs.get(j).total<plugs.get(j+1).total) {
					Plug temp=plugs.get(j);
					plugs.set(j,plugs.get(j+1));
					plugs.set(j+1,temp);
				}
			}
		}
		System.out.println("Consumption Ranking:");
		for(Plug p:plugs)
			System.out.println(p.id+" = "+p.total+" kWh");
		if(!plugs.isEmpty())
			System.out.println("Highest Consumer = "+plugs.get(0).id);
	}
	static void billing(String plan) {
		double units=0;
		for(Plug p:plugs)
			units+=p.total;
		double grossBill=0;
		if(units<=100)
			grossBill=units*8;
		else if(units<=150)
			grossBill=(100*8)+(units-100)*10;
		else
			grossBill=(100*8)+(50*10)+(units-150)*12;
		double fixedCharge=0;
		if(plan.toLowerCase().equals("home"))
			fixedCharge=100;
		else if(plan.toLowerCase().equals("commercial"))
			fixedCharge=300;
		else if(plan.toLowerCase().equals("solar"))
			fixedCharge=50;
		double bill=(grossBill*0.90)+fixedCharge;
		System.out.println("Total Units = "+units);
		System.out.println("Gross Bill = "+grossBill);
		System.out.println("Fixed Charge = "+fixedCharge);
		System.out.println("Final Bill = "+bill);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.print("Number of plugs: ");
		int n=sc.nextInt();
		System.out.print("Enter Plan (Home/Commercial/Solar): ");
		String plan=sc.next();
		for(int i=0;i<n;i++) {
			System.out.print("Plug ID: ");
			String id=sc.next();
			System.out.print("Voltage Target: ");
			double vt=sc.nextDouble();
			System.out.print("Voltage Warning %: ");
			double vw=sc.nextDouble();
			System.out.print("Voltage Critical %: ");
			double vc=sc.nextDouble();
			System.out.print("Usage Target: ");
			double ut=sc.nextDouble();
			System.out.print("Usage Warning %: ");
			double uw=sc.nextDouble();
			plugs.add(new Plug(id,vt,vw,vc,ut,uw));
		}
		while(true) {
			System.out.println("\n1 Reading");
			System.out.println("2 USAGE");
			System.out.println("3 SORT");
			System.out.println("4 BILLING");
			System.out.println("5 EXIT");
			System.out.print("Choice: ");
			int choice=sc.nextInt();
			if(choice==1) {
				System.out.print("Plug ID: ");
				String id=sc.next();
				Plug p=find(id);
				if(p==null) {
					System.out.println("Plug not found");
					continue;
				}
				System.out.print("Time: ");
				String time=sc.next();
				System.out.print("Voltage: ");
				double voltage=sc.nextDouble();
				System.out.print("Usage: ");
				double usage=sc.nextDouble();
				p.add(new Reading(id,time,voltage,usage));
			}
			else if(choice==2) {
				usage();
			}
			else if(choice==3) {
				sort();
			}
			else if(choice==4) {
				billing(plan);
			}
			else if(choice==5) {
				break;
			}
		}
		System.out.println("Exiting...");
	}
}
