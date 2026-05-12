package com.heroku.java;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@Controller
public class GettingStartedApplication {
    private final DataSource dataSource;

    @Autowired
    public GettingStartedApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/database")
    public String database(HttpServletRequest request) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            // Create table with new columns
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                    "table_timestamp_and_random_string (tick timestamp, random_string varchar(50))");

            // Insert timestamp and random string
            statement.executeUpdate("INSERT INTO table_timestamp_and_random_string VALUES " +
                    "(now(), '" + getRandomString() + "')");

            // Log statement with your name
            System.out.println("Dedi Seme - Database method accessed at: " + new Date());

            // Select all records
            ResultSet resultSet = statement.executeQuery(
                    "SELECT tick, random_string FROM table_timestamp_and_random_string");

            // Build HTML response
            StringBuilder response = new StringBuilder();
            response.append("<h1>Database Records</h1>");
            response.append("<table border='1'>");
            response.append("<tr><th>Timestamp</th><th>Random String</th></tr>");

            while (resultSet.next()) {
                response.append("<tr>");
                response.append("<td>").append(resultSet.getTimestamp("tick")).append("</td>");
                response.append("<td>").append(resultSet.getString("random_string")).append("</td>");
                response.append("</tr>");
            }

            response.append("</table>");
            return response.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Add this helper method to generate random strings
    private String getRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(GettingStartedApplication.class, args);
    }
}
