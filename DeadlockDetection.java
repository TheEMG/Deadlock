/**
 * Eric Gutierrez 
 * COSC 3355
 * 3/17/2024
 * Description: The DeadlockDetection program reads system resource states from text files and applies a deadlock
 * detection algorithm. It outputs the state of resources and identifies any deadlocked processes.
 * The program also outputs the allocation matrix, the request matrix, and the available resources vector.
 * 
 * 
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DeadlockDetection {
    // Data structures 
    // Vector 'Available': holds the count of available instances for each resource type.
    private int[] Available; 
    // Matrix 'Allocation': defines the current allocation of each resource type to each process.
    private int[][] Allocation;
    // Matrix 'Request': indicates the current request of each process for each resource type.
    private int[][] Request;
    // The total number of processes (P_i) in the system.
    private int n; 
    // The total number of different resource types in the system.
    private int m;


    /**
    *
    * The constructor invokes the parseFile method to read data from the given file path and populate
    * the Allocation, Request, and Available data structures for deadlock detection.
    *
    * The file should adhere to the expected format detailed in the parseFile method's documentation.
    * 
    * @param filePath The path to the input file containing the system's initial resource state.
    * @throws FileNotFoundException if the file cannot be found at the specified path.
    */
    public DeadlockDetection(String filePath) throws FileNotFoundException {
        parseFile(filePath);
    }

    /**
    * Parses the input file to initialize the data structures for deadlock detection.
    * The input file should be formatted as follows:
    * - The first line contains two integers: 'n' (number of processes) and 'm' (number of resource types).
    * - The next 'n' lines each contain 'm' integers, which populate the Allocation matrix.
    * - The following 'n' lines each contain 'm' integers, which populate the Request matrix.
    * - The final line contains 'm' integers that define the Available vector.
    * 
    * Example format for n=5 processes and m=3 resource types:
    * 5 3
    * 0 1 0  // Allocation for P0
    * 2 0 0  // Allocation for P1
    * 1 0 1  // Allocation for P2
    * 0 0 1  // Allocation for P3
    * 0 1 0  // Allocation for P4
    * 1 0 0  // Request for P0
    * 0 0 1  // Request for P1
    * 1 1 1  // Request for P2
    * 0 0 1  // Request for P3
    * 0 0 0  // Request for P4
    * 0 0 0  // Available resources
    *
    * Note: The spaces between numbers are used for separation. 
    *
    * @param filePath the relative or absolute path to the input file.
    * @throws FileNotFoundException if the file cannot be found at the specified path.
    */
    private void parseFile(String filePath) throws FileNotFoundException {
        Scanner input = new Scanner(new File(filePath));
        
        // Read the first two integers to get the number of processes (n) and the number of resource types (m)
        n = input.nextInt();
        m = input.nextInt();
        
        // Initialize matrices and vectors
        Allocation = new int[n][m];
        Request = new int[n][m];
        Available = new int[m];
        
        // Fill the Allocation matrix 
        // Loop over each process
        for (int i = 0; i < n; i++) { 
            // Loop over each resource type
            for (int j = 0; j < m; j++) { 
                // Ensure there is an integer to read
                if (input.hasNextInt()) {
                    // Read the next integer into the Allocation matrix
                    Allocation[i][j] = input.nextInt(); 
                }
            }
        }
        
        // Fill the Request matrix 
        // Again loop over each process
        for (int i = 0; i < n; i++) { 
            for (int j = 0; j < m; j++) {
                if (input.hasNextInt()) { 
                    Request[i][j] = input.nextInt();
                }
            }
        }
        
        // Fill the Available vector 
        for (int j = 0; j < m; j++) { 
            if (input.hasNextInt()) { 
                Available[j] = input.nextInt(); 
            }
        }
            // Close the scanner   
            input.close(); 

    
    }

    /**
     * Copies the Available vector to a new Work vector.
     * This method is used to intialize the work vector with the same values
     *
     * @return A new int array (vector) that is a copy of the Available vector.
     */
    private int[] copyAvailableToWork() {
        // Create a new Work vector of size 'm'
        int[] work = new int[m]; 
        // Copy elements from the Available vector to the Work vector.
        System.arraycopy(Available, 0, work, 0, m);
        return work; // Return the new Work vector
    }

    /**
     * Checks if all elements in the Finish array are true.
     * This determines if all processes have been able to
     * fulfill their resource requests, indicating that no deadlock is present.
     *
     * @param finish The Finish array where each element represents whether
     *               a process has finished (true) or not (false).
     * @return True if all elements in the Finish array are true, otherwise false.
     */
    private boolean isTrue (boolean[] finish) {
        // Iterate over each element of the Finish array
        for (int i = 0; i < finish.length; i++) {
            if (!finish[i]) {
                return false; // Return false immediately if any element is false
            }
        }
        return true; // Return true if all elements are true
    }

    /**
     * Detects deadlocks in the system by applying the deadlock detection algorithm.
     * This method initializes the Work vector from the Available vector and iterates
     * over each process to check if their resource requests can be satisfied.
     * After attempting to satisfy all processes, it checks if any process remains unsatisfied (if unsatifised then its a deadlock)
     */
    public void detectDeadlock() {
        // Step 1: Work = Available
        int[] Work = copyAvailableToWork(); 
        // Finish status for each process
        boolean[] Finish = new boolean[n]; 
        
        // Step 1: Initialize the Finish vector
        for (int i = 0; i < n; i++) {
           // Process has no allocation
            Finish[i] = true;
            // Check allocation for each resource type
            for (int j = 0; j < m; j++) {
                // Process has allocation
                if (Allocation[i][j] != 0) {
                    Finish[i] = false;
                    // No need to check further resources 
                    break;
                }
            }
        }
        
        // Step 2 and 3: Loop until no more processes can be satisfied
        boolean done = false;
        while (!done) {
            // Assume no process can be satisfied
            done = true; 
            for (int i = 0; i < n; i++) {
                // Check if Finish[i] is false and Request[i] <= Work
                if (!Finish[i] && checkIfLessOrEqual(Request[i], Work)) {
                    // Step 3: Work = Work + Allocation[i]
                    for (int j = 0; j < m; j++) {
                        Work[j] += Allocation[i][j];
                    }
                     // Process has finished
                    Finish[i] = true;
                     // A process has been satisfied, so we're not done....
                    done = false;
                }
            }
        }
        
        // Step 4: Check for deadlock
        // Print a new line before the final result for readability
        System.out.println(); 
        // Use isTrue method name to check if all processes are finished
        if (isTrue(Finish)) {
            System.out.println("No deadlock is detected.");
        } else {
            System.out.println("System is deadlocked, the deadlocked processes are:");
            for (int i = 0; i < n; i++) {
                // Identify and list deadlocked processes
                if (!Finish[i]) {
                    // "P%d\n" is a format string where "%d" is replaced by the value of 'i' (the process index),
                    System.out.printf("P%d\n", i);
                }
            }
        }
    }

    
    // Helper method to compare if Request <= Work
    private boolean checkIfLessOrEqual(int[] request, int[] work) {
        for (int i = 0; i < m; i++) {
            if (request[i] > work[i]) {
                return false;
            }
        }
        return true;
    }
    

    private void printState() {
        System.out.println("Allocated");
        System.out.print("  ");
        for (int i = 0; i < m; i++) {
            System.out.printf("R%d ", i);
        }
        System.out.println();
    
        for (int i = 0; i < n; i++) {
            System.out.printf("P%d ", i);
            for (int j = 0; j < m; j++) {
                System.out.printf("%d  ", Allocation[i][j]);
            }
            System.out.println();
        }
    
        System.out.println("Available");
        System.out.print("  ");
        for (int i = 0; i < m; i++) {
            System.out.printf("R%d ", i);
        }
        System.out.println();
        System.out.print("  ");
        for (int j = 0; j < m; j++) {
            System.out.printf("%d  ", Available[j]);
        }
        System.out.println();
    
        System.out.println("Requested");
        System.out.print("  ");
        for (int i = 0; i < m; i++) {
            System.out.printf("R%d ", i);
        }
        System.out.println();
    
        for (int i = 0; i < n; i++) {
            System.out.printf("P%d ", i);
            for (int j = 0; j < m; j++) {
                System.out.printf("%d  ", Request[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        String[] inputFiles = {"input.txt", "input2.txt", "input3.txt", "input4.txt"};
    
        for (int i = 0; i < inputFiles.length; i++) {
            String inputFile = inputFiles[i];
            try {
                System.out.println("Processing file: " + inputFile);
                DeadlockDetection detector = new DeadlockDetection(inputFile);
                detector.printState();
                detector.detectDeadlock();
                System.out.println("---------------------------------------");
            } catch (FileNotFoundException e) {
                System.out.println("Could not find file: " + inputFile);
                e.printStackTrace();
            }
        }
    }
    
}
