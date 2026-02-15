package dev.mesfilmgratuits.mesfilmgratuits.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String resolution;
    private String source;
    private String audio;
    private String groupName;
    private Integer durationSeconds;
    private Integer progressSeconds;// where you left off feature

    @Column(nullable = false, unique = true, length = 1024)
    private String fullPath;

    @Column(nullable = false)
    private String filename;
    @Column(name = "release_year")
    private Integer year;
    protected MovieEntity() { } // JPA needs this

    public MovieEntity(String title, Integer year, String resolution,
                       String source, String audio, String groupName,
                       String fullPath, String filename) {
        this.title = title;
        this.year = year;
        this.resolution = resolution;
        this.source = source;
        this.audio = audio;
        this.groupName = groupName;
        this.fullPath = fullPath;
        this.filename = filename;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Integer getYear() { return year; }
    public String getResolution() { return resolution; }
    public String getSource() { return source; }
    public String getAudio() { return audio; }
    public String getGroupName() { return groupName; }
    public String getFullPath() { return fullPath; }
    public String getFilename() { return filename; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public Integer getProgressSeconds() { return progressSeconds; }

    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    public void setProgressSeconds(Integer progressSeconds) { this.progressSeconds = progressSeconds; }
    public void setTitle(String title) { this.title = title; }
    public void setYear(Integer year) { this.year = year; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public void setSource(String source) { this.source = source; }
    public void setAudio(String audio) { this.audio = audio; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public void setFilename(String filename) { this.filename = filename; }
}