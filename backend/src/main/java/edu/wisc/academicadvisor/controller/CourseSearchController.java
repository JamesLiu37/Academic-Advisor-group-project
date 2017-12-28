package edu.wisc.academicadvisor.controller;

import edu.wisc.academicadvisor.model.Course;
import edu.wisc.academicadvisor.model.CourseSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseSearchController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping("/courses")
    public @ResponseBody List<CourseSearch> courses(@RequestParam(value="breadth", defaultValue="") String breadth, // contains
                                                    @RequestParam(value="credits", defaultValue="") String numCredits, // equal
                                                    @RequestParam(value="department", defaultValue="") String department, // contains
                                                    @RequestParam(value="number", defaultValue="-1") int number, // equal
                                                    @RequestParam(value="professor", defaultValue="") String professor, // contains
                                                    @RequestParam(value="busy", defaultValue="") String busy, // ignore
                                                    @RequestParam(value="rmp", defaultValue="-1.0") double rmp, // >= rmp
                                                    @RequestParam(value="gpa", defaultValue="-1.0") double gpa, // >= gpa
                                                    @RequestParam(value="tags", defaultValue="") String tags) { // contains in description, title
        List<Map<String, Object>> rows;
        List<CourseSearch> courses = new ArrayList<>();

        try {
            // TODO: copy
            String query = "SELECT DISTINCT mc.course, mc.courseNum, mc.title, mc.numCredits FROM mergedcourses mc " +
                    "LEFT JOIN class c ON mc.course = c.course AND mc.courseNum = c.courseNum LEFT JOIN professor p " +
                    "ON c.professor = p.profName LEFT JOIN gradeDis gd ON CONCAT(mc.course, ' ', mc.courseNum) = " +
                    "gd.course";

            List<String> criteria = new ArrayList<>();
            if (!numCredits.isEmpty()) {
                criteria.add("mc.numCredits LIKE '%" + numCredits + "%'");
            }
            if (number >= 0) {
                criteria.add("mc.courseNum = '" + number + "'");
            }
            if (!breadth.isEmpty()) {
                criteria.add("mc.breadth REGEXP '" + breadth.replace("-","|") + "'");
            }
            if (!department.isEmpty()) {
                criteria.add("mc.course LIKE '%" + department + "%'");
            }
            if (!tags.isEmpty()) {
                criteria.add("mc.description LIKE '%" + tags + "%'");
            }
            if (!professor.isEmpty()) {
                criteria.add("c.professor LIKE '%" + professor + "%'");
            }
            if (rmp >= 0) {
                criteria.add("p.rating >= '" + rmp + "'");
            }
            if (gpa >= 0) {
                criteria.add("gd.avg >= '" + gpa + "'");
            }

            for (int i = 0; i < criteria.size(); i++) {
                String cat = "";
                if (i == 0) {
                    cat = " WHERE ";
                } else {
                    cat = " AND ";
                }

                query += cat;
                query += criteria.get(i);
            }

            query += ";";
            //rows = jdbcTemplate.queryForList("SELECT course, courseNum, title, numCredits FROM  mergedcourses WHERE ...;");
            System.out.println(query);
            rows = jdbcTemplate.queryForList(query);

            for (Map row : rows) {
                courses.add(new CourseSearch((String)row.get("course"),
                        (Integer)row.get("courseNum"),
                        (String)row.get("title"),
                        (String)row.get("numCredits")));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return courses;
    }
}
