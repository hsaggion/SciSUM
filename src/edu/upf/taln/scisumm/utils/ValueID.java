/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;


import gate.Annotation;
import gate.FeatureMap;
import java.util.Comparator;
import summa.constants.Constants;

/**
 *
 * @author UPF
 */
public class ValueID {
    
     public static Comparator valueComparator() {

      Comparator comparator= new Comparator() {
                  public int compare(Object o1, Object o2) {

                         ValueID a1 = (ValueID) o1;
                         ValueID a2 = (ValueID) o2;
                        
                         Double s1 = a1.value;
                         Double s2 = a2.value;

                         double v1=s1.doubleValue();
                         double v2=s2.doubleValue();
                         if(v1==v2) {
                          return 0;
                         } else if(v1<v2) {
                            return 1;
                         } else {
                            return -1;
                         }

                  }

       }
       ;

       return comparator;

    }
    
    Double value;
    Integer id;
    public ValueID(Integer i, Double v) {
        value=v;
        id=i;
    }
    public Integer getID() {
        return id;
    }
    public Double getValue() {
        return value;
    }
    
    
}
