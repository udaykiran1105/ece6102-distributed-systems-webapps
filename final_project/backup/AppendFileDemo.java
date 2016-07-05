import java.io.*;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.Character;
import java.lang.Object;
import java.nio.charset.StandardCharsets;

public class AppendFileDemo
{
   public static void main( String[] args )
   {	
      try{
    	
      
        //Specify the file name and path here
    	File file3 =new File("img_values.txt");
      File file1 =new File("img_values_recorded0.txt");
      File file2 =new File("img_values_recorded1.txt");

      
      if(!file3.exists()){
         file3.createNewFile();
      }


    	//Here true is to append the content to file
    	FileWriter fw3 = new FileWriter(file3,true);
    	//BufferedWriter writer give better performance
    	BufferedWriter bw3 = new BufferedWriter(fw3);
      if(file1.exists()){
        // String content1 = readFile("img_values_recorded0.txt", Charset.defaultCharset());
        String content1 = readFile("img_values_recorded0.txt");
		bw3.write(content1);
		FileWriter fw1 = new FileWriter(file1);
		fw1.flush();
      }

      if(file2.exists()){
        String content2 = readFile("img_values_recorded1.txt");
        bw3.write(content2);
		FileWriter fw2 = new FileWriter(file1);
		fw2.flush();
      }

    	//Closing BufferedWriter Stream
    	bw3.close();

	System.out.println("Data successfully appended at the end of file");

      }catch(IOException ioe){
         System.out.println("Exception occurred:");
    	 ioe.printStackTrace();
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
   
}