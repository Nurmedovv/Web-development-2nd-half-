package by.bsu.lab5.session;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserSession {

    private final String id;
    private final LocalDateTime createdAt;
    private int visitCount;
    private LocalDateTime lastVisit;
    private String previousVisit;

    public UserSession() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public LocalDateTime getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(LocalDateTime lastVisit) {
        this.lastVisit = lastVisit;
    }

    public String getPreviousVisit() {
        return previousVisit;
    }

    public void setPreviousVisit(String previousVisit) {
        this.previousVisit = previousVisit;
    }
}
