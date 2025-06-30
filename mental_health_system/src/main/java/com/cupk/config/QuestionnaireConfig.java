package com.cupk.config;

// src/main/java/com/cupk/config/QuestionnaireConfig.java
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "questionnaire")
public class QuestionnaireConfig {

    private List<Question> questions;

    public QuestionnaireConfig() {
        // Enable comments in the JSON parser
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ClassPathResource resource = new ClassPathResource("questionnaire.json");
        try (InputStream inputStream = resource.getInputStream()) {
            questions = mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, Question.class));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load questionnaire configuration", e);
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public static class Question {
        private int id;
        private String text;
        private String type;
        private List<String> options;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }
    }
}