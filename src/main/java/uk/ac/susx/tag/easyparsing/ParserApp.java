package uk.ac.susx.tag.easyparsing;
import uk.ac.susx.tag.dependencyparser.Parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;



/**
 * Created by juliewe on 05/10/2015.
 * wraparound for Andy's dependency parser --> CONLL in --> CONLL out
 *a small change
 */


public class ParserApp extends EasyFileVisitor{

    private final Parser myparser;
    private final String indir;
    private final String outdir;
    private String inputformat;
    private String outputformat;
    private int inputlength;

    ParserApp(String indir) throws IOException {
        myparser=new Parser("full_wsj_penn_pos_stanford_dep");
        this.indir=indir;
        this.outputformat="id, form, lemma, pos, ner, head, deprel";
        if (this.indir.endsWith("sfd_parsed")){
            this.outdir=indir.replaceAll("sfd_parsed","parsed");
            this.inputformat="id, form, lemma, pos, head, deprel";
            this.outputformat=this.inputformat;

        } else {
            this.outdir = indir.replaceAll("tagged", "parsed");
            this.inputformat="id, form, lemma,pos,ner";
        }
        this.inputlength=this.inputformat.split(",").length;
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
            outfilename=outfilename.replaceAll("sfd_parsed","parsed");

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


            if (this.indir.endsWith("sfd_parsed")){
                File tempfile= new File("tempfile");
                try {
                    this.clean(infile, tempfile);
                } catch (IOException e){
                    e.printStackTrace();
                    System.exit(1);
                }
                infile=tempfile;

            }
            try {
                myparser.batchParseFile(infile, outfile, this.inputformat, this.outputformat, "");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.out.println("Ignoring files without conll extension "+filename);
        }
    }

    public void clean(File infile, File outfile) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(infile),"UTF-8"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile),"UTF-8"));

        String line;

        while((line=reader.readLine())!=null){
            writer.write(cleanLine(line.trim()));
            writer.newLine();
        }



        reader.close();
        writer.close();

    }

    private String cleanLine(String line){
        String[] fields = line.split("\t");
        if (fields.length<2) {
           return "";
        }
        else {
            if (fields.length == this.inputlength) {
                try {
                    Integer.parseInt(fields[0]);
                    Integer.parseInt(fields[4]);
                } catch (Exception e) {
                    System.err.println("Error line " + line);
                    e.printStackTrace();
                }
                return line;
            } else {
                int padding = this.inputlength - fields.length;
                while (padding > 0) {
                    line += "\t" + "-1";
                    padding--;
                }
                //System.err.println("Cleaned: "+line);
                return line;
            }
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