/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.output;

import gate.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author horacio
 */
public class LineAndProb {
    
    String line;
    Double prob;
    public LineAndProb(String l, Double p) {
        line=l;
        prob=p;
        
    }
    
    public static Comparator probComparator = new Comparator() {
                public int compare(Object o1, Object o2) {

                       Double d1,d2;
                       LineAndProb a1 = (LineAndProb) o1;
                       LineAndProb a2 = (LineAndProb) o2;
                       d1 = (Double) a1.prob;
                       d2 = (Double) a2.prob;
                       return d2.compareTo(d1);
                }

     }
     ;
    
    public static void main(String[] args) {
        
        ArrayList<LineAndProb> list=new ArrayList();
        list.add(new LineAndProb("XXX",new Double(0.5)));
         list.add(new LineAndProb("YYY",new Double(1.0)));
          list.add(new LineAndProb("ZZZ",new Double(1.0)));
           list.add(new LineAndProb("WWW",new Double(0.52)));
           Collections.sort(list,probComparator);
           for(LineAndProb ele : list) {
               
               System.out.println(ele.line+" = "+ele.prob);
           }
        
    }
    
    
}
