package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.PositionView;

import java.time.LocalDateTime;

public class PositionDTO implements PositionView {

    private Long id;
    private int duration;
    private String description;
    private int placesNumber;
    private float remuneration;
    private LocalDateTime createdAt;
    private boolean status;
    private JobTypeDTO jobType;
    private MissionUserDTO mission;

    public PositionDTO() {
    }

    public PositionDTO(Long id, int duration, String description, int placesNumber, float remuneration, LocalDateTime createdAt, boolean status, JobTypeDTO jobType, MissionUserDTO mission) {
        this.id = id;
        this.duration = duration;
        this.description = description;
        this.placesNumber = placesNumber;
        this.remuneration = remuneration;
        this.createdAt = createdAt;
        this.status = status;
        this.jobType = jobType;
        this.mission = mission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public JobTypeDTO getJobType() {
        return jobType;
    }

    public void setJobType(JobTypeDTO jobType) {
        this.jobType = jobType;
    }

    public MissionUserDTO getMission() {
        return mission;
    }

    public void setMission(MissionUserDTO mission) {
        this.mission = mission;
    }

    public int getPlacesNumber() {
        return placesNumber;
    }

    public void setPlacesNumber(int placesNumber) {
        this.placesNumber = placesNumber;
    }

    public float getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(float remuneration) {
        this.remuneration = remuneration;
    }
}
