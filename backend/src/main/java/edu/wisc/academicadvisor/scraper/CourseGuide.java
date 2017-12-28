package edu.wisc.academicadvisor.scraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CourseGuide {
    public CourseGuide() { }

    public String getJsonString() throws IOException {
        //uncomment below and comment out rest to use last scrape
        //return new String(Files.readAllBytes(Paths.get("/home/academicadvisor/data.txt")), StandardCharsets.UTF_8);

        String s = null;

        Process p = Runtime.getRuntime().exec("python2.7 /home/academicadvisor/academicadvisor/backend/src/main/python/edu/wisc/academicadvisor/scraper/CourseGuideScraper.py");

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        // read the output from the command
        String outp = "";
        while ((s = stdInput.readLine()) != null) {
            outp = outp + s;
        }

        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        System.out.println(outp);

        return outp;
    }
}
