package com.circuitbreaker.recommendationservice.model;

public class Recommendation {

    private String productId;
    private String reason;
    private double score;

    public Recommendation() {
    }

    public Recommendation(String productId, String reason, double score) {
        this.productId = productId;
        this.reason = reason;
        this.score = score;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
