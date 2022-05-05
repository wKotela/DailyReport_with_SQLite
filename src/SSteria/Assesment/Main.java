package SSteria.Assesment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * It's a program which enables the user to generate a report from a supply/buy database and write it to specified location
 * using DailyReport class.
 * Program is working with SQLite databases, so it's using corresponding JDBC driver.
 * User specifies input and output file location by typing input/output filepaths into the console.
 * @author Wiktor Kotela
 */
public class Main {

    public static void main(String[] args) {
        boolean repeatReportGeneration = true;
        Scanner userInput = new Scanner(System.in);
        //Main program loop
        while (repeatReportGeneration) {
            System.out.println("Specify the SQLite database filepath: (f.e.: C:\\Users\\PC\\test.db)");
            var report = new DailyReportSQLite(userInput.nextLine());
            if(report.isDBFilepathValid()) {
                System.out.println("Would you like to preview database contents? (Y/N or any other)");
                if (userInput.nextLine().toLowerCase().equals("y"))
                    report.printDBContents();
                System.out.println("Please specify filepath of the output .csv report file: (f.e.: C:\\Users\\PC\\report.csv)");
                report.writeReportToFile(userInput.nextLine());
                }
            System.out.println("Would you like to prepare a report from another database? (Y/N or any other)");
            if (!userInput.nextLine().toLowerCase().equals("y")) {
                repeatReportGeneration = false;
            }
        }
    }
}
