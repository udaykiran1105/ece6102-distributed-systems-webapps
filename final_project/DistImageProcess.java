/*
Image Processing application on a Dependable Distributed System using RMI

Developed by:
Prapurna Jayakrishna Duvvuri
Uday Kiran Ravuri
Mrunmayi Sharad Paranjape
Rudra Dushyant Purohit
Ankul Jain

This code has the following key methods and classes:

Marker class - defines the structure of the marker messages

DistImageProcess class - initializes the processes on the nodes and implements the Chandy Lamport algorithm

img_values.txt - stores the current state of the process in persistent memory

current_img_values.txt - gives the live picture of the ongoing transactions since the beginning of the process 
							but it does not represent the state of the process

img_values_recordedx.txt - Depending on the unique process ID, this file records the incoming channel states.
Ex: Process with ID '0' has 'img_values_recorded1' and 'img_values_recorded2' corresponding to its two incoming channels
						
processed_images.txt - This file records the number of images which have been processed until the last checkpoint in each process

 							
*/
import java.io.*;
import java.lang.*;
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
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.nio.file.Files;


class Marker implements Serializable {
	
	private static final long serialVersionUID = 1L;
	int markerID;
	static int ctr = 0;

	public Marker() {
		markerID = ctr;
		ctr++;
	}
}

public class DistImageProcess extends UnicastRemoteObject implements
		ChandyComms, Runnable, Serializable {
	
	private static final long serialVersionUID = 1L;

	int portNo = 6544;
	static int ID = 0;
	static int processed_images ;
	static int received_images=0;
	static int processed_images_current=0;
	static int processed_images_1 = 0;
	static int processed_images_2 = 0;
	static int processed_images_3 = 0;

	static boolean send_unlock=true;
	static boolean snapshot_taken=false;
    String selfName;
	String selfIP;

	int noOfReceivedMarkers = 0;

	String state = "";

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
	
	static int first_time_marker=0;
	static double img_digest;
	File file1,file2,file3;

	HashMap<Marker, Integer> seenMarkerObject = new HashMap<Marker, Integer>();

	public DistImageProcess() throws RemoteException, UnknownHostException {
		super();
		
		processesID.put(1, "chy");
		processesID.put(0, "Uday-PC");
		processesID.put(2, "MEDA-PC");

		if (InetAddress.getLocalHost().getHostName().equals("MEDA-PC")) {
			ID = 2;
		} else if (InetAddress.getLocalHost().getHostName().equals("chy")) {
			ID = 1;
		} else if (InetAddress.getLocalHost().getHostName().equals("Uday-PC")) {
			ID = 0;
		}

        
		processes.put("chy", "10.0.0.4");
		processes.put("MEDA-PC", "10.0.0.5");
		processes.put("Uday-PC", "10.0.0.14");

		selfName = InetAddress.getLocalHost().getHostName();
		selfIP = InetAddress.getLocalHost().getHostAddress();

		if (noOfObjects == 0) {
			++noOfObjects;

	
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

		//copy img_values into current_img_values
		String content3 = readFile("img_values.txt");
		File file4 =new File("current_img_values.txt");	
		FileWriter fw4 = new FileWriter(file4);
    	BufferedWriter bw4 = new BufferedWriter(fw4);
		bw4.write(content3);
		bw4.close();
		
	System.out.println("Data successfully appended at the end of file");

      } catch(IOException ioe){
         System.out.println("Exception occurred:");
    	 ioe.printStackTrace();
      }
			
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
				
		DistImageProcess snapshotThread = new DistImageProcess();
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
            Thread.sleep(5000);
			if(send_unlock){
			
			if(ID==0)
			{
			 img_digest =this.getNextAverage(processed_images);
			 System.out.println("Average Brightness of an image"+processed_images+"is:"+img_digest);
			}
			else if(ID==1)
			{
				 img_digest =this.getNextAverage(processed_images+100);
				 System.out.println("Average Brightness of an image"+processed_images+"is:"+img_digest);
			}
			else 
			{
				img_digest =this.getNextAverage(processed_images+200);
				System.out.println("Average Brightness of an image"+processed_images+"is:"+img_digest);
			}
			
			PrintWriter value = new PrintWriter(new FileOutputStream(new File("current_img_values"+".txt"),true)); //'true' flag for appending
			value.println(img_digest+" "+ID);
			value.close();
			
		if(!freeze_state){	
		
			//copy current_img_values into img_values
		String content4 = readFile("current_img_values.txt");
		File file4 =new File("img_values.txt");	
		FileWriter fw4 = new FileWriter(file4);
    	BufferedWriter bw4 = new BufferedWriter(fw4);
		bw4.write(content4);
		bw4.close();	
			}
			
			for (int option=0;option<3;option++){
				
				if(option!=ID){
				
				String conn = processesID.get(option);
				String IP = processes.get(conn);
				Registry reg = LocateRegistry.getRegistry(IP, portNo);
				ChandyComms chprocesssObj = (ChandyComms) reg
						.lookup("chandyprocess");

				chprocesssObj.connected(selfName);
				
				
				 // appending to file while sending
				chprocesssObj.transmitImage(img_digest,ID);
				

				System.out.println(this.ID + " sending to " + option);
				
				System.out.println("\n\n" + selfName
						+ " transferred an image of brightness: " + img_digest + " to : "
						+ conn);
				System.out.println("\n\n" + selfName + "'s processed_images = "
						+ processed_images);
				
			}
			}
             			
			
		if(!snapshot_taken){
			
        value = new PrintWriter(new FileOutputStream(new File("processed_images"+".txt"),false)); //'true' flag for appending
			value.println(processed_images);
			value.close();
		
		}
		
		processed_images++;
		img_index++;
		}
        }
            
	}
	
