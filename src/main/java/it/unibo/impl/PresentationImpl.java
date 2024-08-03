package it.unibo.impl;

import it.unibo.api.Presentation;

import java.io.PrintStream;
import java.util.Scanner;

public class PresentationImpl implements Presentation {

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
        } while (!answer.equals("q") &&
                !answer.equals("1") &&
                !answer.equals("2") &&
                !answer.equals("3") &&
                !answer.equals("4") &&
                !answer.equals("5") &&
                !answer.equals("6"));

        switch (answer.charAt(0)) {
            case '1' -> runProgram(Presentation.EXAMPLE01);
            case '2' -> runProgram(Presentation.EXAMPLE02);
            case '3' -> runProgram(Presentation.EXAMPLE03);
            case '4' -> runProgram(Presentation.EXAMPLE04);
            case '5' -> runProgram(Presentation.EXAMPLE05);
            case '6' -> runProgram(Presentation.EXAMPLE06);
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
        output.println();
        output.println("Enter a value:");
        output.println("1 - run example01");
        output.println("2 - run example02");
        output.println("3 - run example03");
        output.println("4 - run example04");
        output.println("5 - run example05");
        output.println("6 - run example06");
        output.println("q - quit");
        output.println();
    }

    private void runProgram(String path){
        new BranchAndBoundProblem(path);
    }
}
