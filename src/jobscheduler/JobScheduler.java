/**
 COMP496 - 11am - CSUN Spring 2017
 Project 2 - JobScheduler
 Team Members:
 Mikael A. Mendoza
 Jonathan Villegas
 */

package jobScheduler;

import java.util.ArrayList;
import java.util.Arrays;

public class JobScheduler
{
    private int nJobs;
    private Job[]  jobs;
    private static Schedule best = new Schedule();  //keeps track of Best Schedule for Brutal Force solution

    public JobScheduler( int[] joblength, int[] deadline, int[] profit)
    {
        //Set nJobs
        nJobs = joblength.length;

        //Fill jobs array. The kth job entered has JobNo = k;
        jobs = new Job[nJobs];
        for(int i = 0; i < nJobs; i++){
            jobs[i] =  new Job(i, joblength[i], deadline[i], profit[i]);
        }
    }

    public void printJobs()  //prints the array jobs
    {
        for(int i = 0; i < nJobs; i++){
            System.out.print("\n" + jobs[i].toString());
        }
    }

    //Brute force. Try all n! orderings. Return the schedule with the most profit
    public Schedule bruteForceSolution()
    {
        Job[] jobList = jobs.clone();

        Schedule BFSchedule = new Schedule();
        for(int i = 0; i < nJobs; i++)
        {
            jobList[i].finish = -1;
            jobList[i].start = -1;
        }

        ArrayList<Job> arr = new ArrayList<Job>(Arrays.asList(jobList));
        BFSchedule.addMultiple( permute(arr, 0));   //call to permute() to recursively evaluate all n! orderings
        BFSchedule = createSchedule(BFSchedule.getJobs());

        return BFSchedule;
    }


    public Schedule makeScheduleEDF()
    //earliest deadline first schedule. Schedule items contributing 0 to total profit last
    {
        Job[] sortedJobs = sortByDeadline();

        return createSchedule(sortedJobs);
    }
    //
    public Schedule makeScheduleSJF()
    //shortest job first schedule. Schedule items contributing 0 to total profit last
    {

        Job[] sortedJobs = sortByLength();

        return createSchedule(sortedJobs);
    }


    public Schedule makeScheduleHPF()
    //highest profit first schedule. Schedule items contributing 0 to total profit last
    {

        Job[] sortedJobs = sortByProfit();

        return createSchedule(sortedJobs);

    }

    public Schedule newApproxSchedule() //Greedy-Choice: Greatest Profit/Length First
    {
        Schedule NASSchedule;

        Job[] jobList = sortByProfitOverLength();

        NASSchedule = createSchedule(jobList);

        return NASSchedule;

    }


    /* HELPER METHODS
       The methods below are called by the Scheduling Algorithms to perform Sorts and creating Schedules
       These were added by the Programmers to simplify and reuse certain methods across all Scheduling algorithms
     */

    //this method recursively  produces all Permutations of jobs[]
    private Job[] permute(java.util.List<Job> arr, int k){

        Job[] temp = new Job[arr.size()];
        Schedule tempSchedule;

        for(int i = k; i < arr.size(); i++){
            java.util.Collections.swap(arr, i, k);
            temp = permute(arr, k+1);
            java.util.Collections.swap(arr, k, i);
        }
        if (k == arr.size() -1) {

            Job[] theJobs = arr.toArray(new Job[arr.size()]);
            for (int i = 0; i < nJobs; i++) {
                theJobs[i].start = -1;
                theJobs[i].finish = -1;
            }
            tempSchedule= createSchedule(theJobs);

            if (tempSchedule.getProfit() > best.getProfit()) {
                best = tempSchedule;
            }
        }
        return best.getJobs();
    }

    private Job[] sortByDeadline()
    {
        //make a clone of 'jobs'
        Job[] sortedJobs = jobs.clone();

        Job temp ; //= new Job();
        for(int i = 1; i < sortedJobs.length; i++) {
            for(int j = 0; j < sortedJobs.length - i; j++) {
                if(sortedJobs[j].deadline > sortedJobs[j+1].deadline) {
                    temp = sortedJobs[j+1];
                    sortedJobs[j+1] = sortedJobs[j];
                    sortedJobs[j] = temp;
                }
            }
        }

        return sortedJobs;
    }

