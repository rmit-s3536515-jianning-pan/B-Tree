import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class A2_Task3 {
 // java A2_Task3 dbload search pagesize
 // java A3_Task3 -r from to pagesize
 public static final String FILE_PREFIX = "heap.";
	public static List<DataRecord> records = new ArrayList<>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	 if(args.length==3) {
	  if(args[0].equals("dbload") && Part3.isNumeric(args[2]) && Part3.isNumeric(args[1])) {
	   int pagesize = Integer.parseInt(args[2]);
	   String inputfile = FILE_PREFIX + pagesize;
	   File f = new File(inputfile);
	   if(f.exists()) {
	    equalitySearch(inputfile,Integer.parseInt(args[1]));
	   }
	   else {
	    System.out.println("The file with pagesize of "+pagesize + " is not available");
	   }
	  }
	  else {
	   System.out.println("Incorrect Parameters are typed! Please follow : dbload text pagesize");
	  }
	 }
	 else if (args.length==4) {
	  if(args[0].equals("-r") && Part3.isNumeric(args[3]) && Part3.isNumeric(args[1]) && Part3.isNumeric(args[2])) {
    int pagesize = Integer.parseInt(args[3]);
    String inputfile = FILE_PREFIX + pagesize;
    File f = new File(inputfile);
    if(f.exists()) {
     rangeSearch(inputfile,Integer.parseInt(args[1]),Integer.parseInt(args[2]));
    }
    else {
     System.out.println("The file with pagesize of "+pagesize + " is not available");
    }
   }
   else {
    System.out.println("Incorrect Parameters are typed! Please follow : -r from to pagesize");
   }
  }
  else {
   
   System.out.println("Insufficent parameters are provided!");
   System.exit(1);
   
  }
	}
	
	public static void equalitySearch(String inputfile,int searchkey) {
int f = Math.multiplyExact(BPlusTree.BRANCH_FACTOR, 250);
  
  BPlusTree<Integer, DataRecord > b = new BPlusTree<>(f);
  FileInputStream fis = null;
ObjectInputStream is = null;
try {
 fis = new FileInputStream(inputfile);
 is = new ObjectInputStream(fis);
 Object obj = null;
 
 while((obj=is.readObject())!=null) {
  
  if(obj instanceof DataRecord) {
   DataRecord record = (DataRecord) obj;
   records.add(record);
   b.insertValue(record.getDeviceId(), record);
  }
  
 }
}catch(Exception e) {
 e.printStackTrace();
}

 try {
  fis.close(); 
  is.close();
 } catch (IOException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
 }
 long start = System.nanoTime();
 b.search(searchkey);
 System.out.println( (System.nanoTime() - start) + " nano seconds");
 

	}
	
	public static void rangeSearch(String inputfile,int from , int to) {
int f = Math.multiplyExact(BPlusTree.BRANCH_FACTOR, 250);
  
  BPlusTree<Integer, DataRecord > b = new BPlusTree<>(f);
  FileInputStream fis = null;
ObjectInputStream is = null;
try {
 fis = new FileInputStream(inputfile);
 is = new ObjectInputStream(fis);
 Object obj = null;
 
 while((obj=is.readObject())!=null) {
  
  if(obj instanceof DataRecord) {
   DataRecord record = (DataRecord) obj;
   records.add(record);
   b.insertValue(record.getDeviceId(), record);
  }
  
 }
}catch(Exception e) {
 e.printStackTrace();
}

 try {
  fis.close(); 
  is.close();
 } catch (IOException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
 }
 long start = System.nanoTime();
 b.rangeSearch(from,to);
 System.out.println( (System.nanoTime() - start) + " nano seconds");
	}
	

}
	
	
