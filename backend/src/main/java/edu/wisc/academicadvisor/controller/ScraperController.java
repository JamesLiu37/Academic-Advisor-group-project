package edu.wisc.academicadvisor.controller;

import edu.wisc.academicadvisor.scraper.CourseGuide;
import edu.wisc.academicadvisor.scraper.GradeDistribution;
import edu.wisc.academicadvisor.scraper.RateMyProfessor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class ScraperController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping("/scrape")
    public @ResponseBody boolean courses(@RequestParam(value="process", defaultValue="") String process) {
        if (process.isEmpty()) {
            try {
                if (scrapeCourseGuide()) {
                    if (scrapeGradeDistribution() && scrapeRateMyProfessor()) {
                        return true;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (org.json.simple.parser.ParseException ex) {
                System.out.println("scraper fail, trying again");
                ex.printStackTrace();
                courses(process);
                ex.printStackTrace();
            }

        } else {
            String[] processes = process.split("-");
            // TODO: subsect of processes
        }
        return false;
    }

    private boolean scrapeCourseGuide() throws IOException, ParseException {
        //System.out.println("create CourseGuide()");
        CourseGuide courseGuide = new CourseGuide();
        //System.out.println("getJsonString()");
        String courses = courseGuide.getJsonString();
        jdbcTemplate.execute("DELETE FROM mergedcourses;");
        jdbcTemplate.execute("DELETE FROM class;");
        System.out.println(courses);
        JSONParser jsonParser = new JSONParser();
        JSONArray listOfCourses = (JSONArray) jsonParser.parse(courses);
        //System.out.println(listOfCourses.toJSONString());
        List<Object> mergedinfo, sectioninfo;
        int curr, prev;
        Object info;
        List<String> added = new ArrayList<>();
        for (int i = 0; i < listOfCourses.size(); i++) { // each course
            JSONArray course = (JSONArray) listOfCourses.get(i);
            mergedinfo = new ArrayList<>();
            curr = 0;
            while (!course.get(curr).equals("|")) {
                info = course.get(curr);
                if (!info.equals(String.valueOf((char) 160))) {
                    mergedinfo.add(info);
                    //System.out.println(mergedinfo.size() - 1 + ":" + info);
                }
                curr++;
            }
            curr++;
            if (mergedinfo.size() == 7) {
                String[] b = mergedinfo.get(4).toString().split("\n");
                String breadth = "";
                String split = "Breadth:";
                for (int j = 0; j < b.length; j++) {
                    if (b[j].contains(split)) breadth += b[j].replace(split, "") + ", ";
                    //if (j == b.length - 1) breadth = breadth.substring(0, breadth.length() - 3);
                }
                if (!mergedinfo.get(6).toString().contains("Course Description:")) continue;
                String[] description = mergedinfo.get(6).toString().replace("'","").replace("\n","").split("Pre-Reqs:");
                if (!isParsable((String) mergedinfo.get(1).toString().replace("Cross-Listed",""))) continue;
                if (added.contains(mergedinfo.get(0).toString().replace("'","") + "-" +
                        mergedinfo.get(1).toString().replace("'","").replace("Cross-Listed",""))) {
                    continue;
                } else added.add(mergedinfo.get(0).toString().replace("'","") + "-" +
                        mergedinfo.get(1).toString().replace("'","").replace("Cross-Listed",""));
                if (description.length == 2)
                    jdbcTemplate.execute("INSERT INTO mergedcourses (course, courseNum, title, numCredits, breadth, description, prereqs) VALUES ('" +
                        mergedinfo.get(0).toString().replace("'","") + "'," +
                        mergedinfo.get(1).toString().replace("'","").replace("Cross-Listed","") + ",'" +
                        mergedinfo.get(2).toString().replace("'","") + "','" +
                        mergedinfo.get(3).toString().replace("'","") + "','" +
                        breadth + "','" +
                        description[0].replace("Course Description: ","") + "','" +
                        description[1] + "');");
            } else if (mergedinfo.size() == 6) {
                String[] b = mergedinfo.get(4).toString().split("\n");
                String breadth = "";
                String split = "Breadth:";
                for (int j = 0; j < b.length; j++) {
                    if (b[j].contains(split)) breadth += b[j].replace(split, "") + ", ";
                    //if (j == b.length - 1) breadth = breadth.substring(0, breadth.length() - 3);
                }
                String[] description = mergedinfo.get(5).toString().replace("'","").replace("\n","").split("Pre-Reqs:");
                if (!isParsable((String) mergedinfo.get(1).toString().replace("Cross-Listed",""))) continue;
                if (added.contains(mergedinfo.get(0).toString().replace("'","") + "'," +
                        mergedinfo.get(1).toString().replace("'","").replace("Cross-Listed",""))) {
                    continue;
                } else added.add(mergedinfo.get(0).toString().replace("'","") + "'," +
                        mergedinfo.get(1).toString().replace("'","").replace("Cross-Listed",""));
                if (description.length == 2)
                    jdbcTemplate.execute("INSERT INTO mergedcourses (course, courseNum, title, numCredits, breadth, description, prereqs) VALUES ('" +
                        mergedinfo.get(0).toString().replace("'","") + "'," +
                        mergedinfo.get(1).toString().replace("'","").replace("Cross-Listed","") + ",'" +
                        mergedinfo.get(2).toString().replace("'","") + "','" +
                        mergedinfo.get(3).toString().replace("'","") + "','" +
                        breadth + "','" +
                        description[0].replace("Course Description: ","") + "','" +
                        description[1] + "');");
            } else {
                //for (int j = 0; j < course.size(); j++) System.out.println(j + ":" + course.get(j));
                continue;
            }
            while (curr < course.size()) {
                prev = curr;
                sectioninfo = new ArrayList<>();
                while (!course.get(curr).equals("|")) {
                    info = course.get(curr);
                    if (!info.toString().startsWith(String.valueOf((char) 160))) {
                        sectioninfo.add(info);
                        //System.out.println(mergedinfo.size() - 1 + ":" + info);
                    }
                    curr++;
                }
                curr++;
                if (sectioninfo.size() == 5) {


                    //for (int j = 0; j < sectioninfo.size(); j++) System.out.println(j + ":" + sectioninfo.get(j));
                    if (sectioninfo.get(4).toString().contains(",") && sectioninfo.get(0).toString().replace("'", "").length() <= 10 && sectioninfo.get(3).toString().replace("'", "").length() <= 100) {
                        String[] tmp = sectioninfo.get(4).toString().split(",");
                        String professor = tmp[1] + " " + tmp[0];
                        jdbcTemplate.execute("INSERT INTO class (course, courseNum, section, location, schedule, " +
                                "professor) VALUES ('" + mergedinfo.get(0).toString().replace("'","") + "'," +
                                mergedinfo.get(1).toString().replace("'","").replace("Cross-Listed","") + ",'" +
                                sectioninfo.get(0).toString().replace("'", "") + "','" +
                                sectioninfo.get(3).toString().replace("'", "") + "','" +
                                sectioninfo.get(2).toString().replace("'", "") + "','" +
                                professor.replace("'", "").replace(",","").split("\n")[0].replace("\u00a0"," ").trim() + "');");
                    }
                } else if (sectioninfo.size() == 4) {
                    /*if (sectioninfo.get(2).toString().contains(","))
                        jdbcTemplate.execute("INSERT INTO class (course, courseNum, section, location, schedule, " +
                                "professor) VALUES ('" + mergedinfo.get(0).toString().replace("'","") + "'," +
                                mergedinfo.get(1).toString().replace("'","").replace("Cross-Listed","") + ",'" +
                                sectioninfo.get(0).toString().replace("'", "") + "','','','" +
                                sectioninfo.get(2).toString().replace("'", "") + "');");*/
                }else {
                    for (int j = 0; j < sectioninfo.size(); j++) System.out.println(j + "-" + sectioninfo.get(j));
                }
            }

        }
        return true;
    }

    private static boolean isParsable(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }

    private boolean scrapeGradeDistribution() throws IOException {
        GradeDistribution gradeDistribution = new GradeDistribution();
        Map<String, double[]> distributionMap = gradeDistribution.GradeDistribution();
        Set<String> courses = distributionMap.keySet();
        double[] course = new double[distributionMap.keySet().size()];
        jdbcTemplate.execute("DELETE FROM gradeDis;");
        int i = 0;
        for (String s : distributionMap.keySet()) {
            try {
                String out = Arrays.toString(distributionMap.get(s));
                out = out.replace(" ", "").replace("[", "").replace("]", "");
                String[] output = out.split(",");
                for (int j = 0; j < output.length; j++) {
                    course[j] = Double.parseDouble(output[j]);
                }

                String[] tmp = s.split(" ");
                try {
                    Integer.parseInt(tmp[tmp.length-1]);
                } catch (Exception ex) {
                    //System.out.println("Cannot parse " + tmp[tmp.length-1]);
                    continue;
                }

                jdbcTemplate.execute("INSERT INTO gradeDis (course, avg, percA, percAB, percB, percBC," +
                        "percC, percD, percF) VALUES ('" + s + "'," + course[0] + "," + course[1] +
                        "," + course[2] + "," + course[3] + "," + course[4] + "," + course[5] + "," + course[6] + "," +
                        course[7] + ");");
                i++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    private boolean scrapeRateMyProfessor() throws IOException, ParseException {
        jdbcTemplate.execute("DELETE FROM professor;"); // TODO
        RateMyProfessor rateMyProfessor = new RateMyProfessor();
        List<String> professorsWithDuplicates = jdbcTemplate.queryForList("SELECT c.professor FROM class c WHERE c.professor " +
                "NOT IN (SELECT p.profName FROM professor p);", String.class);
        List<String> extraProfessors = new ArrayList<>();
        for (int i = 0; i < professorsWithDuplicates.size(); i++) {
            if (professorsWithDuplicates.get(i).contains("\n")) {
                String[] extras = professorsWithDuplicates.get(i).split("\n");
                professorsWithDuplicates.set(i, extras[0]);
                for (int j = 1; j < extras.length; j++) extraProfessors.add(extras[j]);
            }
        }
        professorsWithDuplicates.addAll(extraProfessors);
        Set<String> professors = new HashSet<>(professorsWithDuplicates);
        Object[] tmp = professors.toArray();
        String[] list = new String[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            list[i] = (String)tmp[i];
            //System.out.println(list[i]);
        }
        String json = rateMyProfessor.RateMyProfessor(list);
        //System.out.println(json);
        JSONParser jsonParser = new JSONParser();
        JSONArray listOfProfessors = (JSONArray) jsonParser.parse(json);
        //System.out.println(listOfProfessors.toJSONString());
        List<String> used = new ArrayList<>();
        for (int i = 0; i < listOfProfessors.size(); i++) { // each course
            JSONArray professor = (JSONArray) listOfProfessors.get(i);
            if (professor.size() == 4) {
                JSONArray tags = (JSONArray) professor.get(3);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < tags.size(); j++) {
                    if (j < tags.size() - 1) sb.append(tags.get(j).toString().replace("'","") + " ");
                    else sb.append(tags.get(tags.size() - 1).toString().replace("'",""));
                }
                if (used.contains(professor.get(0).toString())) continue;
                else used.add(professor.get(0).toString());
                jdbcTemplate.execute("INSERT INTO professor (profName, rating, easiness, tags) VALUES ('" +
                        professor.get(0).toString().replace("'","") + "'," +
                        professor.get(1).toString().replace("'","") + "," +
                        professor.get(2).toString().replace("'","") + ",'" +
                        sb.toString().replace("'","") + "')");
            } else System.out.println("professor.size() is not as expected: " + professor.size());
        }
        return true;
    }
}