    private Job[] sortByLength()
    {
        //make a clone of 'jobs'
        Job[] sortedJobs = jobs.clone();

        Job temp ; //= new Job();
        for(int i = 1; i < sortedJobs.length; i++) {
            for(int j = 0; j < sortedJobs.length - i; j++) {
                if(sortedJobs[j].length > sortedJobs[j+1].length) {
                    temp = sortedJobs[j+1];
                    sortedJobs[j+1] = sortedJobs[j];
                    sortedJobs[j] = temp;
                }
            }
        }

        return sortedJobs;
    }

    private Job[] sortByProfit()
    {
        //make a clone of 'jobs'
        Job[] sortedJobs = jobs.clone();

        Job temp ; //= new Job();
        for(int i = 1; i < sortedJobs.length; i++) {
            for(int j = 0; j < sortedJobs.length - i; j++) {
                if(sortedJobs[j].profit < sortedJobs[j+1].profit) {
                    temp = sortedJobs[j+1];
                    sortedJobs[j+1] = sortedJobs[j];
                    sortedJobs[j] = temp;
                }
            }
        }

        return sortedJobs;
    }

    //This custom Sorting would make the Greedy Choice of maximizing Profit per Length
    //Short and Profitable jobs will have priority
    private Job[] sortByProfitOverLength(){
        //make a clone of 'jobs'
        Job[] sortedJobs = jobs.clone();

        Job temp ; //= new Job();
        for(int i = 1; i < sortedJobs.length; i++) {
            for(int j = 0; j < sortedJobs.length - i; j++) {
                if((sortedJobs[j].profit / sortedJobs[j].length) < (sortedJobs[j+1].profit / sortedJobs[j+1].length)) {
                    temp = sortedJobs[j+1];
                    sortedJobs[j+1] = sortedJobs[j];
                    sortedJobs[j] = temp;
                }
            }
        }

        return sortedJobs;
    }

    //this method is called by the scheduling algorithms after sorting 'jobs'
    private Schedule createSchedule(Job[] sortedJobs){
        if(sortedJobs == null){
            return null;
        }

        for(int i = 0; i < sortedJobs.length; i++)
        {
            sortedJobs[i].finish = -1;
            sortedJobs[i].start = -1;
        }

        Schedule theSchedule = new Schedule();
        sortedJobs[0].start = 0;
        sortedJobs[0].finish = sortedJobs[0].getLength();
        theSchedule.add(sortedJobs[0]);

        Job temp = sortedJobs[0];
        theSchedule.profit += sortedJobs[0].profit;

        for(int i = 1; i < sortedJobs.length; i++)
        {
            if(!((sortedJobs[i].deadline - sortedJobs[i].length) < temp.finish))
            {
                sortedJobs[i].start = temp.finish;
                sortedJobs[i].finish = sortedJobs[i].start + sortedJobs[i].length;
                temp = sortedJobs[i];
                theSchedule.add(sortedJobs[i]);
                theSchedule.profit += sortedJobs[i].profit;
            }
        }
        for(int i = 1; i < sortedJobs.length; i++)
        {
            if(sortedJobs[i].start  == -1 || sortedJobs[i].finish == -1)
            {
                sortedJobs[i].start = temp.finish;
                sortedJobs[i].finish = sortedJobs[i].start + sortedJobs[i].length;
                temp = sortedJobs[i];
                theSchedule.add(sortedJobs[i]);
            }
        }

        return theSchedule;
    }

}//end of JobScheduler class

//---------------------------Include Job and Schedule classes in JobScheduler. java-----------------------------
class Job
{
    int jobNumber;
    int length;
    int deadline;
    int profit;
    int start;
    int finish;

