
package com.project;
import com.project.Time;
public class Time {
    private Integer hours;
    private Integer mins;
    private int as;

    public Time(Integer hours, Integer mins) {
        this.hours = hours;
        this.mins = mins;
    }
    public Time(String reg) {
        String[] words = reg.split(":");
        int h = Integer.parseInt(words[0]);

        int m =Integer.parseInt(words[1]);

        this.hours = h;
        this.mins = m;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMins() {
        return mins;
    }

    public void setMins(Integer mins) {
        this.mins = mins;
    }
}
