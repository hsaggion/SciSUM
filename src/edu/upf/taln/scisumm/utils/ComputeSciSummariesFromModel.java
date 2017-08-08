/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class ComputeSciSummariesFromModel {
    

    
    
  //  public static String corpusLoc="/home/horacio/temp/div_all";
    public static String corpusLoc="/home/horacio/work/SciSUM-2017/TEST-DATA-VECS-RPS";
    public static String corpusOutLoc="/home/horacio/work/SciSUM-2017/GATE-DOCS-SUMMARIES";
    public static String modelLoc="/home/horacio/work/SciSUM-2017/MODELs-2017";
    public static String outDir;
    public static String summSetName="SciSUMM";
    public static String exportTO="/home/horacio/work/SciSUM-2017/AUTO-SUMMARIES";

    
    
    
    /*
    public static String corpusLoc="/home/horacio/work/SciSUM-2016/EVALUATE-TEST/TESTING-2016";
    public static String corpusOutLoc="/home/horacio/work/SciSUM-2016/EVALUATE-TEST/GATE-XML";
    public static String modelLoc="/home/horacio/work/SciSUM-2016/EVALUATE-TEST/MODELs";
    public static String outDir;
    public static String summSetName="SciSUMM";
    public static String exportTO="/home/horacio/work/SciSUM-2016/EVALUATE-TEST/SUMMARIES";
*/
    
    public ComputeSciSummariesFromModel() {
        
    }
    
    
    ArrayList<String> features;
    ArrayList<Double> weights;
    
    public void loadLRModel(String modelLoc) {
        BufferedReader reader;
        String line;
        String feature;
        String value;
        features=new ArrayList();
        weights=new ArrayList();
        
        int pos;
        try {
            reader=new BufferedReader(new FileReader(modelLoc));
            while((line=reader.readLine()
                    )!=null) {
                
                pos=line.indexOf("\t");
                feature=line.substring(0, pos);
                value=line.substring(pos+1,line.length());
                features.add(feature);
                weights.add(new Double(value));
                System.out.println(feature+"\t"+value);
            }
           
            
           
            
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        
        
    }
    
    public static void main(String[] args) {
        
        String[] models=new String[9];
        
        models[0]=modelLoc+File.separator+"gs_acl_com_vec_max.model";
        models[1]=modelLoc+File.separator+"gs_acl_hum_vec_max.model";
        models[2]=modelLoc+File.separator+"gs_acl_abs_vec_max.model";
        models[3]=modelLoc+File.separator+"gs_summa_com_vec_max.model";
        models[4]=modelLoc+File.separator+"gs_summa_hum_vec_max.model";
        models[5]=modelLoc+File.separator+"gs_summa_abs_vec_max.model";
        models[6]=modelLoc+File.separator+"gs_google_com_vec_max.model";
        models[7]=modelLoc+File.separator+"gs_google_hum_vec_max.model";
        models[8]=modelLoc+File.separator+"gs_google_abs_vec_max.model";
                
        
        String[] outDirs=new String[9];
        outDirs[0]="acl_com";
        outDirs[1]="acl_hum";
        outDirs[2]="acl_abs";
        outDirs[3]="summa_com";
        outDirs[4]="summa_hum";
        outDirs[5]="summa_abs";
        outDirs[6]="google_com";
        outDirs[7]="google_hum";
        outDirs[8]="google_abs";
        
        
        
        ComputeSciSummariesFromModel computator;
        
        File inDir=new File(corpusLoc);
        File[] flist=inDir.listFiles();
        String fname;
        String floc;
        Document doc;
        PrintWriter pw;
        File auxDir;
        try {
            Gate.init();
         ;
            for(int m=0;m
                  <9 ;m++) {
               computator=new ComputeSciSummariesFromModel();
                computator.initSummarizer();
                computator.loadLRModel(models[m]);
                outDir=outDirs[m];
                    for(int f=0;f<flist.length ;f++) {
                        fname=flist[f].getName();
                        floc=flist[f].getAbsolutePath();
                        fname=fname.substring(0, 7);

                        doc=Factory.newDocument(new URL("file:///"+floc));
                        computator.summarizeDocument(doc);
                        auxDir=new File(corpusOutLoc+File.separator+outDir);
                        if(!auxDir.exists()) auxDir.mkdir();
                        pw=new PrintWriter(new FileWriter(corpusOutLoc+File.separator+outDir+File.separator+fname+".xml"));
                        pw.println(doc.toXml());
                        pw.flush();
                        pw.close();
                        Factory.deleteResource(doc);

                    }
            }
        
        } catch(GateException ge) {
            
            ge.printStackTrace();
        } catch (MalformedURLException ex) {
           ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(ComputeSciSummariesFromModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
       
    }
    
    
    
    summa.SimpleSummarizer summarizer ;
    summa.summarizer.ExportSelectedSentences exporter;
  
    public void initSummarizer() {
        
        try {
            summarizer = new summa.SimpleSummarizer();
            exporter= new summa.summarizer.ExportSelectedSentences();
            summarizer.init();
            exporter.init();
          
        } catch (ResourceInstantiationException ex) {
           ex.printStackTrace();
        }
     
    }
    

    
    public void summarizeDocument(Document doc) {
        
        File auxDir;
        try {
            
            
            
            String fname;
            // only sentences after the first section
            
            AnnotationSet sentences;
            AnnotationSet tokens;
            summarizer.setScoreOnly(Boolean.FALSE);
            summarizer.setWordAnn("Token");
            summarizer.setSumSetName(summSetName);
            summarizer.setCompression(new Integer(250));
            summarizer.setSentCompression(Boolean.FALSE);
            summarizer.setSentAnn("Sentence");
            summarizer.setNewDocument(Boolean.FALSE);
            summarizer.setSumFeatures(features);
            summarizer.setSumWeigths(weights);
           
            
            fname=doc.getName();
            String key;
            Integer targetSize;
            System.out.println("Computing "+fname+"...");
        
           
               
               
                summarizer.setAnnSetName("Analysis");
                summarizer.setDocument(doc);
                summarizer.execute();
                exporter.setDocument(doc);
                exporter.setAnnotationSet(summSetName);
      
                exporter.setAnnotationType("Sentence");
                auxDir=new File(exportTO+File.separator+outDir);
                if(!auxDir.exists()) auxDir.mkdir();
                exporter.setDirName(new File(exportTO+File.separator+outDir));
                exporter.execute();
            
            
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
          
             
                
    
    
           
        
        
    }
    
}