    //constructor
    public Job( int jn , int len, int d, int p)
    {
        jobNumber = jn; length = len; deadline = d;
        profit = p;  start = -1;  finish = -1;
    }
    public Job()
    {
        jobNumber = -1;
        length = -1;
        deadline = -1;
        profit = -1;
        start = -1;
        finish = -1;
    }

    //this method outputs the Job as a String specifying its attributes
    public String toString()
    {
        return "#" + jobNumber + ":(" + length + ","
                + deadline + "," + profit +
                "," + start + "," + finish + ")";
    }

    public int getLength()
    {
        return length;
    }

}//end of Job class



// ----------------------------------------------------
class Schedule {
    ArrayList<Job> schedule;
    int profit;

    public Schedule() {
        profit = 0;
        schedule = new ArrayList<Job>();
    }

    public void add(Job job) {
        schedule.add(job);  //add job to schedule

    }

    //add multiple Jobs to Schedule
    public void addMultiple(Job[] newJobs) {
        for (int i = 0; i < newJobs.length; i++) {
            schedule.add(newJobs[i]);
        }
    }


    public int getProfit() {
        return profit;
    }

    public String toString() {
        String s = "Schedule Profit = " + profit;
        for (int k = 0; k < schedule.size(); k++) {
            s = s + "\n" + schedule.get(k);

        }

        return s;
    }

    public Job[] getJobs(){
        return schedule.toArray(new Job[schedule.size()]);
    }

    public static void main(String[] args) {
        long start = 0; 
        long finish = 0;
        long time = 0;
        int[] length = { 7,4,2,5};
        int[] deadline = {7 ,16 ,8, 10};
        int[] profit = { 10, 9, 14, 13};
        JobScheduler js = new JobScheduler( length,deadline, profit);
        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
                "(length, deadline, profit, start, finish)" );
        js.printJobs();

        //--------------------------------------------
        System.out.println("\n\nOptimal Solution Using Brute Force O(n!)");
        start = System.nanoTime();
        Schedule bestSchedule = js.bruteForceSolution();
        finish = System.nanoTime();
        System.out.println( bestSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        //---------------------------------------
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        Schedule EDFPSchedule = js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        Schedule SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        start = System.nanoTime();
        Schedule HPFSchedule = js.makeScheduleHPF();
        finish = System.nanoTime();
        System.out.println(HPFSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        start = System.nanoTime();
        Schedule NASSchedule = js.newApproxSchedule();
        finish = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        int[] length2 = {2, 5 ,1, 4 ,10};
        int[] deadline2 = {10, 3, 4, 12, 3};
        int[] profit2 = {1, 40, 2, 4, 2};
        js = new JobScheduler( length2,deadline2, profit2);
        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
                "(length, deadline, profit, start, finish)" );
        js.printJobs();

        //--------------------------------------------
        System.out.println("\n\nOptimal Solution Using Brute Force O(n!)");
        start = System.nanoTime();
        bestSchedule = js.bruteForceSolution();
        finish = System.nanoTime();
        System.out.println( bestSchedule);

time = finish - start;
        System.out.println("Time is: " + time);
        //---------------------------------------
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        EDFPSchedule = js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);

        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        start = System.nanoTime();
        HPFSchedule = js.makeScheduleHPF();
        finish = System.nanoTime();
        System.out.println(HPFSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        start = System.nanoTime();
        NASSchedule = js.newApproxSchedule();
        finish = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        int[] length3 = {8, 18, 3, 10, 2, 3};
        int[] deadline3 = {10, 3, 24, 2, 41, 20};
        int[] profit3 = {1, 3, 13, 5, 12, 8};
        js = new JobScheduler( length3,deadline3, profit3);
        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
                "(length, deadline, profit, start, finish)" );
        js.printJobs();

        //--------------------------------------------
        System.out.println("\n\nOptimal Solution Using Brute Force O(n!)");
        start = System.nanoTime();
        js.bruteForceSolution();
        finish = System.nanoTime();
        System.out.println( bestSchedule);
time = finish - start;
        System.out.println("Time is: " + time);

