package info.pinlab.schnitt.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
 
//MEMO 
 
public class TextReader {
    
    
	/**
	 * 
	 * @param path
	 * @return  lines in the file
	 */
    static List<String> textToList(String path){
    	List<String> list = new ArrayList<String>();
    	if(path==null){
    		return list;
    	}
        try{
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
 
            String str;
 
            while((str = br.readLine()) != null){
                list.add(str);
            }
            br.close();
        }catch(FileNotFoundException e){
            System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }
        return list;
    }
    
    static double[] getMaxArray(List<String> list){
        List<Object> max = new ArrayList<Object>();
        for(int i = 0; i<list.size(); i++){
            if(((String) list.get(i)).startsWith("            xmax = ")){
                max.add(((String) list.get(i)).replaceAll("(\\s|xmax\\s=)", ""));
            }
        }
        double[] dmax = new double[max.size()];
        for(int i = 0; i < max.size(); i++){
            dmax[i] = Double.valueOf((String) max.get(i));
        }
        return dmax;
    }
    
    static double[] getMinArray(List<String> list){
        List<Object> min = new ArrayList<Object>();
        for(int i = 0; i<list.size(); i++){
            if(((String) list.get(i)).startsWith("            xmin = ")){
                min.add(((String) list.get(i)).replaceAll("(\\s|xmin\\s=)", ""));
            }
        }
        double[] dmin = new double[min.size()];
        for(int i = 0; i < min.size(); i++){
            dmin[i] = Double.valueOf((String) min.get(i));
        }
        return dmin;
    }
    
    
    //detecting "s", ignoring "<GB>" by regex
    
    static String[] getText(List<String> list){
        
        List<String> active = new ArrayList<String>();
        
        for(int i = 0; i<list.size(); i++){
            if(((String) list.get(i)).startsWith("            text = ")){
                active.add(((String) list.get(i)).replaceAll("(\\s+|text\\s+=|<GB>)", ""));
            }
        }
    
        String [] activearray = new String [active.size()];
        
        for(int i = 0; i<active.size(); i++){
            activearray[i] = (String) active.get(i);
        }
        return activearray;
    }
    
}    
 


