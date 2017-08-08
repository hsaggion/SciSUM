/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class MergeCitanceLines {



public static String locAnnotations="/home/horacio/temp/SciSUM-2016/output_annotations/run1";
public static String outLocAnnotations="/home/horacio/temp/SciSUM-2016/output_annotations/run1_one_line";


public static String processFile(String cluster) {
    String output="";
    String line;
    Map<String,ArrayList<String>> refNums=new TreeMap();
    Map<String,ArrayList<String>> refSents=new TreeMap();
    Map<String,ArrayList<String>> refFacets=new TreeMap();
    Map<String,String> citanceStart=new TreeMap();
    Map<String,String> citanceEnd=new TreeMap();
    try {
        BufferedReader reader;
        
        reader=new BufferedReader(new FileReader(locAnnotations+File.separator+cluster));
        int pos;
        int pos1;
        
        String auxLine;
        String citanceNumber;
        String refOffset;
        String facet;
        String referenceText;
        String prefixCitance;
        String posfixCitance;
        ArrayList<String> refs;
        ArrayList<String> facets;
        ArrayList<String> sents;
        while((line=reader.readLine())!=null) {
            if(line.startsWith("PROB:")) {
                pos=line.indexOf("|");
                line=line.substring(pos+1, line.length());
               
            }
            System.out.println(line);
            auxLine=line.replaceAll("Citance Number: ","");
            pos=auxLine.indexOf(" |");
            citanceNumber=auxLine.substring(0, pos);
            System.out.println(citanceNumber);
            pos=line.indexOf("Reference Offset: ["); 
            
            
            prefixCitance=line.substring(0, pos);
            
            refOffset=line.substring(pos+19,line.length());
            pos=refOffset.indexOf("]");
            refOffset=refOffset.substring(0, pos);
            System.out.println(refOffset);
            pos=line.indexOf("Discourse Facet: ");

            pos1=line.indexOf(" | Annotator");
            posfixCitance=line.substring(pos1, line.length());
            
            facet=line.substring(pos+17, pos1);
            System.out.println(facet);
            
            pos=line.indexOf("Reference Text: ");
            pos1=line.indexOf(" | Discourse Facet: ");
            referenceText=line.substring(pos+16, pos1);
            System.out.println(referenceText);
             
            System.out.println("PREFIX "+prefixCitance);
            System.out.println("POSFIX "+posfixCitance);
            
            
            // update tables
          
             if(refNums.containsKey(citanceNumber)) {
                 refs=refNums.get(citanceNumber);
             } else  {
                 refs=new ArrayList();
             }
             refs.add(refOffset);
             refNums.put(citanceNumber,refs);
             
 
    
            if(refSents.containsKey(citanceNumber)) {
                sents=refSents.get(citanceNumber);
            } else {
                sents=new ArrayList();
            }
            sents.add(referenceText);
            refSents.put(citanceNumber, sents);
    
            if(refFacets.containsKey(citanceNumber)) {
                facets=refFacets.get(citanceNumber);
            } else {
                facets=new ArrayList();
            }
            facets.add(facet);
            refFacets.put(citanceNumber, facets);
            
            citanceStart.put(citanceNumber, prefixCitance);
            citanceEnd.put(citanceNumber,posfixCitance);
            
        }
        
        
        // list citances
        System.out.println("*********************");
        
        String theRefs;
        String theSents;
        Map<String,Integer> facetCount=new TreeMap();
        int count;
        
        for(String citance : citanceStart.keySet()) {
        //    System.out.println("CITANCE "+citance);
            
        //    System.out.println(citanceStart.get(citance));
            
            refs=refNums.get(citance);
            theRefs="";
            for(String val : refs) {
                theRefs=theRefs+"\'"+val+"\' ";
            }
            theRefs="["+theRefs.replaceAll("\' \'", "\' , \'")+"]";
         //   System.out.println(theRefs);
            facets=refFacets.get(citance);
            // find best facet
            for(String val : facets) {
                if(facetCount.containsKey(val)) {
                    count=facetCount.get(val).intValue();
                } else {
                    count=0;
                }
                count++;
                facetCount.put(val, new Integer(count));

            }
            
            // find max
            int max=0;
            String best_cat="";
            for(String val: facetCount.keySet()) {
                count=facetCount.get(val).intValue();
                if(count>max) {
                    max=count;
                    best_cat=val;
                }
                
            }
          //  System.out.println(best_cat);
            
            sents=refSents.get(citance);
            theSents="";
             for(String val : sents) {
                theSents=theSents+val+" ";
            }
            
        //    System.out.println(theSents);
         //   System.out.println(citanceEnd.get(citance));
            
            System.out.println("=========================");
            
            System.out.print(citanceStart.get(citance));
            System.out.print("Reference Offset: "+theRefs);
            System.out.print(" | Reference Text: "+theSents);
            System.out.print(" | Discourse Facet: "+best_cat);
            System.out.println(citanceEnd.get(citance));
            
            System.out.println("=========================");
          
            
            output=output+citanceStart.get(citance);
            output=output+"Reference Offset: "+theRefs;
            output=output+" | Reference Text: "+theSents;
            output=output+" | Discourse Facet: "+best_cat;
            output=output+citanceEnd.get(citance)+"\n";
        }
        
    } catch (FileNotFoundException ex) {
        ex.printStackTrace();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    
    return output;
}

public static void main(String[] args) {
    File inDir=new File(locAnnotations);
    File[] files=inDir.listFiles();
    PrintWriter pw;
    String fname;
    for(File file : files) {
        fname=file.getName();
        if(fname.endsWith("ANNV3.txt")) {
            try {
                pw=new PrintWriter(new FileWriter(outLocAnnotations+File.separator+fname));
                pw.print(processFile(fname));
                pw.flush();
                pw.close();
            } catch (IOException ex) {
               ex.printStackTrace();
            }
        }
            
        
    }
    
    
   
}

    
}