        //---------------------------------------
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        start = System.nanoTime();
        HPFSchedule = js.makeScheduleHPF();
        finish = System.nanoTime();
        System.out.println(HPFSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        start = System.nanoTime();
        NASSchedule = js.newApproxSchedule();
        finish = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        int[] length4 = {6, 5, 7, 1, 10, 2, 31};
        int[] deadline4 = {1, 41, 4, 23, 2, 42, 10};
        int[] profit4 = {15, 1 ,  13, 5 , 4 ,7 , 9};
        js = new JobScheduler( length4,deadline4, profit4);
        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
                "(length, deadline, profit, start, finish)" );
        js.printJobs();

        //--------------------------------------------
        System.out.println("\n\nOptimal Solution Using Brute Force O(n!)");
        start = System.nanoTime();
        bestSchedule = js.bruteForceSolution();
        finish = System.nanoTime();
        System.out.println( bestSchedule);

time = finish - start;
        System.out.println("Time is: " + time);
        //---------------------------------------
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        EDFPSchedule = js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        start = System.nanoTime();
        HPFSchedule = js.makeScheduleHPF();
        finish = System.nanoTime();
        System.out.println(HPFSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        start = System.nanoTime();
        NASSchedule = js.newApproxSchedule();
        finish = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        int[] length5 = {20, 3, 21, 53, 42, 3, 1, 10};
        int[] deadline5 = {20, 40, 10, 34, 25, 10, 30, 32};
        int[] profit5 = {31, 20, 42, 10, 20, 13, 40, 24};
        js = new JobScheduler( length5,deadline5, profit5);
        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
                "(length, deadline, profit, start, finish)" );
        js.printJobs();

        //--------------------------------------------
        System.out.println("\n\nOptimal Solution Using Brute Force O(n!)");
        start = System.nanoTime();
        bestSchedule = js.bruteForceSolution();
        finish = System.nanoTime();
        System.out.println( bestSchedule);

time = finish - start;
        System.out.println("Time is: " + time);
        //---------------------------------------
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        EDFPSchedule = js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        start = System.nanoTime();
        HPFSchedule = js.makeScheduleHPF();
        finish = System.nanoTime();
        System.out.println(HPFSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        start = System.nanoTime();
        NASSchedule = js.newApproxSchedule();
        finish = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        int[] length6 = {10, 20, 3, 2, 12, 15, 24, 75, 13};
        int[] deadline6 = {9, 3, 2, 48, 19, 30, 5, 30, 23};
        int[] profit6 = {14, 52, 3, 10, 2, 34, 24, 23, 12};
        js = new JobScheduler( length6,deadline6, profit6);
        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
                "(length, deadline, profit, start, finish)" );
        js.printJobs();

        //--------------------------------------------
        System.out.println("\n\nOptimal Solution Using Brute Force O(n!)");
        start = System.nanoTime();
        bestSchedule = js.bruteForceSolution();
        finish = System.nanoTime();
        System.out.println( bestSchedule);

time = finish - start;
        System.out.println("Time is: " + time);
        //---------------------------------------
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        EDFPSchedule = js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        
        start = System.nanoTime();
        HPFSchedule = js.makeScheduleHPF();
        finish = System.nanoTime();
        System.out.println(HPFSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        start = System.nanoTime();
        NASSchedule = js.newApproxSchedule();
        finish = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        int[] length7 = {1, 4,2, 20, 410, 2, 43, 53, 34, 35,24, 11};
        int[] deadline7 = {32, 510, 2, 42, 4, 30, 32, 24, 11, 30, 12, 34};
        int[] profit7 = {2, 423, 42, 1, 204, 40, 204, 49, 192, 30, 39, 244};
        js = new JobScheduler( length7,deadline7, profit7);
        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
                "(length, deadline, profit, start, finish)" );
        js.printJobs();

        //--------------------------------------------
        //System.out.println("\n\nOptimal Solution Using Brute Force O(n!)");
//        bestSchedule = js.bruteForceSolution();
//        System.out.println( bestSchedule);


