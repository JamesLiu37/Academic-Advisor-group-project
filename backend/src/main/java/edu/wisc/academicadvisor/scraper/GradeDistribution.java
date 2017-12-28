package edu.wisc.academicadvisor.scraper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class GradeDistribution {
    public GradeDistribution() { }

    public Map<String, double[]> GradeDistribution() throws IOException {
        //generate the array of relevant grade distribution files
        File [] files = new File[6];
        files [0] = new File("/home/academicadvisor/academicadvisor/backend/src/main/resources/pdf/Stats_distribs_2014-2015Spring-164-193.pdf");
        files [1] = new File("/home/academicadvisor/academicadvisor/backend/src/main/resources/pdf/Stats_distribs_2014-2015Spring-194-223.pdf");
        files [2] = new File("/home/academicadvisor/academicadvisor/backend/src/main/resources/pdf/Stats_distribs_2014-2015Spring-224-253.pdf");
        files [3] = new File("/home/academicadvisor/academicadvisor/backend/src/main/resources/pdf/Stats_distribs_2014-2015Spring-254-283.pdf");
        files [4] = new File("/home/academicadvisor/academicadvisor/backend/src/main/resources/pdf/Stats_distribs_2014-2015Spring-284-313.pdf");
        files [5] = new File("/home/academicadvisor/academicadvisor/backend/src/main/resources/pdf/Stats_distribs_2014-2015Spring-314-342.pdf");

        return GradeDistribution(files);
    }

    public Map<String, double[]> GradeDistribution(File[] files) throws IOException {
        Map<String, double[]> gradeDistribution = new HashMap<String, double[]>();

        String totalLines[] = new String[10000];
        ArrayList<String> list = new ArrayList<String>();

        for (int count = 0; count < 6; count++) {

            //System.out.println("This is the " + (count + 1) + " times beginning................................");

            //Loading an existing document
            PDDocument document = PDDocument.load(files[count]);

            //Instantiate PDFTextStripper class
            PDFTextStripper pdfStripper = new PDFTextStripper();

            //Retrieving text from PDF document
            String text = pdfStripper.getText(document);

            //split the rest into an String array by each line
            String lines[] = text.split("\\r?\\n");

            //retain the course number from individual section
            for (int i = lines.length - 1; i >= 0; i--) {
                if (lines[i].startsWith("CourseSearch Total")) {
                    String tempStr = lines[i - 1];
                    lines[i] = String.join(" ", lines[i], tempStr.substring(tempStr.length() - 3));
                }
            }

            //distribute the department name, such as "CS", "CHEM" to courses associated
            String departName = "";
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].equals("A AB B BC C D F S U CR N P I NW NR Other"))
                    departName = lines[i + 1];

                lines[i] = String.join(" departmentName ", lines[i], departName);

                if (lines[i].equals("Fall 2015-2016 University of Wisconsin - Madison"))
                    departName = "";

                lines[i] = lines[i].replaceAll("\\*+", "NotApplied ");
            }

            //eliminate different sections in one course but keep section #1 to get the course number
            for (int i = 0; i < lines.length; i++) {
                String temp[] = lines[i].split(" ");
                if (lines[i].startsWith("001")) {
                    if (temp[temp.length - 4].isEmpty())
                        lines[i] = null;

                } else if ((lines[i].startsWith("001") == false) && (lines[i].startsWith("CourseSearch Total") == false))
                    lines[i] = null;
            }

            //delete null nodes in the array
            for (String a : lines) {
                if (a != null)
                    list.add(a);
            }

            //System.out.println("This is the " + (count + 1) + " times ending................................");
            document.close();
        }

        //convert the Arraylist into array
        totalLines = list.toArray(new String[list.size()]);

        //just for checking the elements stored in the array, can delete later
      /*
      for(int i = 0; i < totalLines.length; i++){
    	  System.out.println(totalLines[i]);
      }
    */

        //modify the data scraped from the pdf files
        for (int i = 0; i < totalLines.length; i++) {

            double grade[] = new double[17];
            String courseName;

            //for checking
            //System.out.println(totalLines[i]);

            //if the line starts with "001"
            if (totalLines[i].startsWith("001")) {

                //course name
                String temp[] = totalLines[i].split(" departmentName ");
                String courseDepartment = temp[1];

                temp = totalLines[i].split(".   ");
                String courseNum = temp[temp.length - 1].substring(0, 3);
                courseName = courseDepartment + " " + courseNum;

                //System.out.println(courseName);

                //course grade distribution
                temp = totalLines[i].split(" ");
                int counter = 19;
                int index = 0;

                for (int j = 2; j < counter; j++) {

                    if (temp[j].equals("NotApplied")) {

                        break;
                    } else if (temp[j].matches(".")) {
                        grade[index] = 0.0;
                    } else if (temp[j].matches("")) {

                    } else {
                        grade[index] = Double.valueOf(temp[j]);
                    }

                    //System.out.println(grade[index]);
                    index++;
                }

                //if the line starts with "CourseSearch Total"
            } else {

                //course name
                String temp[] = totalLines[i].split(" departmentName ");

                String departmentName = temp[1];
                String courseNum = temp[0].substring(temp[0].length() - 3);
                courseName = departmentName + " " + courseNum;
                //System.out.println(courseName);

                //course grade
                temp = totalLines[i].split(" ");

                int index = 1;
                int counter = temp.length;
                int offset = 0;


                for (int j = 3; j < temp.length; j++) {

                    try {
                        if (temp[j].matches("NotApplied")) {

                            break;
                        } else if (temp[j].matches(".")) {
                            grade[index] = 0.0;
                            //System.out.println(grade[index]);
                            index++;

                        } else if (temp[j].matches("")) {


                        } else {
                            grade[index] = Double.valueOf(temp[j]);
                            index++;
                            offset++;
                        }
                        if (index >= 17)
                            break;

                    } catch (NumberFormatException ex) {

                        break;
                    }
                }

                try {
                    if (temp[3].equals("NotApplied") == false)
                        grade[0] = Double.valueOf(temp[35 - offset].substring(0, 5));
                } catch (NumberFormatException ex) {
                    grade[0] = 0.0;

                } catch (StringIndexOutOfBoundsException ex) {
                    grade[0] = 0.0;

                }

                for (int k = 0; k < grade.length; k++) {
                    System.out.println(grade[k]);
                }
            }
            //input the data into Map gradeDistribution
            gradeDistribution.put(courseName, grade);
        }
        return gradeDistribution;
    }
}
