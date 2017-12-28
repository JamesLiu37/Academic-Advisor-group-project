package edu.wisc.academicadvisor.controller;

import edu.wisc.academicadvisor.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class CourseController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping("/course")
    public @ResponseBody
    List<Course> course(@RequestParam(value = "course", defaultValue = "") String course,
                        @RequestParam(value = "num", defaultValue = "0") int courseNum) {
        System.out.println(course + " " + courseNum);
        List<Map<String, Object>> rows;
        List<Course> ret = new ArrayList<>();

        try {
            //rows = jdbcTemplate.queryForList("SELECT course, courseNum, title, numCredits FROM  mergedcourses WHERE ...;");
            String query = "SELECT DISTINCT mc.course, " +
                    "mc.courseNum, " +
                    "c.section, " +
                    "mc.title, " +
                    "mc.numCredits, " +
                    "mc.breadth, " +
                    "c.professor, " +
                    "p.rating, " +
                    "gd.avg, " +
                    "c.schedule, " +
                    "mc.description " +
                    "FROM mergedcourses mc " +
                    "LEFT JOIN class c ON mc.course = c.course " +
                    "AND mc.courseNum = c.courseNum " +
                    "LEFT JOIN professor p ON c.professor = p.profName " +
                    "LEFT JOIN gradeDis gd ON CONCAT(mc.course, ' ', mc.courseNum) = gd.course " +
                    "WHERE c.courseNum = '" + courseNum + "' AND " +
                    "c.course LIKE '%" + course + "%';";
            System.out.println(query);
            rows = jdbcTemplate.queryForList(query);

            for (Map<String, Object> row : rows) {
                Double rating = (Double) row.get("rating");
                if (rating == null) rating = 0.0;
                Double avg = (Double) row.get("avg");
                if (avg == null) avg = 0.0;
                ret.add(new Course((String) row.get("course"),
                        (Integer) row.get("courseNum"),
                        (String) row.get("section"),
                        (String) row.get("title"),
                        (String) row.get("numCredits"),
                        (String) row.get("breadth"),
                        (String) row.get("professor"),
                        rating,
                        avg,
                        (String) row.get("schedule"),
                        (String) row.get("description")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
}
