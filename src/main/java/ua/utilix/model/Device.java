package ua.utilix.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Device {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private String sigfoxName = "";
    private String sigfoxId = "";

    public Device() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSigfoxName() {
        return sigfoxName;
    }

    public void setSigfoxName(String sigfoxName) {
        this.sigfoxName = sigfoxName;
    }

    public String getSigfoxId() {
        return sigfoxId;
    }

    public void setSigfoxId(String sigfoxId) {
        this.sigfoxId = sigfoxId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

}
