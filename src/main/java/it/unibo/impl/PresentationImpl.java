package it.unibo.impl;

import java.io.PrintStream;
import java.util.Scanner;

public class PresentationImpl {

    private static final Scanner input = new Scanner(System.in);
    private static final PrintStream output = System.out;

    public PresentationImpl() {

        this.start();
    }

    private void start() {
        this.displayIntro();

        String answer;
        do {
            answer = input.next();
        } while (!answer.equals("r") && !answer.equals("e") && !answer.equals("q"));

        switch (answer.charAt(0)) {
            case 'r' -> runBranch();
            case 'e' -> examplesBranch();
            case 'q' -> System.exit(0);
            default -> {
                System.err.println("Reading char failed in the branch choice");
                System.exit(1);
            }
        }
    }

    private void displayIntro() {
        output.println();
        output.println();
        output.println("Operation Research project by Silvi Sinani");
        output.println();
        output.println("Description:");
        output.println("PLI resolver by Branch and Bound algorithm. ");
        output.println("IBM ILOG CPLEX Dual algorithm is used to resolve the relaxations.");
        output.println();
        output.println();
        output.println("Execution:");
        output.println("Enter a value");
        output.println("r - run the program");
        output.println("e - examples");
        output.println("q - quit");
    }

    private void examplesBranch() {
        output.println("Examples");
    }

    private void runBranch() {
        output.println("Examples");
    }

}
