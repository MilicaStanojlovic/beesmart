package com.beesmart.service.services;

import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class TemplateService {

    /**
     * Load a DRT template with CSV data and return a KieSession.
     *
     * @param templatePath  path to .drt file (e.g., "/rules/templates/treatment-template.drt")
     * @param csvPath       path to .csv file (e.g., "/rules/templates/treatment-data.csv")
     * @return KieSession with generated rules ready to fire
     */
    public KieSession createSessionFromTemplate(String templatePath, String csvPath) {
        // 1. Read CSV data into list of maps
        List<Map<String, String>> data = readCsv(csvPath);

        // 2. Read template
        InputStream templateStream = getClass().getResourceAsStream(templatePath);

        // 3. Compile template with data
        ObjectDataCompiler compiler = new ObjectDataCompiler();
        String drl = compiler.compile(convertToMapList(data), templateStream);

        System.out.println("=== Generated DRL from template ===");
        System.out.println(drl);

        // 4. Build KieSession with generated rules
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/rules/generated-template-rules.drl", drl);

        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();

        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Template compilation errors: " +
                    kb.getResults().getMessages());
        }

        KieContainer kc = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        return kc.newKieSession();
    }

    /**
     * Simpler method: generate DRL string from template + CSV
     * You can then add this to your existing KieSession
     */
    public String generateRulesFromTemplate(String templatePath, String csvPath) {
        List<Map<String, String>> data = readCsv(csvPath);
        InputStream templateStream = getClass().getResourceAsStream(templatePath);
        ObjectDataCompiler compiler = new ObjectDataCompiler();
        return compiler.compile(convertToMapList(data), templateStream);
    }

    private List<Map<String, String>> readCsv(String csvPath) {
        List<Map<String, String>> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(csvPath)))) {

            String headerLine = br.readLine();
            if (headerLine == null) return result;

            String[] headers = headerLine.split(",");
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i].trim(), values[i].trim());
                }
                result.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV: " + csvPath, e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> convertToMapList(List<Map<String, String>> data) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, String> row : data) {
            Map<String, Object> objectRow = new HashMap<>(row);
            result.add(objectRow);
        }
        return result;
    }
}
