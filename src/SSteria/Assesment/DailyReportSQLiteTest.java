package SSteria.Assesment;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)

public class DailyReportSQLiteTest {

    private DailyReportSQLite report;
    //Private fields below are used to handle test conditions
    private String testDBFilepath;
    private String testOutputFilepath;
    private String expectedOutputString;
    private String expectedOutputFileContents;
    private boolean expectedDBFilepathValidity;
    private int expectedWriteReturnCode;

    public DailyReportSQLiteTest(String testDBFilepath, String testOutputFilepath, String expectedOutputString, String expectedOutputFileContents,
                           boolean expectedDBFilepathValidity, int expectedWriteReturnCode) {
        this.testDBFilepath = testDBFilepath;
        this.testOutputFilepath = testOutputFilepath;
        this.expectedOutputString = expectedOutputString;
        this.expectedOutputFileContents = expectedOutputFileContents;
        this.expectedDBFilepathValidity = expectedDBFilepathValidity;
        this.expectedWriteReturnCode = expectedWriteReturnCode;
    }

    //Collection of test condition parameters
    @Parameterized.Parameters
    public static Collection<Object[]> testConditions() {
        return Arrays.asList(new Object[][]{
                {"testDB1.db","temp\\testOutput1.csv","supply,1812\nbuy,1455\nresult,357","supply,1812\nbuy,1455\nresult,357",true,1}, //1812 - 1455 - result 357, valid output filepath
                {"testDB1","temp\\testOutput1.csv","Please specify an existing .db file as an input. testDB1 is not a valid one.","",false,0}, //wrong input DB filepath
                {"testDB1.db","temp\\testOutput1","supply,1812\nbuy,1455\nresult,357","",true,-1}, //wrong output .csv filepath
                {"","temp\\testOutput1","No database filepath selected, please choose a proper input file with .db extension.","",false,0}, //empty input filepath
                {"testDB2.db","temp\\testOutput2.csv","supply,0\nbuy,864\nresult,-864","supply,0\nbuy,864\nresult,-864",true,1}, //0 - 864 - result -864
                {"testDB3.db","temp\\testOutput3.csv","supply,6282\nbuy,0\nresult,6282","supply,6282\nbuy,0\nresult,6282",true,1}, //6282 - 0 - result 6282
                {"testDB4.db","temp\\testOutput4.csv","supply,2946368\nbuy,92323\nresult,2854045","supply,2946368\nbuy,92323\nresult,2854045",true,1}, //2946368 - 92323 - result 2854045
                {"testDB5.db","temp\\testOutput5.csv","Error occured when connecting to testDB5.db, [SQLITE_ERROR] SQL error or missing database (no such table: DailyOperations)","",false,0}, //invalid name of the table (DailyOperations)
                {"testDB6.db","temp\\testOutput6.csv","Error occured when connecting to testDB6.db, no such column: 'operationType'","",false,0}, //invalid name of the column (operationTypes)
                {"testDB7.db","temp\\testOutput7.csv","Error occured when connecting to testDB7.db, no such column: 'amount'","",false,0}, //invalid name of the column (amounts)
                {"testDB8.db","temp\\testOutput8.csv","Invalid contents of selected database","",false,0}, //invalid contents of the input database
        });
    }

    //Creating a temporary folder to enable simple delete of test output reports
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    //Creating a new report object before each test case
    @org.junit.Before
    public void setup(){
        report = new DailyReportSQLite(testDBFilepath);
    }

    @org.junit.Test
    public void getOutputDataTest() {
        assertEquals(expectedOutputString,report.getOutputData());
    }

    @org.junit.Test
    public void getDBFilepathTest() {
        assertEquals(testDBFilepath,report.getDBFilepath());
    }

    @org.junit.Test
    public void isDBFilepathValidTest() {
        assertEquals(expectedDBFilepathValidity,report.isDBFilepathValid());
    }

    //Checking validity of writeReportToFile method return code
    @org.junit.Test
    public void writeReportToFileReturnCode() throws IOException {
        folder.newFolder("temp");
        int writeResult = report.writeReportToFile(folder.getRoot() + "\\" + testOutputFilepath);
        assertEquals(expectedWriteReturnCode,writeResult);
    }

    //Checking validity of writeReportToFile method output contents
    @org.junit.Test
    public void writeReportToFileContentsCheck() throws IOException{
        folder.newFolder("temp");
        report.writeReportToFile(folder.getRoot() + "\\" + testOutputFilepath);
        String contents = "";
        try{
            byte[] contentsBytes = Files.readAllBytes(Path.of(folder.getRoot() + "\\" + testOutputFilepath));
            contents = new String(contentsBytes);
        }
        catch (NoSuchFileException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            assertEquals(expectedOutputFileContents, contents);
        }
    }

}