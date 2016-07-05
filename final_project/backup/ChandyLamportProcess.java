import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.rmi.ServerException;
import java.io.PrintWriter;
import java.io.*;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.lang.*;


//boolean record_12,record_13,record_21,record_23,record_31,record_32 = false;

class Marker implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int ID;
	boolean seen;
	static int counter = 0;

	public Marker() {
		ID = counter;
		counter++;
	}
}

public class ChandyLamportProcess extends UnicastRemoteObject implements
		CommonInterface, Runnable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int portNo = 6544;
	static int ID = 0;
	static int processed_images ;
	static int received_images=0;
	static int processed_images_current=0;
	static int processed_images_1 = 0;
	static int processed_images_2 = 0;
	static int processed_images_3 = 0;

	static boolean send_lock=true;
	static boolean snapshot_taken=false;
    String selfName;
	String selfIP;

	int noOfReceivedMarkers = 0;

	String state = "";

	int incomingChannel;

	static boolean recordChannelState = false;
	static boolean record_12,record_13,record_21,record_23,record_31,record_32 = false;
	static boolean freeze_state=false;
	
	int channelState = 0;
	HashMap<String, String> processes = new HashMap<String, String>();
	HashMap<Integer, String> processesID = new HashMap<Integer, String>();

	static Marker m;
	static Marker receiveM;

	static int noOfObjects = 0;
	static int img_index=0;

	//List<String> channel_21 = new ArrayList<String>();
	
	
	//static int channel_12=0;
	//static int channel_12r=0;
	//static int channelState_12=0;
	
	//static int channel_13=0;
	//static int channel_13r=0;
	//static int channelState_13=0;
	
	//static int channel_23=0;
	//static int channel_23r=0;
	//static int channelState_23=0;
	
	//static int channel_21=0;
	//static int channel_21r=0;
	//static int channelState_21=0;
	
	//static int channel_32=0;
	//static int channel_32r=0;
	//static int channelState_32=0;
	
	//static int channel_31=0;
	//static int channel_31r=0;
	//static int channelState_31=0;
	
	static int first_time_marker=0;
	static double amount;
	File file1,file2,file3;

	//boolean sentToComet = false;

	HashMap<Marker, Integer> seenMarkerObject = new HashMap<Marker, Integer>();

	public ChandyLamportProcess() throws RemoteException, UnknownHostException {
		super();

		/*
		processesID.put(0, "comet");
		processesID.put(1, "rhea");
		processesID.put(2, "glados");
        */
		
		processesID.put(1, "GURUJI-PC");
		processesID.put(0, "DELL");
		processesID.put(2, "Uday-PC");
		
		/*
		if (InetAddress.getLocalHost().getHostName().equals("glados")) {
			ID = 2;
		} else if (InetAddress.getLocalHost().getHostName().equals("comet")) {
			ID = 0;
		} else if (InetAddress.getLocalHost().getHostName().equals("rhea")) {
			ID = 1;
		}
*/

		if (InetAddress.getLocalHost().getHostName().equals("Uday-PC")) {
			ID = 2;
		} else if (InetAddress.getLocalHost().getHostName().equals("GURUJI-PC")) {
			ID = 1;
		} else if (InetAddress.getLocalHost().getHostName().equals("DELL")) {
			ID = 0;
		}

		/*processes.put("comet", "129.21.34.80");
		processes.put("rhea", "129.21.37.49");
		processes.put("glados", "129.21.22.196");
*/
        processes.put("GURUJI-PC", "128.61.59.218");
		processes.put("DELL", "143.215.95.234");
		processes.put("Uday-PC", "143.215.81.86");

		selfName = InetAddress.getLocalHost().getHostName();
		selfIP = InetAddress.getLocalHost().getHostAddress();

		if (noOfObjects == 0) {
			++noOfObjects;

			/* FileInputStream inputStream = new FileInputStream("img_values"+ID+".txt");
			try{
			String img_data = IOUtils.toString(inputStream);
			} finally {
				inputStream.close();
			}
			*/
			
			//PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values"+ID+".txt"),true)); //'true' flag for appending			
			//value.println(img_data); //img_data is the first 100 intensity values of images
			
			Registry reg = LocateRegistry.createRegistry(portNo);
			reg.rebind("chandyprocess", this);

			System.out.println("Object bound to the registry");

			
			 try{
        //Specify the file name and path here
    	 file3 =new File("img_values.txt");
		if (ID==0) {
			
		 file1 =new File("img_values_recorded1.txt");
       file2 =new File("img_values_recorded2.txt");
		} else if(ID==1) {
			 file1 =new File("img_values_recorded0.txt");
       file2 =new File("img_values_recorded2.txt");
		} else {
			 file1 =new File("img_values_recorded0.txt");
      file2 =new File("img_values_recorded1.txt");
		}
      
      if(!file3.exists()){
         file3.createNewFile();
      }
    	//Here true is to append the content to file
    	FileWriter fw3 = new FileWriter(file3,true);
    	//BufferedWriter writer give better performance
    	BufferedWriter bw3 = new BufferedWriter(fw3);
      if(file1.exists()){
        // String content1 = readFile("img_values_recorded0.txt", Charset.defaultCharset());
        String content1=null;
		if (ID==0)
		content1 = readFile("img_values_recorded1.txt");
	if (ID==1)
		content1 = readFile("img_values_recorded0.txt");
	if (ID==2)
		content1 = readFile("img_values_recorded0.txt");
	
		bw3.write(content1);
		FileWriter fw1 = new FileWriter(file1);
		fw1.flush();
      }

      if(file2.exists()){
      String content2=null;
if (ID==0)
		content2 = readFile("img_values_recorded2.txt");
	if (ID==1)
		content2 = readFile("img_values_recorded2.txt");
	if (ID==2)
		content2 = readFile("img_values_recorded1.txt");
	
	   bw3.write(content2);
		FileWriter fw2 = new FileWriter(file2);
		fw2.flush();
      }
    	//Closing BufferedWriter Stream
    	bw3.close();

	System.out.println("Data successfully appended at the end of file");

      }catch(IOException ioe){
         System.out.println("Exception occurred:");
    	 ioe.printStackTrace();
       }
			
			
			
			
			
			/*
			 * for (Map.Entry<Integer, String> entry : processesID.entrySet()) {
			 * if (entry.getValue().equals(selfName)) { ID = entry.getKey();
			 * 
			 * if (ID == 0) { // process 1 needs to initiate the snapshot
			 * System.out.println(selfName +
			 * " is going to create a snapshot thread");
			 * this.createSnapshotThread(); } } }
			 */

			if (ID == 0) {
				System.out.println(selfName
						+ " is going to create a snapshot thread");
				this.createSnapshotThread();
			}
		}
	}
	
	public static String readFile( String file ) throws IOException {
    BufferedReader reader = new BufferedReader( new FileReader (file));
    String         line = null;
    StringBuilder  stringBuilder = new StringBuilder();
    String         ls = System.getProperty("line.separator");

    try {
        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        return stringBuilder.toString();
    } finally {
        reader.close();
    }
}

	private void createSnapshotThread() throws RemoteException,
			UnknownHostException {
		ChandyLamportProcess snapshotThread = new ChandyLamportProcess();
		Thread t = new Thread(snapshotThread, "snapshotThread");
		t.start();
	}

	public void send() throws RemoteException, NotBoundException,
			InterruptedException,FileNotFoundException,IOException {
			File file = new File("processed_images.txt");    
			if (file.exists()) {
			BufferedReader br = new BufferedReader(new FileReader("processed_images.txt"));
			processed_images=Integer.parseInt(br.readLine());
			processed_images++;
			br.close();
			}
			else{
				processed_images=0;
			}
			
			while (processed_images<100) {
            Thread.sleep(10000);
			if(send_lock){
			
			//Random r = new Random();
			//int option = r.nextInt(3 - 0);
			
			/*while (option == ID) {
				option = r.nextInt(3 - 0);
				if (option != ID) {
					break;
				}
			}*/
			
			
			
			//int amount = r.nextInt(100 - 0); //extract brightness of images - MUST traverse the image list (100)  
			if(ID==0)
			{
			 amount =this.getNextAverage(processed_images);
			 System.out.println("Average Brightness of an image"+processed_images+"is:"+amount);
			}
			else if(ID==1)
			{
				 amount =this.getNextAverage(processed_images+100);
				 System.out.println("Average Brightness of an image"+processed_images+"is:"+amount);
			}
			else 
			{
				amount =this.getNextAverage(processed_images+200);
				System.out.println("Average Brightness of an image"+processed_images+"is:"+amount);
			}
			//if (processed_images_current - amount >= 0) {
			if(!freeze_state){
			PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values"+".txt"),true)); //'true' flag for appending
			value.println(amount+" "+ID);
			value.close();
			}
			
			
			for (int option=0;option<3;option++){
				if(option!=ID){
				
				String conn = processesID.get(option);
				String IP = processes.get(conn);
				Registry reg = LocateRegistry.getRegistry(IP, portNo);
				CommonInterface chprocesssObj = (CommonInterface) reg
						.lookup("chandyprocess");

				chprocesssObj.connected(selfName);
				
				
				
				//processed_images = processed_images - amount;
				
				/*
				if(ID==0)
				{
					if(option==1)
					{
						channel_12-=amount;
						//processed_images_current-=amount;
					}
					else if(option==2)
					{
						channel_13-=amount;
						//processed_images_current-=amount;
					}
					
				}
				
				else if(ID==1)
				{
					if(option==0)
					{
						channel_21-=amount;
						processed_images_current-=amount;
					}
					else if(option==2)
					{
						channel_23-=amount;
						processed_images_current-=amount;
					}
					
				}
				
				else 
				{
					if(option==0)
					{
						channel_31-=amount;
						processed_images_current-=amount;
					}
					else if(option==1)
					{
						channel_32-=amount;
						processed_images_current-=amount;
					}
					
				}
				*/
				
				 // appending to file while sending
				chprocesssObj.transmitMoney(amount,ID);
				
				/*
				if (option == 0) {
					if (ID == 1) {
						channel_12 = amount;
					} else if (ID == 2) {
						channel_13 = amount;
					}
				}
					
				if (option == 1) {
					if (ID == 2) {
						channel_23 = amount;
					} else if (ID == 0) {
						channel_21 = amount;
					}
				}
				
				if (option == 2) {
					if (ID == 1) {
						channel_32 = amount;
					} else if (ID == 0) {
						channel_31 = amount;
					}
				}
				*/
				System.out.println(this.ID + " sending to " + option);
				
				System.out.println("\n\n" + selfName
						+ " transferred an image of brightness: " + amount + " to : "
						+ conn);
				System.out.println("\n\n" + selfName + "'s processed_images = "
						+ processed_images);
				
				

			}
			}
             			
			
			/*else {
				System.out.println("\n\n" + "Insufficient processed_images");
			}*/
		if(!snapshot_taken){
        PrintWriter value = new PrintWriter(new FileOutputStream(new File("processed_images"+".txt"),false)); //'true' flag for appending
			value.println(processed_images);
			value.close();
		}
		processed_images++;
		img_index++;
		}
		
        }
            
	}
	
	 public double printPixelARGB(int pixel) {
    int alpha = (pixel >> 24) & 0xff;
    int red = (pixel >> 16) & 0xff;
    int green = (pixel >> 8) & 0xff;
    int blue = (pixel) & 0xff;
    double avg = (red + blue + green)/3; 
    return avg;
    // System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
  }


   public double marchThroughImage(BufferedImage image) {
    int w = image.getWidth();
    int h = image.getHeight();
    System.out.println("width, height: " + w + ", " + h);
    double average = 0;
    for (int i = 0; i < h; i++) {
      for (int j = 0; j < w; j++) {
        int pixel = image.getRGB(j, i);
        // printPixelARGB(pixel);
        average += this.printPixelARGB(pixel)/(w*h);
        // System.out.println("Individual Value is :" + average);

        // System.out.println("");
      }
    }
    return average;
    // averages[z++] = String.valueOf(average) + " " + String.valueOf(ID);
    // System.out.println("Average: " + averages[z-1]);
  }

  public double getNextAverage(int index) throws IOException{
  String strI = String.valueOf(index);
  String imageName = "images/img" + strI + ".jpg";
  BufferedImage image = ImageIO.read(this.getClass().getResource(imageName));
  return this.marchThroughImage(image);
}
	
	public static void main(String[] args) throws RemoteException,
			UnknownHostException, NotBoundException, InterruptedException,FileNotFoundException,IOException {
		ChandyLamportProcess clp = new ChandyLamportProcess();
		clp.send();
		// have to write restart function code somewhere and call it here
	}

	@Override
	public void connected(String name) throws RemoteException {
		System.out.println("\n\n" + selfName + " got connected to: " + name);
	}

	@Override
	public void receiveMoney(double amount,int money_id) throws RemoteException, FileNotFoundException {
		
/*
if (recordChannelState) {
			channelState = channelState + amount;
		}
		*/


	if(money_id==0)
	{
		if(ID==1)
		{
		//channel_21+=amount;
		//channel_21.add(amount);
		//processed_images_current++;
		if(!freeze_state){
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values"+".txt"),true)); //'true' flag for appending
		value.println(amount+" "+money_id);
		value.close();
		}
		// processed_images_current+=amount;
		
		if(record_21)
		{
			//channel_21r.add(amount);
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+money_id+".txt"),true)); //'true' flag for appending
		value.println(amount+"  "+money_id);
		value.close();
	
		//	channel_21r+=amount;
			//System.out.println("Updated Channe_21r:"+channel_21r);
		}
			
		}
		else if(ID==2)
		{
		//channel_31.add(amount);
		//processed_images_current++;
		if(!freeze_state){
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values"+".txt"),true)); //'true' flag for appending
		value.println(amount+" "+money_id);
		value.close();
		}
		//channel_31+=amount;
		//processed_images_current+=amount;
		if(record_31)
		{
			//channel_31r.add(amount);
			
			//channel_31r+=amount;
			//System.out.println("Updated Channe_31r:"+channel_31r);
			PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+money_id+".txt"),true)); //'true' flag for appending
		value.println(amount+" "+money_id);
		value.close();
		}
			
		}
	}

	if(money_id==1)
	{
		if(ID==0)
		{
		
		//channel_12.add(amount);
		//processed_images_current++;
		if(!freeze_state){
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values"+".txt"),true)); //'true' flag for appending
		value.println(amount+"  "+money_id);
		value.close();
		}	
		// channel_12+=amount;
		// processed_images_current+=amount;
		if(record_12)
		{
			//channel_12r.add(amount);
			
			// channel_12r+=amount;
			//System.out.println("Updated Channe_12r:"+channel_12r);
			PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+money_id+".txt"),true)); //'true' flag for appending
		value.println(amount+"  "+money_id);
		value.close();
		}
			
			
		}
		else if(ID==2)
		{

			//channel_32.add(amount);
		//processed_images_current++;
		if(!freeze_state){
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values"+".txt"),true)); //'true' flag for appending
		value.println(amount+" "+money_id);
		value.close();
		}
		// channel_32+=amount;
		// processed_images_current+=amount;	
		if(record_32)
		{
			//channel_32r.add(amount);
			//channel_32r+=amount;
			//System.out.println("Updated Channe_32r:"+channel_32r);
			PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+money_id+".txt"),true)); //'true' flag for appending
		value.println(amount+" "+money_id);
		value.close();
		}
		
		}
	}
	if(money_id==2)
	{
		if(ID==0)
		{
			//channel_13.add(amount);
		//processed_images_current++;
		if(!freeze_state){
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values"+".txt"),true)); //'true' flag for appending
		value.println(amount+" "+money_id);
		value.close();
		}
			
		// channel_13+=amount;
		// processed_images_current+=amount;
		if(record_13)
		{
			//channel_13r.add(amount);
			// channel_13r+=amount;
			//System.out.println("Updated Channe_13r:"+channel_13r);
			PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+money_id+".txt"),true)); //'true' flag for appending
		value.println(amount+" "+money_id);
		value.close();
		}
				
		}
		else if(ID==1)
		{
		//channel_23.add(amount);
		//processed_images_current++;
		if(!freeze_state){
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values"+".txt"),true)); //'true' flag for appending
		value.println(amount+" "+money_id);
		value.close();
		}
		//channel_23+=amount;
		//processed_images_current+=amount;
		if(record_23)
		{
			// channel_23r+=amount;
			//channel_23r.add(amount);
			//System.out.println("Updated Channe_23r:"+channel_23r);
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+money_id+".txt"),true)); //'true' flag for appending
		value.println(amount+" "+money_id);
		value.close();
		}
				
			
		
		}
	
		
	
	}

		//this.processed_images = this.processed_images + amount;
		//System.out.println("\n\n" + this.selfName + "'s updated processed_images = "
			//	+ this.processed_images);
	}

	@Override
	public void run() {
		//while (true) {
			try {
				//Thread.sleep(160000);
				Thread.sleep(45000);
				first_time_marker++;
				System.out.println("First Time Marker before calling snapshot" + first_time_marker);
				takeSnapshot();
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		//}
	}

	private void takeSnapshot() throws RemoteException, NotBoundException, UnknownHostException, InterruptedException {
		//recording it's own state
		
		System.out.println("First Time Marker" + first_time_marker);
		//processed_images+=channel_12+channel_13;
		//processed_images=processed_images_current;
		//channel_12=0;
		//channel_13=0;
		send_lock=false;
		
		System.out.println("entered take snapshot function");
		//recording its ownstate done
		freeze_state=true;
		//starts recording
		record_12=true;
		record_13=true;
		snapshot_taken=true;
		
		//this.m = new Marker();

		state = "";
		
		state += selfName + " processed_images: " + processed_images;
		int ID;
		String hostName_1 = "";
		String connectToIP_1 = "";
		String hostName_2 = "";
		String connectToIP_2 = "";
		String channelValue="";
		String channelState = "";
		/*for (Map.Entry<Integer, String> entry : processesID.entrySet()) {
			ID = entry.getKey();
			if (ID != this.ID) {
				hostName = processesID.get(ID);
				connectToIP = processes.get(hostName);
				System.out.println("--debug print-- Line 244: connectToIP:"+connectToIP);
				Registry r = LocateRegistry.getRegistry(connectToIP, portNo);
				CommonInterface processObj = (CommonInterface) r
						.lookup("chandyprocess");
				System.out.println("Initiator Sending marker to ID:"+ID);
				//processObj.assignMarkerObject(this.m,this.ID);
				processObj.assignMarkerObject(this.m,0);
				/*
				state += processObj.sendState(); // sends the current processed_images
				System.out.println("--debug print-- Line 248: state:"+state);
				channelValue = processObj.collectChannelState();
				System.out.println("--debug print-- Line 250: channelValue:"+channelValue);
				processObj.sendM(selfIP);
				
				//channelState = "\nChannel state between " + this.selfName
						//+ " and " + hostName + " is: " + channelValue;
				channelState = channelValue;		
				state += channelState;
				*/
				
			
				hostName_1 = processesID.get(1);
				connectToIP_1 = processes.get(hostName_1);
				hostName_2 = processesID.get(2);
				connectToIP_2 = processes.get(hostName_2);
				System.out.println("--debug print-- Line 244: connectToIP_1:"+connectToIP_1);
				System.out.println("--debug print-- Line 244: connectToIP_2:"+connectToIP_2);
				System.out.println("--debug print-- Line 244: hostName_1:"+hostName_1);
				System.out.println("--debug print-- Line 244: hostName_2:"+hostName_2);
				Registry r1 = LocateRegistry.getRegistry(connectToIP_1, portNo);
				Registry r2 = LocateRegistry.getRegistry(connectToIP_2, portNo);
				
				CommonInterface processObj_1 = (CommonInterface) r1
						.lookup("chandyprocess");
				CommonInterface processObj_2 = (CommonInterface) r2
						.lookup("chandyprocess");
				
				//processObj.assignMarkerObject(this.m,this.ID);
				System.out.println("Initiator Sending marker to :"+hostName_1);
				processObj_1.assignMarkerObject(new Marker(),0);
				
				System.out.println("Initiator Sending marker to :"+hostName_2);
				processObj_2.assignMarkerObject(new Marker(),0);
				send_lock=true;
				
				
				
				
		
		
		
		/*
		state+= "channel state between process 1 and process 2:"+channel_21+"  channel state between process 1 and process 3"+channel_31;
		System.out.println("---------------STATE------------");
		
		System.out.println(state);
		System.out.println("---------------------------------");
		*/
		Thread.sleep(30000);
				// call a function here which will print global state when algo terminates
				//printGlobalState();
	}
	
	/*
	private void printGlobalState() throws RemoteException, NotBoundException, UnknownHostException {
	System.out.println("Entered print Global State function ");
	String hostName = "";
	String connectToIP = "";
	boolean flag_print_global=true;
	int retValue_2[] = new int[5];
	int retValue_3[] = new int[5];	
	
	
	while(flag_print_global){
	System.out.println("record 12"+record_12+"record 13"+record_13);

	
	
	if(record_12==false && record_13==false)
	{
		System.out.println("Entered record_12==false && record_13==false ");
		flag_print_global=false;
	        for (int ID=0;ID<3;ID++) {
			
			if (ID != this.ID) {
				hostName = processesID.get(ID);
				connectToIP = processes.get(hostName);
				Registry r = LocateRegistry.getRegistry(connectToIP, portNo);
				CommonInterface processObj = (CommonInterface) r
						.lookup("chandyprocess");
				if(ID==1){
								
				do{
					retValue_2=processObj.collectChannelState();
					if(retValue_2[2]==1){
						this.record_21=true;
					}
					else{
						this.record_21=false;
					}
					if(retValue_2[3]==1){
						this.record_23=true;
					}
					else{
						this.record_23=false;
					}
				
					
				
				}while(!(record_21==false && record_23==false));
				System.out.println("Came out of  record_21==false && record_23==false Line:530");	
				
			    this.channelState_21=retValue_2[0];
				this.channelState_23=retValue_2[1];
				this.processed_images_2=retValue_2[4];
				
			    
				}
				else if(ID==2){
				
				
				
				do{
					retValue_3=processObj.collectChannelState();
					if(retValue_3[2]==1){
						this.record_31=true;
					}
					else{
						this.record_31=false;
					}
					if(retValue_3[3]==1){
						this.record_32=true;
					}
					else{
						this.record_31=false;
					}
				
				}while(!(record_31==false && record_32==false));
				System.out.println("Came out of  record_31==false && record_32==false  Line:559");
				
			    this.channelState_31=retValue_3[0];
				this.channelState_32=retValue_3[1];
				this.processed_images_3=retValue_3[4];
				
				System.out.println("At Line:565");
				
				
						
				
			}
		}
	
	}
	
	
	
	
	}
	}
	
	System.out.println("channelState_12 is"+channelState_12+"\n  channelState_13 is"+channelState_13+ 
	"\n channelState_21 is"+channelState_21+"\n  channelState_23 is"+channelState_23+"\n channelState_31 is"
	+channelState_31+"\n  channelState_32 is"+channelState_32+"process 1's processed_images is"+processed_images+"process 2's processed_images is"+processed_images_2
	+"process 3's processed_images is"+processed_images_3);
	
	}
	*/
	@Override
	public void sendM(String IP) throws RemoteException, NotBoundException {
		Registry r = LocateRegistry.getRegistry(IP, portNo);
		CommonInterface pObj = (CommonInterface) r.lookup("chandyprocess");

	}

	@Override
	public void assignMarkerObject(Marker mark, int sender_id) throws RemoteException,  UnknownHostException, NotBoundException, InterruptedException {
		//if (InetAddress.getLocalHost().getHostName().equals("glados")) {
		//if (InetAddress.getLocalHost().getHostName().equals("Ankul-PC")) {	
		//	receiveM = mark;
		//	System.out.println("--debug print-- entered receiveM");
		//} else {
		//	System.out.println("--debug print-- did not enter receiveM")
	Thread.sleep(4000);
		
	System.out.println("received marker from ID" + sender_id);
	String hostName = "";
	String connectToIP = "";
	m = mark;
	//System.out.println("First time Marker above if condition above" + first_time_marker);
	
    if(first_time_marker==0 && ID!=0)
	{
		send_lock=false;
        first_time_marker++;
		//System.out.println("First time Marker above if condition below" + first_time_marker);
	if(ID==0){

	if(sender_id==1)
	{
		//processed_images+=channel_12+channel_13;   //records its own state
		//Thread.sleep(3000);
		record_13=true;
		freeze_state=true;
		snapshot_taken=true;
		//processed_images=processed_images_current;
		//channel_12=0;
		//channel_13=0;
		//channelState_12=0;
		
		System.out.println("Made record_13 "+record_13);
		//System.out.println("Made processed_images "+processed_images);
	}
	
	if(sender_id==2)
	{
		
		//processed_images+=channel_13+channel_12; //records its own state
		//Thread.sleep(3000);
		record_12=true;
		freeze_state=true;
		snapshot_taken=true;
		
		//processed_images=processed_images_current;
		//channel_13=0;
		//channel_12=0;
		//channelState_13=0;
		
		System.out.println("Made record_12 "+record_12);
		//System.out.println("Made processed_images "+processed_images);
	}

}

else if(ID==1){

	if(sender_id==0)
	{
		//processed_images+=channel_21+channel_23;  //records its own state
		//Thread.sleep(3000);
		record_23=true;
		freeze_state=true;
		snapshot_taken=true;
		
		//processed_images=processed_images_current;
		//channel_21=0;
		//channel_23=0;
		//channelState_21=0;
		
		System.out.println("Made record_23 "+record_23);
		//System.out.println("Made processed_images "+processed_images);
		
	}
	
	if(sender_id==2)
	{
		//processed_images+=channel_23+channel_21;            //records its own state
		//Thread.sleep(3000);
		record_21=true;	
		freeze_state=true;
		snapshot_taken=true;
		//processed_images=processed_images_current;
		//channel_23=0;
		//channel_21=0;
		//channelState_23=0;	
		
		System.out.println("Made record_21 "+record_21);
		//System.out.println("Made processed_images "+processed_images);
	}




}

else if(ID==2){

	if(sender_id==0)
	{
		//processed_images+=channel_31+channel_32;         //records its own state
		//Thread.sleep(3000);
		freeze_state=true;
		record_32 =true;
		snapshot_taken=true;
		
		//processed_images=processed_images_current;
		//channel_31=0;
		//channel_32=0;
		//channelState_31=0;
		
		System.out.println("Made record_32 "+record_32);
		//System.out.println("Made processed_images "+processed_images);
	}
	
	if(sender_id==1)
	{
		//processed_images+=channel_32+channel_31;        //records its own state
		//Thread.sleep(3000);
		record_31=true;
		freeze_state=true;
		snapshot_taken=true;
		//processed_images=processed_images_current;
		//channel_32=0;
		//channel_31=0;
		//channelState_32=0;
		
		//System.out.println("Made record_31 "+record_31);
		//System.out.println("Made processed_images "+processed_images);
	}


}
//TODO //Write code to send markers to other processes
//Thread.sleep(8000);

/*
for(int x=0;x<3;x++)
{
	if(x!=ID){
hostName = processesID.get(x);
connectToIP = processes.get(hostName);
Registry r = LocateRegistry.getRegistry(connectToIP, portNo);
				CommonInterface new_processObj = (CommonInterface) r
						.lookup("chandyprocess");
				System.out.println("Process:"+ID+" Sending Marker to process:"+x);
				new_processObj.assignMarkerObject(new Marker(),ID);
				//System.out.println("Process:"+ID+" Sending Marker to process:"+x);
				//Thread.sleep(8000);
	}

}
*/
//Thread.sleep(4000);
this.sendMarkersToOtherProcesses();

}
else{
if(ID==0){

if(sender_id==1){
record_12=false;
//channelState_12=channelState_12+channel_12r;
//channel_12r=0;

System.out.println("Made record_12 "+record_12);
}
else if(sender_id==2){
record_13=false;
//channelState_13=channelState_13+channel_13r;
//channel_13r=0;

System.out.println("Made record_13 "+record_13);
}

}

else if(ID==1){
if(sender_id==2){
record_23=false;
//channelState_23=channelState_23+channel_23r;
//channel_23r=0;

System.out.println("Made record_23 "+record_23);
}
else if(sender_id==0){
record_21=false;
//channelState_21=channelState_21+channel_21r;
//channel_21r=0;

System.out.println("Made record_21 "+record_21);
}
}

else if(ID==2){
	if(sender_id==0){
record_31=false;
//channelState_31=channelState_31+channel_31r;
//channel_31r=0;

System.out.println("Made record_31 "+record_31);
	}
else if(sender_id==1){	
record_32=false;
//channelState_32=channelState_32+channel_32r;
//channel_32r=0;

System.out.println("Made record_32 "+record_32);
}
}

}
                                          
			
		//}
}

public void sendMarkersToOtherProcesses() throws RemoteException,  UnknownHostException, NotBoundException, InterruptedException {
	String hostName = "";
	String connectToIP = "";
	for(int x=0;x<3;x++)
{
	if(x!=this.ID){
hostName = processesID.get(x);
connectToIP = processes.get(hostName);
Registry r = LocateRegistry.getRegistry(connectToIP, portNo);
				CommonInterface new_processObj = (CommonInterface) r
						.lookup("chandyprocess");
				System.out.println("Inside sendMarkersToOtherProcesses finction");
				System.out.println("Process:"+this.ID+" Sending Marker to process:"+x);
				new_processObj.assignMarkerObject(new Marker(),this.ID);
				//System.out.println("Process:"+ID+" Sending Marker to process:"+x);
				//Thread.sleep(8000);
	}

}
    send_lock=true;
	System.out.println("Made send_lock:"+send_lock);
}
	
	
	@Override
	public String sendState() throws RemoteException {
		
		System.out.println("--debug printreturn value of sendStae() function"+"\n" + selfName + " processed_images = " + processed_images);
		
		return ("\n" + selfName + " processed_images = " + processed_images);
		
	}

	@Override
	public void getM(Marker mark) throws RemoteException {
		receiveM = mark;
		System.out.println(selfName + " has receiveM :" + receiveM
				+ " with ID = " + receiveM.ID);
	}

	/*
	@Override
	public int[] collectChannelState() throws RemoteException {

		//String retValue;
		//String retValue1;
		
		int retValue[] = new int[5];
		if (this.ID == 0) {
			retValue[0] = this.channelState_12;
			retValue[1] = this.channelState_13;
			retValue[2] = this.record_12?1:0;
			retValue[3] = this.record_13?1:0;
			retValue[4] = this.processed_images;
			
			//channel_12 = 0;
			//retValue1 = " channel state between process 3 and process 2:"+channel_32;
			//channel_32=0;
		} else if (this.ID == 1) {
			
			retValue[0] = this.channelState_21;
			retValue[1] = this.channelState_23;
			retValue[2] = this.record_21?1:0;
			retValue[3] = this.record_23?1:0;
			retValue[4] = this.processed_images;
		    /*
			retValue = "channel state between process 2 and process 3:"+channel_23;
			channel_23 = 0;
			retValue1= " channel state between process 1 and process 3::"+ channel_13;
			channel_13=0;
			*/
		
		/*
		} else {
			retValue[0] = this.channelState_31;
			retValue[1] = this.channelState_32;
			retValue[2] = this.record_31?1:0;
			retValue[3] = this.record_32?1:0;
			retValue[4] = this.processed_images;
		}
        //System.out.println("--debug print return value of collectChannelState() function"+retValue);
		return retValue;
	}
*/
	@Override
	public void transmitMoney(double amount, int money_sender_id) throws RemoteException,InterruptedException,FileNotFoundException {
		//incomingChannel = amount;
		//Thread.sleep(4000);
		this.receiveMoney(amount,money_sender_id);
	}

	@Override
	public int incomingChannelValue() {
		return incomingChannel;
	}
	}
