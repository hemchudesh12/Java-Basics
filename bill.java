
package prep;
import java.util.*;
import java.time.*;
class Reading{
    String id,time;
    double voltage,usage;
    Reading(String id,String time,double voltage,double usage){
        this.id=id;
        this.time=time;
        this.voltage=voltage;
        this.usage=usage;
    }
}
class Plug{
    String id;
    double vt,vw,vc;
    double ut,uw;
    double[] va,ua;
    int vi=0,ui=0;
    ArrayList<Double> vData=new ArrayList<>();
    ArrayList<Double> uData=new ArrayList<>();
    double total=0;
    boolean power=true;
    long lastUsageNotification=0;
    Plug(String id,double vt,double vw,double vc,double ut,double uw){
        this.id=id;
        this.vt=vt;
        this.vw=vw;
        this.vc=vc;
        this.ut=ut;
        this.uw=uw;
        va=new double[bill.voltageBucketSize];
        ua=new double[bill.usageBucketSize];
    }
    synchronized void add(Reading r){
        va[vi++]=r.voltage;
        ua[ui++]=r.usage;
        if(vi==va.length)voltageCheck();
        if(ui==ua.length)usageCheck();
    }
    void quickSort(double[] arr,int low,int high){
        if(low<high){
            int pivot=partition(arr,low,high);
            quickSort(arr,low,pivot-1);
            quickSort(arr,pivot+1,high);
        }
    }
    int partition(double[] arr,int low,int high){
        double pivot=arr[high];
        int i=low-1;
        for(int j=low;j<high;j++){
            if(arr[j]<=pivot){
                i++;
                double temp=arr[i];
                arr[i]=arr[j];
                arr[j]=temp;
            }
        }
        double temp=arr[i+1];
        arr[i+1]=arr[high];
        arr[high]=temp;
        return i+1;
    }
    synchronized void voltageCheck(){
        if(vi==0)return;
        double[] values=Arrays.copyOf(va,vi);
        quickSort(values,0,values.length-1);
        double max=values[values.length-1];
        vData.add(max);
        double wl=vt-vt*vw/100.0;
        double wh=vt+vt*vw/100.0;
        double cl=vt-vt*vc/100.0;
        double ch=vt+vt*vc/100.0;
        System.out.println("\n--- "+id+" VOLTAGE BUCKET ---");
        System.out.println("Time: "+LocalTime.now());
        System.out.println("Maximum Voltage: "+max);
        System.out.println("Warning Range: "+wl+" - "+wh);
        System.out.println("Critical Range: "+cl+" - "+ch);
        if(max<cl||max>ch){
            power=false;
            System.out.println("Voltage Alarm: CRITICAL");
            System.out.println("Action: Power OFF");
        }else if(max<wl||max>wh){
            System.out.println("Voltage Alarm: WARNING");
        }else{
            power=true;
            System.out.println("Voltage Alarm: NORMAL");
            System.out.println("Action: Power ON");
        }
        vi=0;
    }
    synchronized void usageCheck(){
        if(ui==0)return;
        double sum=0;
        for(int i=0;i<ui;i++)sum+=ua[i];
        double avg=sum/ui;
        uData.add(avg);
        total+=sum*bill.readingIntervalMs/3600000000.0;
        double low=ut-ut*uw/100.0;
        double high=ut+ut*uw/100.0;
        System.out.println("\n--- "+id+" USAGE WINDOW ---");
        System.out.println("Average Usage: "+avg);
        System.out.println("Usage Sum: "+sum);
        System.out.println("Usage Range: "+low+" - "+high);
        System.out.println("Total Consumption: "+total+" kWh");
        if(avg<low||avg>high){
            long now=System.currentTimeMillis();
            if(lastUsageNotification==0||now-lastUsageNotification>=3600000L){
                System.out.println("Usage Alarm: WARNING");
                System.out.println("Notification sent for "+id);
                lastUsageNotification=now;
            }else{
                System.out.println("Usage Alarm: WARNING; notification limit reached");
            }
        }else{
            System.out.println("Usage Alarm: NORMAL");
        }
        ui=0;
    }
    synchronized void showLive(){
        double wl=vt-vt*vw/100.0;
        double wh=vt+vt*vw/100.0;
        double cl=vt-vt*vc/100.0;
        double ch=vt+vt*vc/100.0;
        double low=ut-ut*uw/100.0;
        double high=ut+ut*uw/100.0;
        System.out.println("\n--- LIVE STATUS: "+id+" ---");
        System.out.println("Voltage Target: "+vt);
        System.out.println("Voltage Warning Band: "+wl+" - "+wh);
        System.out.println("Voltage Critical Band: "+cl+" - "+ch);
        if(vi>0){
            double max=va[0];
            for(int i=1;i<vi;i++)if(va[i]>max)max=va[i];
            System.out.println("Current Maximum Voltage: "+max);
        }
        System.out.println("Usage Target: "+ut);
        System.out.println("Usage Warning Band: "+low+" - "+high);
        if(ui>0){
            double sum=0;
            for(int i=0;i<ui;i++)sum+=ua[i];
            System.out.println("Current Average Usage: "+sum/ui);
            System.out.println("Current Usage Sum: "+sum);
        }
        System.out.println("Power Status: "+(power?"ON":"OFF"));
        System.out.println("Consumption: "+total+" kWh");
    }
    synchronized void resizeBuckets(int voltageSize,int usageSize){
        if(vi>0)voltageCheck();
        if(ui>0)usageCheck();
        va=new double[voltageSize];
        ua=new double[usageSize];
        vi=0;
        ui=0;
    }
}
public class bill{
    static ArrayList<Plug> plugs=new ArrayList<>();
    static volatile long readingIntervalMs=30000;
    static volatile long voltageWindowMs=120000;
    static volatile long usageWindowMs=3600000;
    static volatile int voltageBucketSize=(int)(voltageWindowMs/readingIntervalMs);
    static volatile int usageBucketSize=(int)(usageWindowMs/readingIntervalMs);
    static volatile boolean running=true;
    static final Object configLock=new Object();
    static Random random=new Random();
    static Plug find(String id){
        for(Plug p:plugs)if(p.id.equalsIgnoreCase(id))return p;
        return null;
    }
    static void startSensor(){
        Thread sensor=new Thread(()->{
            while(running){
                long started=System.currentTimeMillis();
                synchronized(configLock){
                    for(Plug p:plugs){
                        if(!running)break;
                        double voltage=220+random.nextDouble()*20;
                        double usage=300+random.nextDouble()*500;
                        String time=LocalTime.now().toString();
                        Reading r=new Reading(p.id,time,voltage,usage);
                        System.out.printf("%n[%s] Time: %s | Voltage: %.2f V | Usage: %.2f W%n",p.id,time,voltage,usage);
                        p.add(r);
                    }
                }
                long delay=readingIntervalMs-(System.currentTimeMillis()-started);
                if(delay>0&&running){
                    try{
                        Thread.sleep(delay);
                    }catch(InterruptedException e){
                        break;
                    }
                }
            }
        });
        sensor.setName("Sensor-Thread");
        sensor.setDaemon(true);
        sensor.start();
    }
    static void usage(){
        double home=0;
        System.out.println("\n--- PLUG USAGE REPORT ---");
        for(Plug p:plugs){
            synchronized(p){
                System.out.printf("%s = %.6f kWh%n",p.id,p.total);
                home+=p.total;
            }
        }
        System.out.printf("Home Total = %.6f kWh%n",home);
        for(Plug p:plugs){
            synchronized(p){
                double share=home==0?0:p.total*100/home;
                System.out.printf("%s Share = %.2f%%%n",p.id,share);
            }
        }
    }
    static void sort(){
        ArrayList<Plug> sorted=new ArrayList<>(plugs);
        for(int i=0;i<sorted.size()-1;i++){
            for(int j=0;j<sorted.size()-i-1;j++){
                if(sorted.get(j).total<sorted.get(j+1).total){
                    Plug temp=sorted.get(j);
                    sorted.set(j,sorted.get(j+1));
                    sorted.set(j+1,temp);
                }
            }
        }
        System.out.println("\n--- CONSUMPTION RANKING ---");
        for(Plug p:sorted)System.out.printf("%s = %.6f kWh%n",p.id,p.total);
        if(!sorted.isEmpty())System.out.println("Highest Consumer = "+sorted.get(0).id);
    }
    static double calculateGrossBill(double units,int slab){
        if(units<=0)return 0;
        if(slab==1){
            double covered=Math.min(units,100);
            return covered*8+calculateGrossBill(units-covered,2);
        }
        if(slab==2){
            double covered=Math.min(units,50);
            return covered*10+calculateGrossBill(units-covered,3);
        }
        return units*12;
    }
    static double fixedCharge(String plan){
        if(plan.equalsIgnoreCase("home"))return 100;
        if(plan.equalsIgnoreCase("commercial"))return 300;
        if(plan.equalsIgnoreCase("solar"))return 50;
        System.out.println("Unknown plan. Home charge will be used.");
        return 100;
    }
    static void billing(String plan){
        double units=0;
        for(Plug p:plugs){
            synchronized(p){
                units+=p.total;
            }
        }
        double grossBill=calculateGrossBill(units,1);
        double fixed=fixedCharge(plan);
        double billAmount=grossBill*0.90+fixed;
        System.out.println("\n--- ELECTRICITY BILL ---");
        System.out.printf("Total Units = %.6f kWh%n",units);
        System.out.printf("Gross Bill = %.2f%n",grossBill);
        System.out.printf("Discount = %.2f%n",grossBill*0.10);
        System.out.printf("Fixed Charge = %.2f%n",fixed);
        System.out.printf("Final Bill = %.2f%n",billAmount);
    }
    static void configure(Scanner sc){
        System.out.println("\n--- CONFIGURE SENSOR ---");
        System.out.print("Reading interval in seconds: ");
        long interval=sc.nextLong();
        System.out.print("Voltage window in seconds: ");
        long voltageWindow=sc.nextLong();
        System.out.print("Usage window in seconds: ");
        long usageWindow=sc.nextLong();
        if(interval<=0||voltageWindow<=0||usageWindow<=0){
            System.out.println("All values must be greater than zero.");
            return;
        }
        if(interval>Long.MAX_VALUE/1000||voltageWindow>Long.MAX_VALUE/1000||usageWindow>Long.MAX_VALUE/1000){
            System.out.println("Time interval is too large.");
            return;
        }
        long intervalMs=interval*1000;
        long voltageMs=voltageWindow*1000;
        long usageMs=usageWindow*1000;
        int voltageSize=(int)Math.max(1,(voltageMs+intervalMs-1)/intervalMs);
        int usageSize=(int)Math.max(1,(usageMs+intervalMs-1)/intervalMs);
        if(voltageSize>Integer.MAX_VALUE-8||usageSize>Integer.MAX_VALUE-8){
            System.out.println("Bucket size is too large.");
            return;
        }
        synchronized(configLock){
            for(Plug p:plugs)p.resizeBuckets(voltageSize,usageSize);
            readingIntervalMs=intervalMs;
            voltageWindowMs=voltageMs;
            usageWindowMs=usageMs;
            voltageBucketSize=voltageSize;
            usageBucketSize=usageSize;
        }
        System.out.println("Configuration updated.");
        showConfiguration();
    }
    static void showConfiguration(){
        System.out.println("\n--- CURRENT CONFIGURATION ---");
        System.out.println("Reading Interval: "+readingIntervalMs/1000+" seconds");
        System.out.println("Voltage Window: "+voltageWindowMs/1000+" seconds");
        System.out.println("Voltage Bucket Size: "+voltageBucketSize);
        System.out.println("Usage Window: "+usageWindowMs/1000+" seconds");
        System.out.println("Usage Window Size: "+usageBucketSize);
    }
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        System.out.print("Number of plugs: ");
        int n=sc.nextInt();
        System.out.print("Enter Plan (Home/Commercial/Solar): ");
        String plan=sc.next();
        for(int i=0;i<n;i++){
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
        startSensor();
        while(true){
            System.out.println("\n========== SMART ENERGY MONITOR ==========");
            System.out.println("1. Check Usage Report");
            System.out.println("2. Calculate Bill");
            System.out.println("3. Consumption Ranking");
            System.out.println("4. Live Voltage and Usage Status");
            System.out.println("5. Change Sensor Interval and Window Durations");
            System.out.println("6. Show Current Configuration");
            System.out.println("7. Exit");
            System.out.print("Choice: ");
            int choice=sc.nextInt();
            if(choice==1)usage();
            else if(choice==2)billing(plan);
            else if(choice==3)sort();
            else if(choice==4){
                for(Plug p:plugs)p.showLive();
            }else if(choice==5)configure(sc);
            else if(choice==6)showConfiguration();
            else if(choice==7){
                running=false;
                System.out.println("Exiting...");
                break;
            }else{
                System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }
}
