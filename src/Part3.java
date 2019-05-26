import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Part3 {


	
//	private static List<DataRecord> records = new ArrayList<DataRecord>();
	private static List<Page> pages = new ArrayList<>();
	private static int count = 0;
	private static int pagesize;
	private static long timeelapsd;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filename = null;
		String outputfilename = null;
		if(args.length!=4) {
			System.out.println("Please follow : dbload -p pagesize datafile");
			System.exit(1);
		}else {
			if(args[0].equals("dbload") && args[1].equals("-p")) {
				
				if(isNumeric(args[2])) { // the page size should be int
					String[] checkFile = args[3].split("\\.");
					if(checkFile.length==2 && checkFile[1].equalsIgnoreCase("csv")) {
						pagesize = Integer.parseInt(args[2]);
						filename = args[3];
						outputfilename = "heap."+pagesize;
						write(filename,outputfilename); // write the data 
						System.out.println("There are  " +pages.size() + " Pages");
						System.out.println("There are " + count + " records loaded to heap file");
						System.out.println("The time taken to load the data to heap file is " + timeelapsd + " milliseconds !");
						System.out.println("The number of record in page numbe 1  : " + pages.get(1).getRecords().size() );
						for(Page p : pages) {
							System.out.println(p.getRecords().size());
						}
					}
					else {
						System.out.println("file name is not correcet! Should be CSV FILE");
					}
				}
				else System.out.println("Please follow : dbload -p pagesize datafile");
				
			}
			else System.out.println("Please follow : dbload -p pagesize datafile");
			
		}

	}
	
	
	public static void write(String filename , String outputfilename) {

		Pattern pattern = Pattern.compile(",");
		
		long start = System.currentTimeMillis();
		try(BufferedReader in =new BufferedReader(new FileReader(filename));){
			
			FileOutputStream fos = new FileOutputStream(outputfilename);
			ObjectOutputStream out = new ObjectOutputStream(fos);

			
			Scanner sc = new Scanner(in);
			String line = "";
			
			int pagecount = 1;
			int perPageLength = 0;
			List<DataRecord> records = new ArrayList<DataRecord>();
			while(sc.hasNextLine()) {
				
				line = sc.nextLine();
				
				String[] record = line.split(",");
				 if(record[0].equalsIgnoreCase("deviceId")) {
					 continue;
				 }
				 
				 DataRecord dr = new DataRecord(Integer.parseInt(record[0]),record[1], record[2], Long.parseLong(record[3]), record[4],
							record[5], record[6], Integer.parseInt(record[7]), record[8], record[9], record[10],Integer.parseInt(record[11]), record[12]);
					
				
				perPageLength += dr.getRecordLength();
				
				if(perPageLength > pagesize) {  // if the record is added , the page will be over 
					pages.add(new Page(pagecount++,new ArrayList<DataRecord>(records)));
					count +=records.size();
//					System.out.println(pages.get(0).getRecords().size());
					records.clear();
					perPageLength = 0;

					out.writeObject("\n");
					writeRecord(out, dr);

					out.writeObject("\n");
					out.reset(); // fix the out of memory issue
					records.add(dr);
				}
				else {
					
					if(!sc.hasNextLine()) { // if no more line , eof file
						records.add(dr);
						
						pages.add(new Page(pagecount++,new ArrayList<DataRecord>(records)));
						count +=records.size();
						
//						records.clear();
						perPageLength = 0;
						writeRecord(out, dr);

						out.writeObject("\n");
						out.reset(); // fix the out of memory issue
						
					}
					else {
						writeRecord(out, dr);
						

						out.writeObject("\n");

						records.add(dr);
					}
					
				}
				
				out.flush();

			}
			out.writeObject(null); // it is used for skipping the eof exception for reading purpose
			out.close();
			fos.close();
			
			long end = System.currentTimeMillis();
			timeelapsd = (end - start);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void readFile(String outputfilename) {
		//reading the file
		
		FileInputStream fin;
		ObjectInputStream ois;
		try {
			fin = new FileInputStream(outputfilename);
			ois = new ObjectInputStream(fin);
//			int count = 1;
			Object obj = null;
			while ((obj = ois.readObject()) != null) {
				
			      if (obj instanceof DataRecord) {
			        System.out.print(((DataRecord) obj).toString());

			      }
			      else {
			    	  System.out.print(obj);
			      }

			   }
			
			ois.close();
			fin.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void writeRecord(ObjectOutputStream raf,DataRecord record) {
		try {
			
			raf.writeObject(record);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static boolean isNumeric(String strNum) {
	    try {
	        int d = Integer.parseInt(strNum);
	    } catch (NumberFormatException | NullPointerException nfe) {
	        return false;
	    }
	    return true;
	}
	
	public static Timestamp getTimestamp(String value) {
		DateFormat inputdatetimeformat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		 DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	        
				try {
					Date date = inputdatetimeformat.parse(value);		
					
					Timestamp sqltimestamp = Timestamp.valueOf(outputformat.format(date));

					return sqltimestamp;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				return null; // if exception
	}

}
