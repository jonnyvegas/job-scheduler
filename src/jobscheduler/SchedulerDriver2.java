/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jobscheduler;

/**
 *
 * @author jonat
 */
//Comp 496ALG  Project 2  TestCase A with solutions
import java.util.*;

public class SchedulerDriver2
{

   public static void main (String[] args)
   {
   
      int[] length = { 7,4,2,5};
      int[] deadline = {7 ,16 ,8, 10};   
      int[] profit = { 10, 9, 14, 13};  
      JobScheduler js = new JobScheduler(length,deadline, profit);
//      System.out.println("Jobs to be scheduled");
//      System.out.println("Job format is " +
//      "(length, deadline, profit, start, finish)" );
//      js.printJobs();
//      
//     //--------------------------------------------
//      System.out.println("\nOptimal Solution Using Brute Force O(n!)");
//      Schedule bestSchedule = js.bruteForceSolution();
//      System.out.println( bestSchedule);
//      
//      
//       //---------------------------------------
      //System.out.println("\nEDF with unprofitable jobs last ");
      //Schedule EDFPSchedule = js.makeScheduleEDF();
     // System.out.println(EDFPSchedule);
     
//      //-------------------------------------      
//      System.out.println("\nSJF with unprofitable jobs last");
//      Schedule SJFPSchedule = js.makeScheduleSJF();
//      System.out.println(SJFPSchedule);
//      
//      //--------------------------------------------
//      System.out.println("\nHPF with unprofitable jobs last");
//      Schedule HPFSchedule = js.makeScheduleHPF();
//      System.out.println(HPFSchedule);
//
//      // ------------------------------
//      System.out.println("\nYour own creative solution");
//      Schedule NASSchedule = js.newApproxSchedule();
//      System.out.println(NASSchedule);
       
   }
}

/*-----------------Sample Run -----------------------

 Input: Jobs to be scheduled
 Job format is (length, deadline, profit, start, finish)
 #0:(7,7,10,-1,-1)
 #1:(4,16,9,-1,-1)
 #2:(2,8,14,-1,-1)
 #3:(5,10,13,-1,-1)
 
 Optimal Solution Using Brute Force O(n!)
 Schedule Profit = 36
 #3:(5,10,13,0,5)
 #2:(2,8,14,5,7)
 #1:(4,16,9,7,11)
 #0:(7,7,10,11,18)
 
 EDF with unprofitable jobs last 
 Schedule Profit = 19
 #0:(7,7,10,0,7)
 #1:(4,16,9,7,11)
 #3:(5,10,13,11,16)
 #2:(2,8,14,16,18)
 
 
 SJF with unprofitable jobs last
 Schedule Profit = 23
 #2:(2,8,14,0,2)
 #1:(4,16,9,2,6)
 #0:(7,7,10,6,13)
 #3:(5,10,13,13,18)
 
 HPF with unprofitable jobs last
 Schedule Profit = 36
 #2:(2,8,14,0,2)
 #3:(5,10,13,2,7)
 #1:(4,16,9,7,11)
 #0:(7,7,10,11,18)
 
 
 Your own creative solution  
 <This will have your own answers>
 
*/
 