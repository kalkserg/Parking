package ua.utilix.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Location {
    @Id
    @GeneratedValue
    private Long id;
    private String sigfoxId = "";
    private String protocol = "";
    private String addr = "";
    private Integer stateId;
    private boolean park;

    private double battery;
    private int temperature;
    private int carCount;
    private Sigfox.TypeError errorMagnet;
    private Sigfox.TypeError errorBatteryAlarm;
    private Sigfox.TypeError errorDamage;
    private Sigfox.TypeError errorUnknown;


    public Location() {
    }

    public Location(Integer stateId) {
        this.stateId = stateId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSigfoxId() {
        return sigfoxId;
    }

    public void setSigfoxId(String sigfoxId) {
        this.sigfoxId = sigfoxId;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public boolean isPark() {
        return park;
    }

    public void setPark(boolean park) {
        this.park = park;
    }

    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getCarCount() {
        return carCount;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }

    public Sigfox.TypeError getErrorMagnet() {
        return errorMagnet;
    }

    public void setErrorMagnet(Sigfox.TypeError errorMagnet) {
        this.errorMagnet = errorMagnet;
    }

    public Sigfox.TypeError getErrorBatteryAlarm() {
        return errorBatteryAlarm;
    }

    public void setErrorBatteryAlarm(Sigfox.TypeError errorBatteryAlarm) {
        this.errorBatteryAlarm = errorBatteryAlarm;
    }

    public Sigfox.TypeError getErrorDamage() {
        return errorDamage;
    }

    public void setErrorDamage(Sigfox.TypeError errorDamage) {
        this.errorDamage = errorDamage;
    }

    public Sigfox.TypeError getErrorUnknown() {
        return errorUnknown;
    }

    public void setErrorUnknown(Sigfox.TypeError errorUnknown) {
        this.errorUnknown = errorUnknown;
    }
}
