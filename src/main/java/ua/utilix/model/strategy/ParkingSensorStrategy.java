package ua.utilix.model.strategy;

import ua.utilix.model.SigfoxData;

public class ParkingSensorStrategy extends DefaultStrategy {

    public ParkingSensorStrategy(SigfoxData sigfoxData) {
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
            case VD:
                park = (bytes[2] & 0xFF) == 1;
                sigfoxData.setPark(park);
                sigfoxData.setType(TypeMessage.VD);
                break;

            //....
        }

        sigfoxData.setMessage(input);
        sigfoxData.setId(id);

        return sigfoxData;
    }


    private TypeMessage getType(byte bb) {
        int itype = (bb & 0xFF);
        if (itype == 26) return TypeMessage.VD; // Vehicle detection
        return null;
    }
}
