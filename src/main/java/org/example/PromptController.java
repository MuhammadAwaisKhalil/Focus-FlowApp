package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;


public class PromptController {
    @FXML
    private TextArea promptBox;
    private DashboardController controller;
    private static String apiKey;
    private static String model = "gemini-2.5-flash";
    // to remember the trait of a task
    private static HashMap<String,String> traitRecorder = new HashMap<>();

    public PromptController() {
        Dotenv dotenv = Dotenv.load();
        apiKey = dotenv.get("GEMINI_API_KEY");
    }


    public void setDashboardController(DashboardController controller) {
        this.controller = controller;
    }

    @FXML
    private void makeSchedule(ActionEvent e) throws IOException {
        String userPrompt = promptBox.getText();
        if (userPrompt.trim().isEmpty()) {
            System.out.println("Enter a vaid prompt");
            return;
        }
        new Thread(()->{
            String aiResponse = getAISchedule(userPrompt);
            Platform.runLater(()->{
                try {
                    controller.renderSchedule(aiResponse);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }).start();

    }

    private String getAISchedule(String userPrompt) {


        String endpointURL = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        String instructions = "You are an expert study assistant who knows how to make te most optimized " +
                " study schedules based on user demand. You must respond only with a raw JSON array. Do Not use " +
                "mark down blocks like ```json. Use this exact format: [{\"subject\":\"Name\", \"duration\":60, \"color\":\"#hexcode\", \"row\":9, \"col\":1}]" +
                "The column ranges from 1-5(Monday-Friday) and rows from 1-25 (12am - 11pm). One column in one day." +
                "User request: ";

        String userInput = userPrompt.replace("\"", "\\\"").replace("\n"," ");
        String prompt = instructions + userInput;
        String safePrompt = prompt.replace("\"","\\\"").replace("\n"," ");

        String jsonFormat = "{\n" +
                "\"contents\":[{\n" +
                "\"parts\":[{\"text\":\"" + safePrompt + "\"}]\n" +
                "}]\n" +
                "}";
        new Thread(()->{
            Platform.runLater(()->{

            });
        }).start();
        try {
            // create the client
            HttpClient client = HttpClient.newHttpClient();
            //create request to API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpointURL))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonFormat))
                    .build();


            // getting response from API

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Error: "+response.body());
            JSONObject responseJson = new JSONObject(response.body());

            String aiResponse = responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            return aiResponse.replace("```json","").replace("```","").replace("\n"," ").trim();


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static String getTraitNameFromAI(String subject){
        if(traitRecorder.containsKey(subject)){
            return traitRecorder.get(subject);
        }

        String endpointURL="https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        String instruction="Categorize the study subject '" + subject + "' into EXACTLY ONE of these 8 traits: " +
                "\"Logic, Knowledge, Creativity, Communication, Memory, Strategy, Organization, Focus.\"" +
                "Only return one of these 8 traits that best fits the given subject. Do not give any other information" +
                "apart from the trait and it should not have any punctuation, spaces or special characters.";
        String safePrompt = instruction.replace("\"","\\\"").replace("\n"," ");

        String jsonRequest = "{\"contents\":[{\"parts\":[{\"text\":\"" + safePrompt+"\"}]}]}";

        try{
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpointURL))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            JSONObject task = new JSONObject(response.body());
            String traitName = task.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            traitRecorder.put(subject,traitName);
            if(traitName!=null)
                return traitName;
            return "Focus";
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return "Focus";
        }


    }
    public static String getSummaryFromAI(String prompt){
       JSONObject textPart = new JSONObject();
       textPart.put("text",prompt);

       JSONArray partsArray = new JSONArray();
       partsArray.put(textPart);

       JSONObject contentsPart = new JSONObject();
       contentsPart.put("parts",partsArray);

       JSONArray contentsArray = new JSONArray();
       contentsArray.put(contentsPart);

       JSONObject requestBody = new JSONObject();
       requestBody.put("contents",contentsArray);

       String jsonPrompt = requestBody.toString();

       String endpointURL="https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;
       try{
           HttpClient client = HttpClient.newHttpClient();

           HttpRequest request = HttpRequest.newBuilder()
                   .uri(URI.create(endpointURL))
                   .header("Content-Type","application/json")
                   .POST(HttpRequest.BodyPublishers.ofString(jsonPrompt))
                   .build();

           HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

           JSONObject jsonSummary = new JSONObject(response.body());

           String summary = jsonSummary.getJSONArray("candidates")
                   .getJSONObject(0)
                   .getJSONObject("content")
                   .getJSONArray("parts")
                   .getJSONObject(0)
                   .getString("text");

           if(summary==null){
               throw new NullPointerException("Summary not returned");
           }
           return summary;
       } catch (Exception e) {
           System.out.println(e.fillInStackTrace().getMessage());
       }

        return "No task Loaded";
    }
    public static ArrayList<QuizQuestion> generateQuizFromAI(String topic,int numberOfQuestions){
        String prompt = "You are an expert test creator. Create a " + numberOfQuestions +
                "-question multiple choice quiz on the topic: '" + topic + "'. " +
                "You MUST respond ONLY with a raw, valid JSON array. Do not include markdown formatting, " +
                "do not include ```json tags, and do not say anything else. " +
                "The JSON MUST follow this exact structure: " +
                "[" +
                "  {" +
                "    \"question\": \"What is the powerhouse of the cell?\"," +
                "    \"options\": [\"Nucleus\", \"Mitochondria\", \"Ribosome\", \"Membrane\"]," +
                "    \"correctIndex\": 1" +
                "  }" +
                "]";
        String AIresponse = getSummaryFromAI(prompt);
        String cleanJson = AIresponse.replace("```json", "").replace("```", "").trim();

        ArrayList<QuizQuestion> quiz = new ArrayList<>();

        try{
            JSONArray quizArray = new JSONArray(cleanJson);

            for(int i=0;i<quizArray.length();i++){
                JSONObject questionJSON=quizArray.getJSONObject(i);
                String question = questionJSON.getString("question");
                int correctOption = questionJSON.getInt("correctIndex");

                String[] options = new String[4];
                JSONArray optionJSON = questionJSON.getJSONArray("options");
                for(int j=0;j<4;j++){
                    options[j] = optionJSON.getString(j);
                }

                quiz.add(new QuizQuestion(question,options,correctOption));
            }

        } catch (Exception e) {
            System.out.println("Problem in Quiz section. "+e.getMessage());
        }
        return quiz;

    }
}