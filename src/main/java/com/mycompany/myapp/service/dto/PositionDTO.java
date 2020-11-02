package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.service.view.PositionView;

import java.time.LocalDateTime;

public class PositionDTO implements PositionView {

    private Long id;
    private int duration;
    private String description;
    private LocalDateTime createdAt;
    private boolean status;
    private JobTypeDTO jobType;

    public PositionDTO() {
    }

    public PositionDTO(Long id, int duration, String description, LocalDateTime createdAt, boolean status, JobTypeDTO jobType) {
        this.id = id;
        this.duration = duration;
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
        this.jobType = jobType;
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
}
