package info.pinlab.schnitt.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileNameManip {
    
    public static String getFileName(String fullpath){
        int index = fullpath.lastIndexOf('\\');
        return fullpath.substring(index+1, fullpath.length());
    }
    
    public static String getDirName(String fullpath){
        int index = fullpath.lastIndexOf('\\');
        return fullpath.substring(0, index+1);
    }
    
    static final String regex = "(\\[|\\])";
    /**
     * ignores case
     *
     * @param wavfilename
     * @return
     */
    public static List<String> getTextGridFileNameCandidates(String wavfilename){
        List<String> textgridfilesnames = new ArrayList<String>();
        
        textgridfilesnames.add(wavfilename.replaceAll(".wav",".TextGrid").replaceAll(regex, "_"));
        textgridfilesnames.add(wavfilename.replaceAll(".wav",".TEXTGRID").replaceAll(regex, "_"));
        textgridfilesnames.add(wavfilename.replaceAll(".wav",".textgrid").replaceAll(regex, "_"));
        textgridfilesnames.add(wavfilename.replaceAll(".WAV",".textgrid").replaceAll(regex, "_"));
        textgridfilesnames.add(wavfilename.replaceAll(".WAV",".TextGrid").replaceAll(regex, "_"));
        
        return textgridfilesnames;
    }

    
    public static String dirNameToTextGridFilePath(String dirName){
        String WavFileName = FileNameManip.getFileName(dirName);
        
        List<String> tgNameCandidates = FileNameManip.getTextGridFileNameCandidates(WavFileName);
        
        for(String tgNameCandidate: tgNameCandidates){
            String path = FileNameManip.getDirName(dirName) + tgNameCandidate;
//            System.out.println("PATH" + path);
            File filePath = new File(path);
            if(filePath.exists() && filePath.isFile()){
                return filePath.getAbsolutePath();
            }
        }
        return null; //-- no corresponding .textGrid
    }
}