public double meanImage(int pixel) {
		 
    int alpha = (pixel >> 24) & 0xff;
    int red = (pixel >> 16) & 0xff;
    int green = (pixel >> 8) & 0xff;
    int blue = (pixel) & 0xff;
    double avg = (red + blue + green)/3; 
    return avg;
    
  }


   public double processImage(BufferedImage image) {
    int w = image.getWidth();
    int h = image.getHeight();
    System.out.println("width, height: " + w + ", " + h);
    double average = 0;
	
    for (int i = 0; i < h; i++) {
      for (int j = 0; j < w; j++) {
        int pixel = image.getRGB(j, i);
        average += this.meanImage(pixel)/(w*h);
      }
    }
	
    return average;
  }

  public double getNextAverage(int index) throws IOException{
  String strI = String.valueOf(index);
  String imageName = "images/img" + strI + ".jpg";
  BufferedImage image = ImageIO.read(this.getClass().getResource(imageName));
  return this.processImage(image);
}
	
	public static void main(String[] args) throws RemoteException,
			UnknownHostException, NotBoundException, InterruptedException,FileNotFoundException,IOException {
		DistImageProcess clp = new DistImageProcess();
		clp.send();
	}

	@Override
	public void connected(String name) throws RemoteException {
		System.out.println("\n\n" + selfName + " got connected to: " + name);
	}

	@Override
	public void receiveImage(double img_digest,int image_id) throws RemoteException, FileNotFoundException, IOException {
		

	if(image_id==0)
	{
		if(ID==1)
		{

		PrintWriter value = new PrintWriter(new FileOutputStream(new File("current_img_values"+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		
		if(!freeze_state){
		
		//copy current_img_values into img_values
		String content4 = readFile("current_img_values.txt");
		File file4 =new File("img_values.txt");	
		FileWriter fw4 = new FileWriter(file4);
    	BufferedWriter bw4 = new BufferedWriter(fw4);
		bw4.write(content4);
		bw4.close();
		
		}
		
		if(record_21)
		{
		
		value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+image_id+".txt"),true)); //'true' flag for appending
		value.println(img_digest+"  "+image_id);
		value.close();
	
		}
			
		}
		else if(ID==2)
		{
		
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("current_img_values"+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		
		if(!freeze_state){
		
		//copy current_img_values into img_values
		String content4 = readFile("current_img_values.txt");
		File file4 =new File("img_values.txt");	
		FileWriter fw4 = new FileWriter(file4);
    	BufferedWriter bw4 = new BufferedWriter(fw4);
		bw4.write(content4);
		bw4.close();
		
		}
	
		if(record_31)
		{
		
		value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+image_id+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		}
			
		}
	}

	if(image_id==1)
	{
		if(ID==0)
		{
		
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("current_img_values"+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		if(!freeze_state){
		
		//copy current_img_values into img_values
		String content4 = readFile("current_img_values.txt");
		File file4 =new File("img_values.txt");	
		FileWriter fw4 = new FileWriter(file4);
    	BufferedWriter bw4 = new BufferedWriter(fw4);
		bw4.write(content4);
		bw4.close();
		
		}	
		
		if(record_12)
		{
		value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+image_id+".txt"),true)); //'true' flag for appending
		value.println(img_digest+"  "+image_id);
		value.close();
		}
			
			
		}
		else if(ID==2)
		{

		PrintWriter value = new PrintWriter(new FileOutputStream(new File("current_img_values"+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		
		if(!freeze_state){
		
		//copy current_img_values into img_values
		String content4 = readFile("current_img_values.txt");
		File file4 =new File("img_values.txt");	
		FileWriter fw4 = new FileWriter(file4);
    	BufferedWriter bw4 = new BufferedWriter(fw4);
		bw4.write(content4);
		bw4.close();
		
		}
	
		if(record_32)
		{
			
		value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+image_id+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		}
		
		}
	}
	
	if(image_id==2)
	{
		if(ID==0)
		{
		
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("current_img_values"+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		
		if(!freeze_state){

		//copy current_img_values into img_values
		String content4 = readFile("current_img_values.txt");
		File file4 =new File("img_values.txt");	
		FileWriter fw4 = new FileWriter(file4);
    	BufferedWriter bw4 = new BufferedWriter(fw4);
		bw4.write(content4);
		bw4.close();
		
		}
			
		if(record_13)
		{
		
		value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+image_id+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		}
				
		}
		else if(ID==1)
		{
		
		PrintWriter value = new PrintWriter(new FileOutputStream(new File("current_img_values"+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		
		if(!freeze_state){
		
		//copy current_img_values into img_values
		String content4 = readFile("current_img_values.txt");
		File file4 =new File("img_values.txt");	
		FileWriter fw4 = new FileWriter(file4);
    	BufferedWriter bw4 = new BufferedWriter(fw4);
		bw4.write(content4);
		bw4.close();
		
		}
	
		if(record_23)
		{
		
		value = new PrintWriter(new FileOutputStream(new File("img_values_recorded"+image_id+".txt"),true)); //'true' flag for appending
		value.println(img_digest+" "+image_id);
		value.close();
		}
				
		}
	
	}
	}

	@Override
	public void run() {
		while (processed_images < 100) {
			try {
				//Thread.sleep(160000);
				Thread.sleep(30000);
				first_time_marker++;
				System.out.println("First Time Marker before calling snapshot" + first_time_marker);
				
				if (ID==0) {	
		 file1 =new File("img_values_recorded1.txt");
       file2 =new File("img_values_recorded2.txt");
		FileWriter fw_1 = new FileWriter(file1);
		FileWriter fw_2 = new FileWriter(file2);
		fw_1.flush();
		fw_2.flush();
		} else if(ID==1) {
			 file1 =new File("img_values_recorded0.txt");
       file2 =new File("img_values_recorded2.txt");
		FileWriter fw_1 = new FileWriter(file1);
		FileWriter fw_2 = new FileWriter(file2);
		fw_1.flush();
		fw_2.flush();
		} else {
			 file1 =new File("img_values_recorded0.txt");
      file2 =new File("img_values_recorded1.txt");
		FileWriter fw_1 = new FileWriter(file1);
		FileWriter fw_2 = new FileWriter(file2);
		fw_1.flush();
		fw_2.flush();
		}

freeze_state = false;
snapshot_taken = false;

	//copy current_img_values into img_values
		String content6 = readFile("current_img_values.txt");
		File file6 =new File("img_values.txt");	
		FileWriter fw6 = new FileWriter(file6);
    	BufferedWriter bw6 = new BufferedWriter(fw6);
		bw6.write(content6);
		bw6.close();
		
       PrintWriter value = new PrintWriter(new FileOutputStream(new File("processed_images"+".txt"),false)); //'true' flag for appending
			value.println(processed_images);
			value.close();

				captureSnapshot();
					
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
      		}
	
	}
	}

	private void captureSnapshot() throws RemoteException, NotBoundException, UnknownHostException, InterruptedException, IOException {
		//recording it's own state
		
		System.out.println("First Time Marker" + first_time_marker);
		
		send_unlock=false;
		
		System.out.println("entered take snapshot function");
		//recording its ownstate done
		freeze_state=true;
		//starts recording
		record_12=true;
		record_13=true;
		snapshot_taken=true;

		state = "";
		
		state += selfName + " processed_images: " + processed_images;
		int ID;
		
		String hostName_1 = "";
		String connectToIP_1 = "";
		String hostName_2 = "";
		String connectToIP_2 = "";
		String channelValue="";
		String channelState = "";
					
				hostName_1 = processesID.get(1);
				connectToIP_1 = processes.get(hostName_1);
				hostName_2 = processesID.get(2);
				connectToIP_2 = processes.get(hostName_2);
				System.out.println("connectToIP_1:"+connectToIP_1);
				System.out.println("connectToIP_2:"+connectToIP_2);
				System.out.println("hostName_1:"+hostName_1);
				System.out.println("hostName_2:"+hostName_2);
				Registry r1 = LocateRegistry.getRegistry(connectToIP_1, portNo);
				Registry r2 = LocateRegistry.getRegistry(connectToIP_2, portNo);
				
				ChandyComms processObj_1 = (ChandyComms) r1
						.lookup("chandyprocess");
				ChandyComms processObj_2 = (ChandyComms) r2
						.lookup("chandyprocess");
				processObj_1.unfreezeState();
				processObj_2.unfreezeState();
				
				System.out.println("Initiator Sending marker to :"+hostName_1);
				processObj_1.sendMarker(new Marker(),0);
				
				System.out.println("Initiator Sending marker to :"+hostName_2);
				processObj_2.sendMarker(new Marker(),0);
				send_unlock=true;
				
		Thread.sleep(30000);
				
	}

	@Override
	public void unfreezeState() throws RemoteException,  UnknownHostException, NotBoundException, IOException{
	
	freeze_state=false;
	snapshot_taken=false;
	first_time_marker=0;
	
	//copy current_img_values into img_values
		String content5 = readFile("current_img_values.txt");
		File file5 =new File("img_values.txt");	
		FileWriter fw5 = new FileWriter(file5);
    	BufferedWriter bw5 = new BufferedWriter(fw5);
		bw5.write(content5);
		bw5.close();
		
       PrintWriter value = new PrintWriter(new FileOutputStream(new File("processed_images"+".txt"),false)); //'true' flag for appending
			value.println(processed_images);
			value.close();
	
	
	if (ID==0) {	
		 file1 =new File("img_values_recorded1.txt");
       file2 =new File("img_values_recorded2.txt");
		FileWriter fw_1 = new FileWriter(file1);
		FileWriter fw_2 = new FileWriter(file2);
		fw_1.flush();
		fw_2.flush();
		} else if(ID==1) {
			 file1 =new File("img_values_recorded0.txt");
       file2 =new File("img_values_recorded2.txt");
		FileWriter fw_1 = new FileWriter(file1);
		FileWriter fw_2 = new FileWriter(file2);
		fw_1.flush();
		fw_2.flush();
		} else {
			 file1 =new File("img_values_recorded0.txt");
      file2 =new File("img_values_recorded1.txt");
		FileWriter fw_1 = new FileWriter(file1);
		FileWriter fw_2 = new FileWriter(file2);
		fw_1.flush();
		fw_2.flush();
		}
	
	
	
	}
	
	@Override
	public void sendMarker(Marker mark, int sender_id) throws RemoteException,  UnknownHostException, NotBoundException, InterruptedException {
		
	Thread.sleep(4000);
		
	System.out.println("received marker from ID" + sender_id);
	String hostName = "";
	String connectToIP = "";
	m = mark;
	
    if(first_time_marker==0 && ID!=0)
	{
		send_unlock=false;
        first_time_marker++;
		
	if(ID==0){

	if(sender_id==1)
	{
		//Thread.sleep(3000);
		record_13=true;
		freeze_state=true;
		snapshot_taken=true;
		
		System.out.println("Made record_13 "+record_13);
	}
	
	if(sender_id==2)
	{
		
		//Thread.sleep(3000);
		record_12=true;
		freeze_state=true;
		snapshot_taken=true;
		
		System.out.println("Made record_12 "+record_12);
	}

}

else if(ID==1){

	if(sender_id==0)
	{
		//Thread.sleep(3000);
		record_23=true;
		freeze_state=true;
		snapshot_taken=true;
		
		System.out.println("Made record_23 "+record_23);		
	}
	
	if(sender_id==2)
	{
		//Thread.sleep(3000);
		record_21=true;	
		freeze_state=true;
		snapshot_taken=true;
		
		System.out.println("Made record_21 "+record_21);
	}

}

else if(ID==2){

	if(sender_id==0)
	{
		//Thread.sleep(3000);
		freeze_state=true;
		record_32 =true;
		snapshot_taken=true;

		System.out.println("Made record_32 "+record_32);
	}
	
	if(sender_id==1)
	{
		//Thread.sleep(3000);
		record_31=true;
		freeze_state=true;
		snapshot_taken=true;
	}


}
//Thread.sleep(8000);

this.sendMarkersToOtherProcesses();

}
else{
if(ID==0){

if(sender_id==1){
record_12=false;

System.out.println("Made record_12 "+record_12);
}
else if(sender_id==2){
record_13=false;

System.out.println("Made record_13 "+record_13);
}

}

else if(ID==1){
	
if(sender_id==2){
	
record_23=false;
System.out.println("Made record_23 "+record_23);

}
else if(sender_id==0){
	
record_21=false;
System.out.println("Made record_21 "+record_21);

}
}

else if(ID==2){
	
	if(sender_id==0){
record_31=false;
System.out.println("Made record_31 "+record_31);
	
	}
else if(sender_id==1){	

record_32=false;
System.out.println("Made record_32 "+record_32);

}

}

}
                                          			
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
				ChandyComms new_processObj = (ChandyComms) r
						.lookup("chandyprocess");
				System.out.println("Inside sendMarkersToOtherProcesses finction");
				System.out.println("Process:"+this.ID+" Sending Marker to process:"+x);
				new_processObj.sendMarker(new Marker(),this.ID);
				//Thread.sleep(8000);
	}

}
    send_unlock=true;
	System.out.println("Made send_unlock:"+send_unlock);
}
	
	@Override
	public void transmitImage(double img_digest, int money_sender_id) throws RemoteException,InterruptedException,FileNotFoundException,IOException {
		//Thread.sleep(4000);
		this.receiveImage(img_digest,money_sender_id);
	}
	
	}
