package uk.ac.susx.tag.easyparsing;
import uk.ac.susx.tag.classificationframework.Util;
import uk.ac.susx.tag.classificationframework.datastructures.DependencyTree;
import uk.ac.susx.tag.classificationframework.datastructures.Document;
import uk.ac.susx.tag.classificationframework.datastructures.Instance;
import uk.ac.susx.tag.classificationframework.featureextraction.documentprocessing.TweetTagConverter;
import uk.ac.susx.tag.classificationframework.featureextraction.pipelines.FeatureExtractionPipeline;
import uk.ac.susx.tag.dependencyparser.Parser;
import uk.ac.susx.tag.dependencyparser.datastructures.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


/**
 * Created by juliewe on 05/10/2015.
 * wraparound for Andy's dependency parser --> CONLL in --> CONLL out
 *a small change
 */


public class ParserApp extends EasyFileVisitor{

    private final Parser myparser;
    private final String indir;
    private final String outdir;

    ParserApp(String indir) throws IOException {
        myparser=new Parser("full_wsj_penn_pos_stanford_dep");
        this.indir=indir;
        this.outdir=indir.replaceAll("tagged","parsed");
        if (Files.notExists(Paths.get(outdir))){
            Files.createDirectories(Paths.get(outdir));
        }
    }


    public void help(){

    }
    public void processFile(String filename) {
        //filename is path, only process conll files
        //ensure that directory hierarchy has been built before creating output file

        if (filename.endsWith("conll")) {
            File infile = new File(filename);
            String outfilename = filename.replaceAll("tagged", "parsed");

            String[] outparts=outfilename.split("/");
            String outdir=outparts[0];
            for (int i=1;i<outparts.length-1;i++){
                outdir+="/"+outparts[i];
            }
            if(Files.notExists(Paths.get(outdir))){
                try {
                    Files.createDirectories(Paths.get(outdir));
                } catch (IOException e) {
                    System.err.println("Cannot create directory "+outdir);
                    e.printStackTrace();
                }
            }

            File outfile = new File(outfilename);

            try {
                myparser.batchParseFile(infile, outfile, "id, form, lemma, pos, ner", "id, form, lemma, pos, ner, head, deprel", "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Ignoring files without conll extension "+filename);
        }
    }

    public static void main(String[] args){


        if (args.length<1){
            System.out.println("Required inputdir as argument");
            System.exit(-1);
        }
        try {
            ParserApp myapp = new ParserApp(args[0]);

        if (args.length==2){
            myapp.processFile(String.valueOf(Paths.get(args[0], args[1])));
        } else {
            try {
                myapp.processDir(myapp.indir);
            } catch (IOException e){
                System.err.println("Error processing directory "+myapp.indir);
                e.printStackTrace();
            }
        }
        } catch (IOException e){
            System.err.println("Error starting parser");
            e.printStackTrace();
        }
    }
}