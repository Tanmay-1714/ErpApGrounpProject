// File: edu.univ.erp.domain.Section.java (Updated with Getters/Setters)

package edu.univ.erp.domain;

/**
 * Represents a class section from the university_erp_db.sections table.
 */
public class Section {
    private int sectionId;
    private int courseId;
    private int instructorId;
    private String day;
    private String time;
    private String room;
    private int capacity;
    private String semester;
    private int year;

    // Non-table fields for convenience (filled by SectionDAO)
    public Course course;
    public Instructor instructor;

    // Constructor (using all fields from sections table)
    public Section(int sectionId, int courseId, int instructorId, String day, String time, String room, int capacity, String semester, int year) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.day = day;
        this.time = time;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
    }

    // --- GETTERS (The methods your UI is looking for!) ---

    public int getSectionId() { return sectionId; }
    public int getCourseId() { return courseId; }
    public int getInstructorId() { return instructorId; }
    public String getDay() { return day; }
    public String getTime() { return time; }
    public String getRoom() { return room; }
    public int getCapacity() { return capacity; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }

    // --- SETTERS ---
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public void setDay(String day) { this.day = day; }
    public void setTime(String time) { this.time = time; }
    public void setRoom(String room) { this.room = room; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setYear(int year) { this.year = year; }

    // Helper method for display (used in RegisterDropDialog)
    public String getCourseCode() {
        return (course != null) ? course.getCode() : "N/A";
    }
}