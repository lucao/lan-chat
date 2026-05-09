package br.gov.dataprev.lanchat.service;

import br.gov.dataprev.lanchat.model.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class StorageService {

    private static final String PREF_NICKNAME = "nickname";
    private static final Preferences PREFS = Preferences.userNodeForPackage(StorageService.class);
    private static final Path DATA_DIR = Paths.get(System.getProperty("user.home"), ".lan-chat");
    private static final Path HISTORY_DIR = DATA_DIR.resolve("history");
    private static final Path FILES_DIR = DATA_DIR.resolve("received-files");

    private final ObjectMapper mapper;

    public StorageService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            Files.createDirectories(HISTORY_DIR);
            Files.createDirectories(FILES_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String loadNickname() { return PREFS.get(PREF_NICKNAME, null); }
    public void saveNickname(String nickname) { PREFS.put(PREF_NICKNAME, nickname); }

    public List<Message> loadHistory(String peerNickname) {
        File f = historyFile(peerNickname);
        if (!f.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(f, new TypeReference<>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void saveHistory(String peerNickname, List<Message> messages) {
        try {
            mapper.writeValue(historyFile(peerNickname), messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getReceivedFilesDir() { return FILES_DIR; }

    private File historyFile(String peerNickname) {
        String safe = peerNickname.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        return HISTORY_DIR.resolve(safe + ".json").toFile();
    }
}
