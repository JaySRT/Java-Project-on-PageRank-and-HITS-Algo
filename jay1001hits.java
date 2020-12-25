package com.company;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

public class jay1001hits {
    static int[][] adjMat=null;
    static double[] authority;
    static  double[] prev_auth;
    static double[] hub;
    static  double[] prev_hub;
    static double target_err=1;
    static double curr_err=2;
    static  double hub_sm_sq=0.0;
    static  double auth_sm_sq=0.0;
    static Integer no_vertex=null;
    static Integer no_edges=null;
    static DecimalFormat decFormat = new DecimalFormat("0.0000000");

    static int[][] formadjMat(BufferedReader file,int no_vertex,int no_edges) throws IOException {
        int[][] adjMat=new int[no_vertex][no_vertex];
        String items;
        while((items=file.readLine())!=null){
            String[] splitted_items=items.split(" ");
            adjMat[Integer.parseInt(splitted_items[0])][Integer.parseInt(splitted_items[1])]=1;
        }
        return adjMat;
    }
    static double[] set_hub_val(int initialvalue,int no_vertex){
        double[] values={ (1/Math.sqrt(no_vertex)),1/(no_vertex+0.0),0,1};
        double[] hub=new double[no_vertex];
        Arrays.fill(hub,values[2+initialvalue]);
        return hub;
    }
    static double[] set_auth_val(int initialvalue,int no_vertex){
        double[] values={ 1/Math.sqrt(no_vertex),1/(no_vertex+0.0),0,1};
        double[] authority=new double[no_vertex];
        Arrays.fill(authority,values[2+initialvalue]);
        return authority;
    }
    static void auth_upd(){
        auth_sm_sq=0.0;
        for(int i=0;i<adjMat.length;i++){
            double total=0.0;
            for (int j=0;j<adjMat[0].length;j++){
                total+=adjMat[j][i]*hub[j];
            }
            authority[i]=total;
            auth_sm_sq+=authority[i]*authority[i];
        }
    }
    static void hub_upd(){
        hub_sm_sq=0.0;
        for(int i=0;i<adjMat.length;i++) {
            double total = 0.0;
            for (int j = 0; j < adjMat[0].length; j++) {
                total += adjMat[i][j] * authority[j];
            }
            hub[i]=total;
            hub_sm_sq+=hub[i]*hub[i];
        }
    }

    static double scale_hub_auth(int iterations){
        double max=-1;
        for(int i=0;i<hub.length;i++){
            hub[i]=hub[i]/Math.sqrt(hub_sm_sq);
            authority[i]=authority[i]/Math.sqrt(auth_sm_sq);
            max=Math.max(Math.max(Math.abs(hub[i]-prev_hub[i]),Math.abs(authority[i]-prev_auth[i])),max);
        }
        if (iterations<=0){
            return max;
        }
        return 2;
    }

    static void print_hub_auth(int iteration,boolean final_iteration_or_not){
        String output_str="";
        if (iteration==0) {
            output_str += "Base : " + iteration + " :";
        }
        else {
            output_str += "Iter : " + iteration + " :";
            if(no_vertex>10&&final_iteration_or_not){
                System.out.println("Iter : " + iteration );
            }
        }
        for(int i=0;i<hub.length;i++){
            output_str+="A/H["+i+"]="+ decFormat.format(authority[i])+"/"+decFormat.format(hub[i])+" ";
            if(no_vertex>10&&final_iteration_or_not){
                System.out.println("A/H ["+i+"] = "+ decFormat.format(authority[i])+"/"+decFormat.format(hub[i])+" ");
            }
        }
        if(no_vertex<=10){
            System.out.println(output_str);}
        else if (final_iteration_or_not){
            System.out.println();
        }
    }
    public static void main(String[] args) throws IOException {
        FileReader inputFile=null;
        if(args.length!=3){
            System.out.println("Wrong Input arguments");
            return;
        }
        //Parsing the inputs
        Integer iterations=Integer.parseInt(args[0]);
        Integer intialvalues=Integer.parseInt((args[1]));
        String filename=args[2];

        //checking if all the input arguments are correct.
        if (intialvalues>1 || intialvalues<-2){
            System.out.println("initial value is not one of values(-2,-1,0,1)");
            return;
        }
        try
        {
            inputFile=new FileReader(filename);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File name:"+filename+" not found");
        }
        //reading the file to create adJ Matrix
        BufferedReader filereader=new BufferedReader(inputFile);
        String vertex_edges= filereader.readLine();
        String[] splitted_items=vertex_edges.split(" ");
        no_vertex=Integer.parseInt(splitted_items[0]);
        no_edges=Integer.parseInt(splitted_items[1]);
        adjMat=formadjMat(filereader,no_vertex,no_edges);
        //setting initial values for hubs and authority
        hub=set_hub_val(intialvalues, no_vertex);
        authority=set_auth_val(intialvalues,no_vertex);
        if (iterations<=0){
            if(iterations==0){
                target_err=Math.pow(10.0,-5+0.0);
                iterations=-1;
            }
            else{
                target_err=Math.pow(10.0,iterations+0.0);}
            curr_err=1;
        }

        if (no_vertex>10){
            target_err=Math.pow(10.0,-5+0.0);
            hub=set_hub_val(-1, no_vertex);
            authority=set_auth_val(-1,no_vertex);
            iterations=-1;
        }
        print_hub_auth(0,false);
        int current_iteration=1;
        //Running HITS Algo Steps
        while(iterations!=0&& curr_err>=target_err) {
            prev_auth=authority.clone();
            prev_hub=hub.clone();
            auth_upd();
            hub_upd();
            curr_err=scale_hub_auth(iterations);
            print_hub_auth(current_iteration, (curr_err < target_err || iterations == 1) && no_vertex > 10);
            current_iteration += 1;
            iterations--;

        }
    }
}
