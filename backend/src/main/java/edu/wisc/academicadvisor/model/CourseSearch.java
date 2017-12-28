package edu.wisc.academicadvisor.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class CourseSearch implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String course; // COMP SCI
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int courseNum; // 302
    private String title; // Introduction to Programming
    private String numCredits; // 3

    public CourseSearch(String course, int courseNum, String title, String numCredits) {
        this.course = course;
        this.courseNum = courseNum;
        this.title = title;
        this.numCredits = numCredits;
    }

    public String getCourse() {
        return course;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public String getTitle() {
        return title;
    }

    public String getNumCredits() {
        return numCredits;
    }
}
