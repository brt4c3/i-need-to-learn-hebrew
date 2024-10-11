package com.example;

public class AudioProcessingRequest {
    private String trainDir;
    private String langDir;
    private String modelDir;
    private String praatScript;
    private String mongoUri;
    private String dbName;
    private String collectionName;

    // Getters and setters
    public String getTrainDir() {
        return trainDir;
    }

    public void setTrainDir(String trainDir) {
        this.trainDir = trainDir;
    }

    public String getLangDir() {
        return langDir;
    }

    public void setLangDir(String langDir) {
        this.langDir = langDir;
    }

    public String getModelDir() {
        return modelDir;
    }

    public void setModelDir(String modelDir) {
        this.modelDir = modelDir;
    }

    public String getPraatScript() {
        return praatScript;
    }

    public void setPraatScript(String praatScript) {
        this.praatScript = praatScript;
    }

    public String getMongoUri() {
        return mongoUri;
    }

    public void setMongoUri(String mongoUri) {
        this.mongoUri = mongoUri;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}