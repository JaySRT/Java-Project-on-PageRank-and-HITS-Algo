package com.company;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;


public class jay1001pgrk {
    static double const_d=0.85;
    static double [] pgrk_arr;
    static double target_err=1;
    static double curr_err=2;
    static double[] tm_pgrk_arr;
    static Integer no_vertex=null;
    static Integer no_edges=null;
    static ArrayList<ArrayList<Integer>> adjList2;
    static DecimalFormat decForm = new DecimalFormat("0.0000000");

    static void formAdjList(BufferedReader file, int no_vertex, int no_edges) throws IOException {
        String items;
        adjList2=new ArrayList<>();
        for (int i=0;i<no_vertex;i++){
            adjList2.add(new ArrayList<Integer>());
        }
        while((items=file.readLine())!=null) {
            String[] splitted_items = items.split(" ");
            adjList2.get(Integer.parseInt(splitted_items[0])).add(Integer.parseInt(splitted_items[1]));
        }
    }

    static void init_pageant(int no_vertex, int initialvalue){
        double[] values={ (1/Math.sqrt(no_vertex)),1/(no_vertex+0.0),0,1};
        pgrk_arr=new double[no_vertex];
        Arrays.fill(pgrk_arr,values[2+initialvalue]);
    }

    static  double calc_page_rank(int no_vertex,int iter){
        for (int i=0;i<no_vertex;i++){
            for(int j=0;j<adjList2.get(i).size();j++){
                tm_pgrk_arr[adjList2.get(i).get(j)]+=pgrk_arr[i]/(adjList2.get(i).size()+0.0);
            }
        }
        double max_error=-1;
        for (int i=0;i<no_vertex;i++){
            tm_pgrk_arr[i]=const_d*tm_pgrk_arr[i]+((1-const_d)/no_vertex);
            if(Math.abs(tm_pgrk_arr[i]-pgrk_arr[i])>max_error){
                max_error=Math.abs(tm_pgrk_arr[i]-pgrk_arr[i]);
            }
        }
        if (iter<=0){
            return max_error;
        }
        return 2;
    }

    static void print_page_rank(int iteration,boolean final_iteration_or_not){
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
        for(int i=0;i<pgrk_arr.length;i++){
            output_str+="P["+i+"]="+ decForm.format(pgrk_arr[i])+" ";
            if(no_vertex>10&&final_iteration_or_not){
                System.out.println("P ["+i+"] = "+ decForm.format(pgrk_arr[i])+" ");
            }
        }
        if(no_vertex<=10){
            System.out.println(output_str);}
        else if(final_iteration_or_not) {
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
        Integer iter=Integer.parseInt(args[0]);
        Integer initialvalues=Integer.parseInt((args[1]));
        String filename=args[2];
        //checking if all the input arguments are correct.
        if (initialvalues>1 || initialvalues<-2){
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
        BufferedReader filereader=new BufferedReader(inputFile);
        String vertex_edges= filereader.readLine();
        String[] splitted_items=vertex_edges.split(" ");
        no_vertex=Integer.parseInt(splitted_items[0]);
        no_edges=Integer.parseInt(splitted_items[1]);
        formAdjList(filereader,no_vertex,no_edges);
        init_pageant(no_vertex,initialvalues);
        if (iter<=0){
            if (iter==0){
                target_err=Math.pow(10.0,-5.0);
                iter=-1;
            }
            else{
                target_err=Math.pow(10.0,iter+0.0);}
            curr_err=1;
        }
        if (no_vertex>10){
            target_err=Math.pow(10.0,-5+0.0);
            iter=-1;
            init_pageant(no_vertex,-1);

        }
        tm_pgrk_arr=new double[no_vertex];
        print_page_rank(0,false);
        int curr_itr=1;
        while(iter!=0 && curr_err>=target_err){
            Arrays.fill(tm_pgrk_arr,0.0);
            curr_err=calc_page_rank(no_vertex,iter);
            pgrk_arr=tm_pgrk_arr.clone();
            print_page_rank(curr_itr,(curr_err <target_err || iter == 1) && no_vertex > 10);
            curr_itr++;
            iter--;
        }
    }
}
}