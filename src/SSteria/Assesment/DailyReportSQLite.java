package SSteria.Assesment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class DailyReportSQLite {
    private String DBFilepath;
    private String DBContents;
    private String outputData;
    private boolean DBFilepathValid;

    public DailyReportSQLite(String DBFilepath) {
        this.DBFilepath = DBFilepath;
        File file = new File(DBFilepath);
        if (file.exists() && DBFilepath.endsWith(".db")) {
            try (Connection SQLConnection = DriverManager.getConnection("jdbc:sqlite:" + DBFilepath)) {
                DBFilepathValid = true;
                DBContents = "";
                System.out.println("Succesfully connected with: " + DBFilepath);
                long supply = 0;
                long buy = 0;
                long result = 0;
                StringBuilder DBContentsAcquisition = new StringBuilder();
                Statement statement = SQLConnection.createStatement();
                ResultSet results = statement.executeQuery("SELECT * FROM DailyOperations");
                while (results.next()) {
                    DBContentsAcquisition.append(results.getString("operationType") + "," + results.getInt("amount") + "\n");
                    if(results.getString("operationType").equals("supply")) {
                        supply += results.getInt("amount");
                    }
                    else if(results.getString("operationType").equals("buy")) {
                        buy += results.getInt("amount");
                    }
                    else {
                        outputData = "Invalid contents of selected database";
                        DBFilepathValid = false;
                    }
                }
                DBContents = DBContentsAcquisition.toString();
                if(DBFilepathValid) {
                    result = supply - buy;
                    outputData = "supply," + supply + "\nbuy," + buy + "\nresult," + result;
                    System.out.println("Summary:");
                    System.out.println(outputData);
                }
                else
                    System.out.println(outputData);
            }
            catch (SQLException e) {
                DBFilepathValid = false;
                outputData = "Error occured when connecting to " + DBFilepath + ", " + e.getMessage();
                System.out.println(outputData);
            }
        }
        else if(DBFilepath.length() == 0) {
            DBFilepathValid = false;
            outputData = "No database filepath selected, please choose a proper input file with .db extension.";
            System.out.println(outputData);
            DBContents = "";
        }
        else {
            DBFilepathValid = false;
            outputData = "Please specify an existing .db file as an input. " + DBFilepath + " is not a valid one.";
            System.out.println(outputData);
            DBContents = "";
        }
    }

    public String getOutputData(){
        return outputData;
    }

    public String getDBContents(){
        return DBContents;
    }

    public String getDBFilepath(){
        return DBFilepath;
    }

    public void printDBContents(){
        System.out.println(DBContents);
    }

    public boolean isDBFilepathValid() {
        return DBFilepathValid;
    }

    /**
     * Method to save summary report from a database as a .csv file at indicated filepath.
     * @param outputFilepath indicates filepath of newly created report, f.e. C:\\Users\\PC\\Downloads\\report.csv .If indicated filepath already exists,
     *                       it will be overwritten - user must ensure it won't overwrite important existing documents. Current user also must have an OS
     *                       permission to write in a specified localization.
     * @return <b>-1</b> - if output filepath wasn't specified correctly and write to file cannot be performed
     * @return <b>0</b> - if input filepath wasn't specified correctly and write to file cannot be performed
     * @return <b>1</b> - if write to file completed successfully
     */
    public int writeReportToFile(String outputFilepath) {
        if (DBFilepathValid) {
            if (outputFilepath.endsWith(".csv")) {
                try (FileWriter fileWriter = new FileWriter(outputFilepath)) {
                    fileWriter.write(outputData);
                    fileWriter.close();
                    System.out.println("Report write performed succesfully to: " + outputFilepath);
                    return 1;
                }
                catch (IOException ex) {
                    System.out.println("Write failed.\nWrong output filepath, please specify a valid one, ending with .csv extension." + " " + ex.getMessage());
                    return -1;
                }
            }
            else {
                System.out.println("Write failed.\nWrong output filepath, please specify a valid one, ending with .csv extension.");
                return -1;
            }
        }
        else {
            System.out.println("Write failed.\nPlease specify correct database filename, before writing report to .csv file.");
            return 0;
        }
    }
}
