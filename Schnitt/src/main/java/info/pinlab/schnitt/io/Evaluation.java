package info.pinlab.schnitt.io;




import java.util.List;

import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.Interval;
import info.pinlab.snd.trs.Tier.Type;
import info.pinlab.snd.trs.VadErrorTier;
import info.pinlab.snd.vad.VadError;

public class Evaluation {
    
    
//    public void processFile(File dir) throws IOException{};
//    public void processDir(File dir);
    
    
    public static void main(String[] args) throws Exception{
    
    //target path
    String path = "C:\\Users\\Naoi\\Desktop\\VAD-ay2015-uw-pre-Working\\1252652_ay2015-uw-pre-TaskSet05_003_1__20150824-150917_.TextGrid";    
    
    List<String> list =  TextReader.textToList(path);
 
    //creating arrays consisting max, mind and text
 
    double [] dmax = TextReader.getMaxArray(list);
    double [] dmin = TextReader.getMinArray(list);
    String [] text = TextReader.getText(list);
    
    
    BinaryTier target = new BinaryTier(Type.TARG);
    
    target.addInterval(dmin[0],dmax[dmax.length-1], false);
    for(int i=0; i<text.length;i++){
        if(text[i].equals("\"s\"")) //"s" means speaking(active)
        target.addInterval(dmin[i],dmax[i], true);
    }
    
    
    System.out.println(target);
    System.out.println("Number of intervals: "+ text.length);
 
 
    VadErrorTier tier = new VadErrorTier(target, target);
    System.out.println(tier);
    
    
 
 
    //calculating penalties
    
    double error = 0;
    
    for(int i = 0 ; i < tier.size() ; i++){
        Interval<VadError> interval = tier.getIntervalX(i);
        VadError err = interval.label;
        double dur = interval.endT - interval.startT;
        
        switch(err){
        case TP:
            break;
        case TN:
            break;
        case WC:
            error += dur;
            break;
        case NDS_1:
            error += dur;
            break;
        case NDS_2:
            error = error + dur;
            break;
        case FEC:
            error = error + Math.pow(dur, 2);
            break;
        case REC:
            error = error + Math.pow(dur, 2);
            break;
        case HEAD:
            error = error + dur;
            break;
        case TAIL:
            error = error + dur;
            break;
        default:
            break;
        }
    }
    
    
    
    System.out.println();
    
    System.out.println("Evaluation (smaller is better)");
    System.out.println(error);
    
    }
    
}