        //---------------------------------------
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        EDFPSchedule = js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        HPFSchedule = js.makeScheduleHPF();
        start = System.nanoTime();
        
        System.out.println(HPFSchedule);
        finish = System.nanoTime();
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        NASSchedule = js.newApproxSchedule();
        start = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        
        int[] length8 = { 7,4,2,5};
        int[] deadline8 = {7 ,16 ,8, 10};   
        int[] profit8 = { 10, 9, 14, 13};
        js = new JobScheduler( length8,deadline8, profit8);
        System.out.println("Test case A");
        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
                "(length, deadline, profit, start, finish)" );
        js.printJobs();
        
                //--------------------------------------------
        System.out.println("\n\nOptimal Solution Using Brute Force O(n!)");
        bestSchedule = js.bruteForceSolution();
        System.out.println( bestSchedule);


        //---------------------------------------
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        EDFPSchedule = js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        HPFSchedule = js.makeScheduleHPF();
        start = System.nanoTime();
        
        System.out.println(HPFSchedule);
        finish = System.nanoTime();
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        NASSchedule = js.newApproxSchedule();
        start = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        
        int[] length9 = { 2,3,1,10,7,4,2,5,7,7};
        int[] deadline9 = { 10,12, 9 ,22,  10, 4, 18, 15, 5, 9};   
        int[] profit9 = { 2,5,13,28,9,14, 2, 7, 3, 10};
        js = new JobScheduler( length9,deadline9, profit9);
        System.out.println("Test case B");

        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
               "(length, deadline, profit, start, finish)" );
        js.printJobs();
        
        //--------------------------------------------
        System.out.println("\n\nOptimal Solution Using Brute Force O(n!)");
        bestSchedule = js.bruteForceSolution();
        System.out.println( bestSchedule);


        //---------------------------------------
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        EDFPSchedule = js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        HPFSchedule = js.makeScheduleHPF();
        start = System.nanoTime();
        
        System.out.println(HPFSchedule);
        finish = System.nanoTime();
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        NASSchedule = js.newApproxSchedule();
        start = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);
        
        int[] length10 = { 2,3,1,10,7,  4,6,9,3,2,  5,2,5,7,7,  6,3,7,8,4,  5,2,9,10,5};

        int[] deadline10 = { 10,12,15,8,10,  9,22,12,15,35,  29,32,45,41,13,
                            16,10,20,10,4,  18,15,5,9, 30};  
                             
        int[] profit10 = { 2,5,13,28,8, 7,6,5,3,4,  9,7,6,9,14,  2,7,11,3,10,
                        8,5,9,10,3 };

        System.out.println("Test case C");
        js = new JobScheduler( length10,deadline10, profit10);
        System.out.println("Jobs to be scheduled");
        System.out.println("Job format is " +
                "(length, deadline, profit, start, finish)" );
        js.printJobs();
        System.out.println("\nEDF with unprofitable jobs last ");
        start = System.nanoTime();
        EDFPSchedule = js.makeScheduleEDF();
        finish = System.nanoTime();
        System.out.println(EDFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //-------------------------------------
        System.out.println("\nSJF with unprofitable jobs last");
        start = System.nanoTime();
        SJFPSchedule = js.makeScheduleSJF();
        finish = System.nanoTime();
        System.out.println(SJFPSchedule);
time = finish - start;
        System.out.println("Time is: " + time);
        //--------------------------------------------
        System.out.println("\nHPF with unprofitable jobs last");
        HPFSchedule = js.makeScheduleHPF();
        start = System.nanoTime();
        
        System.out.println(HPFSchedule);
        finish = System.nanoTime();
time = finish - start;
        System.out.println("Time is: " + time);
        // ------------------------------
        System.out.println("\nYour own creative solution");
        NASSchedule = js.newApproxSchedule();
        start = System.nanoTime();
        System.out.println(NASSchedule);
        time = finish - start;
        System.out.println("Time is: " + time);



    }
}