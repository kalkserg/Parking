package ua.utilix.model;


public class SigfoxData implements Sigfox {
    private Sigfox strategy;
    private TypeMessage type;
    private int sendCounter;
    private String message;
    private String id;

    private boolean startUp;
    private boolean intgCmpl;
    private boolean cmdOk;
    private double battery;
    private int temperature;
    private boolean park;
    private int carCount;
    private TypeError errorMagnet;
    private TypeError errorBatteryAlarm;
    private TypeError errorDamage;
    private TypeError errorUnknown;


    public void setBattery(int btt) {
        this.battery = btt * 6.7;
    }

    public double getBattery() {
        return battery;
    }

    public String getStringBattery() {
        return getBattery() + " %";
    }

    public boolean isStartUp() {
        return startUp;
    }

    public void setStartUp(boolean startUp) {
        this.startUp = startUp;
    }

    public boolean isIntgCmpl() {
        return intgCmpl;
    }

    public void setIntgCmpl(boolean intgCmpl) {
        this.intgCmpl = intgCmpl;
    }

    public boolean isCmdOk() {
        return cmdOk;
    }

    public void setCmdOk(boolean cmdOk) {
        this.cmdOk = cmdOk;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature * 5 - 20;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getStringTemperature() {
        return getTemperature() + " " + '\u00B0' + "C";
    }

    public int getCarCount() {
        return carCount;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }

    public TypeError getErrorMagnet() {
        return errorMagnet;
    }

    public void setErrorMagnet(TypeError errorMagnet) {
        this.errorMagnet = errorMagnet;
    }

    public TypeError getErrorDamage() {
        return errorDamage;
    }

    public void setErrorDamage(TypeError errorDamage) {
        this.errorDamage = errorDamage;
    }

    public TypeError getErrorBatteryAlarm() {
        return errorBatteryAlarm;
    }

    public void setErrorBatteryAlarm(TypeError errorBatteryAlarm) {
        this.errorBatteryAlarm = errorBatteryAlarm;
    }

    public TypeError getErrorUnknown() {
        return errorUnknown;
    }

    public void setErrorUnknown(TypeError errorUnknown) {
        this.errorUnknown = errorUnknown;
    }


    public Sigfox getStrategy() {
        return strategy;
    }

    public void setStrategy(Sigfox strategy) {
        this.strategy = strategy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TypeMessage getType() {
        return type;
    }

    public void setType(TypeMessage type) {
        this.type = type;
    }

    public int getSendCounter() {
        return sendCounter;
    }

    public void setSendCounter(int sendCounter) {
        this.sendCounter = sendCounter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPark() {
        return park;
    }

    public void setPark(boolean park) {
        this.park = park;
    }

    @Override
    public String toString() {
        if (type == TypeMessage.SYSTEM)
//            return "ParkingPill " + id + ". " + type + ". StartUp " + isStartUp() + ". IntgCmpl " + isIntgCmpl() + ". CmdOk " + isCmdOk();
            return " StartUp " + isStartUp() + ". IntgCmpl " + isIntgCmpl() + ". CmdOk " + isCmdOk();
        else if (type == TypeMessage.BTT)
//            return "ParkingPill " + id + ". " + type + ". Parking is " + (isPark()?"BUSY":"FREE") + ". Battery " + getStringBattery();
            return "Parking is " + (isPark()?"BUSY":"FREE") + ". Battery " + getStringBattery();
        else if (type == TypeMessage.TMP)
//            return "ParkingPill " + id + ". " + type + ". Parking is " + (isPark()?"BUSY":"FREE") + ". Temperature " + getStringTemperature();
            return "Parking is " + (isPark()?"BUSY":"FREE") + ". Temperature " + getStringTemperature();
        else if (type == TypeMessage.CC)
//            return "ParkingPill " + id + ". " + type + ". Сar count " + getCarCount();
            return "Сar count " + getCarCount();
        else if (type == TypeMessage.ERR)
//            return "ParkingPill " + id + ". " + type + ". " + getErrorMagnet() + getErrorBatteryAlarm()  + getErrorDamage()  + getErrorUnknown();
            return type + ". " + getErrorMagnet() + getErrorBatteryAlarm()  + getErrorDamage()  + getErrorUnknown();
        else if (type == TypeMessage.VD)
//            return "ParkingPill " + id + ". " + type + ". " + getErrorMagnet() + getErrorBatteryAlarm()  + getErrorDamage()  + getErrorUnknown();
            return "Parking is " + (isPark()?"BUSY":"FREE");
        return id + ". Невідомий тип повідомлення. Данні: " + message;
    }
}
