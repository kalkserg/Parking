package ua.utilix.model.strategy;

import ua.utilix.model.SigfoxData;

public class ParkingPillStrategy extends DefaultStrategy {

    public ParkingPillStrategy(SigfoxData sigfoxData) {
        super(sigfoxData);
    }

    public SigfoxData parse(String id, String input, int sequence) {

        byte[] bytes = asBytes(input);
//        byte incomeByte = bytes[bytes.length - 1];
        byte incomeByte = bytes[0];

        boolean startUp;
        boolean intgCmp;
        boolean cmdOk;
        int battery = 0;
        int temp = 0;
        boolean park;
        int carCount = 0;
        boolean errorMagnet;
        boolean errorBatteryAlarm = false;
        boolean errorDamage = false;
        boolean errorUnknown = false;

        switch (getType(incomeByte)) {
            case SYSTEM:
                startUp = (incomeByte & 0x01) == 1;
                intgCmp = ((incomeByte & 0x02) >> 1) == 1;
                cmdOk = ((incomeByte & 0x04) >> 1) == 1;
                sigfoxData.setStartUp(startUp);
                sigfoxData.setIntgCmpl(intgCmp);
                sigfoxData.setCmdOk(cmdOk);
                sigfoxData.setType(TypeMessage.SYSTEM);
                break;

            case BTT:
                park = (incomeByte & 0x01) == 1;
                battery = (incomeByte & 0x1E) >> 1;
                sigfoxData.setPark(park);
                sigfoxData.setBattery(battery);
                sigfoxData.setType(TypeMessage.BTT);
                break;

            case TMP:
                park = (incomeByte & 0x01) == 1;
                temp = (incomeByte & 0x1E) >> 1;
                sigfoxData.setPark(park);
                sigfoxData.setTemperature(temp);
                sigfoxData.setType(TypeMessage.TMP);
                break;

            case CC:
                carCount = incomeByte & 0x1F;
                sigfoxData.setCarCount(carCount);
                sigfoxData.setType(TypeMessage.CC);
                break;

            case ERR:
                errorMagnet = (incomeByte & 0x01) == 1;
                errorBatteryAlarm = (incomeByte & 0x02) == 2;
                errorDamage = (incomeByte & 0x04) == 4;
                errorUnknown = (incomeByte & 0x08) == 8;
                if (errorMagnet) sigfoxData.setErrorMagnet(TypeError.MAG);
                if (errorBatteryAlarm) sigfoxData.setErrorBatteryAlarm(TypeError.BATT);
                if (errorDamage) sigfoxData.setErrorDamage(TypeError.PHOTO);
                if (errorUnknown) sigfoxData.setErrorUnknown(TypeError.WDOG);
                sigfoxData.setType(TypeMessage.ERR);
                break;
        }

        sigfoxData.setMessage(input);
        sigfoxData.setId(id);

        return sigfoxData;
    }


    private TypeMessage getType(byte bb) {
        int itype = (bb & 0xE0) >> 5;
        if (itype == 0) return TypeMessage.SYSTEM;
        else if (itype == 1) return TypeMessage.BTT;
        else if (itype == 2) return TypeMessage.TMP;
        else if (itype == 4) return TypeMessage.CC;
        else if (itype == 7) return TypeMessage.ERR;

        return null;
    }
}
