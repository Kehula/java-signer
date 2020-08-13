package main;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    int[] inputData = new int[]{0, 1, 1, 2, 3, 5, 8};
    //int[] testInput = new int[]{0, 1};
    
    System.out.println("SingleThread test:");
    SingleThread.runTest(inputData);
    
    System.out.println("MultiThread test:");
    MultithreadTest.runTest(inputData);
  }
}
