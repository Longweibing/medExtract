/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjectExtension;
import java.util.GregorianCalendar;

/**
 *
 * @author fortylashes
 */
public class LengthOfStay {
    String admitDate;
    String dischargeDate;
    GregorianCalendar gregAdDate = new GregorianCalendar();
    GregorianCalendar gregDisDate = new GregorianCalendar();
    Integer LengthOfStayDays;
    
    public LengthOfStay(){
    }
    
    public void setAdmitDate(String admD){
        this.admitDate = admD;
    }
    
    public void setDischargeDate(String disD){
        this.dischargeDate = disD;
    }
    
    public void parseStringDateAdmit(){
        //long numberOfMSInADay = 1000*60*60*24;
        try{
        String[] splitDate = this.admitDate.split("/");
        gregAdDate.set(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]));
        }
        catch(Exception e){
            
        }
    }
    
    public void parseStringDateDischarge(){
        try{
        String[] splitDate = this.dischargeDate.split("/");
        gregDisDate.set(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]));
        }
        catch(Exception e){
            
        }
    }
    
    public void getLOSDays(){
        try{
        long numberOfMSInADay = 1000*60*60*24;
        long span = this.gregDisDate.getTimeInMillis() - this.gregAdDate.getTimeInMillis();
        GregorianCalendar c3 = new GregorianCalendar();
        c3.setTimeInMillis(span);
        this.LengthOfStayDays = (int) Math.abs(Math.ceil(((float) c3.getTimeInMillis() / (float) numberOfMSInADay))); //3653
        }
        catch(Exception e){
            this.LengthOfStayDays = 0;
        }
    }
    
    public void findDates(String medRec){
        Integer stringCounter = 0;
        String dateLine = null;
        for(String para: medRec.split(System.getProperty("line.separator"))){
            /*if(para != "") stringCounter++;
            if(stringCounter.equals(2)) dateLine = para; break;
            */
            //System.out.println(para.split(" | ")[0].toString());
            if(isInteger(para.split(" | ")[0])){
                dateLine = para;
                break;
            }
        }
        String[] dateRow = dateLine.split(" | ");
        this.setDischargeDate(dateRow[9]);
        this.setAdmitDate(dateRow[22]);
        
        this.parseStringDateAdmit();
        this.parseStringDateDischarge();
        
        this.getLOSDays();
        
        
    }
    
    public Integer returnLOSDays(){
        return this.LengthOfStayDays;
    }
    
    public static boolean isInteger(String s) {
    try { 
        Integer.parseInt(s); 
    } catch(NumberFormatException e) { 
        return false; 
    }
    // only got here if we didn't return false
    return true;
}
}
