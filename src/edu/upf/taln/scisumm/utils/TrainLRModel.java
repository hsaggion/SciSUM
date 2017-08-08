/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author horacio
 */
public class TrainLRModel {
    
    
    
    public static String outLoc="/home/horacio/work/SciSUM-2016/EVALUATE-TEST/MODELs";
    public static String inLoc="/home/horacio/work/SciSUM-2016/EVALUATE-TEST/ARFFs";
    
     public void writeLRModel(String outName) {
        PrintWriter pw;
        try {
            pw=new PrintWriter(new 
            FileWriter(outLoc+File.separator+outName+".model"));
            for(int f=0;f<features.length;f++) {
                
                pw.println(features[f]+"\t"+weights[f]);
                pw.flush();
                
            }
            pw.close();
         } catch(IOException ioe) {
             ioe.printStackTrace();
         }

        
    }
    
    String[] features;
    Double[] weights;
    
    public void trainLRModel(String outName) {
         
        try {
            Instances mydata=ConverterUtils.DataSource.read(inLoc+File.separator+outName+".arff");
            LinearRegression lr=new LinearRegression();
            String[] options = new String[1];
            options[0]="";
            lr.setOptions(options);
            mydata.setClassIndex(mydata.numAttributes()-1);
            
            lr.buildClassifier(mydata);
            double[] coheficients=lr.coefficients();
            weights=new Double[coheficients.length-2];
            features=new String[coheficients.length-2];
            // all minus class and independent constant
            for(int c=0;c<coheficients.length-2;c++) {
                weights[c]=new Double(coheficients[c]);
                features[c]=mydata.attribute(c).name();
            }
            Evaluation evaluate=new Evaluation(mydata);
          
            evaluate.evaluateModel(lr, mydata);
            System.out.println(evaluate.toSummaryString());

         
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
        
    public static void main(String[] args) {
        TrainLRModel train;
        
        File inDir=new File(inLoc);
        File[] flist=inDir.listFiles();
        String fname;
        
        for(int f=0;f<flist.length;f++) {
            fname=flist[f].getName();
            if(fname.contains("max") && fname.contains(".arff")) {
                fname=fname.replaceAll(".arff", "");
                System.out.println(fname+"...");
                train=new TrainLRModel();
                train.trainLRModel(fname);
                train.writeLRModel(fname);
            }
        }
        
    }
    
    
}
