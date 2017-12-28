package edu.wisc.academicadvisor.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String course;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int courseNum;
    private String section;
    private String title;
    private String numCredits;
    private String breadth;
    private String professor;
    private double rating;
    private double avg;
    private String schedule;
    private String description;

    public Course(String course, int courseNum, String section, String title, String numCredits, String breadth,
                  String professors, double ratings, double avg, String schedules, String description) {
        this.course = course;
        this.courseNum = courseNum;
        this.section = section;
        this.title = title;
        this.numCredits = numCredits;
        this.breadth = breadth;
        this.professor = professors;
        this.rating = ratings;
        this.avg = avg;
        this.schedule = schedules;
        this.description = description;
    }

    public String getCourse() {
        return course;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public String getSection() {
        return section;
    }

    public String getTitle() {
        return title;
    }

    public String getNumCredits() {
        return numCredits;
    }

    public String getBreadth() {
        return breadth;
    }

    public String getProfessors() {
        return professor;
    }

    public double getRatings() {
        return rating;
    }

    public double getGpa() {
        return avg;
    }

    public String getSchedules() {
        return schedule;
    }

    public String getDescription() {
        return description;
    }
}
