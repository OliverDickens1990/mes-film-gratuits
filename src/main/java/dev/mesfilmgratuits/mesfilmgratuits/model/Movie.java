package dev.mesfilmgratuits.mesfilmgratuits.model;

public class Movie {

    private String title;
    private Integer year;
    private String resolution;
    private String source;
    private String audio;
    private String group;
    private String filename;

    public Movie(String title, Integer year, String resolution,
                 String source, String audio, String group, String filename) {
        this.title = title;
        this.year = year;
        this.resolution = resolution;
        this.source = source;
        this.audio = audio;
        this.group = group;
        this.filename = filename;
    }

    public String getTitle() { return title; }
    public Integer getYear() { return year; }
    public String getResolution() { return resolution; }
    public String getSource() { return source; }
    public String getAudio() { return audio; }
    public String getGroup() { return group; }
    public String getFilename() { return filename; }
}