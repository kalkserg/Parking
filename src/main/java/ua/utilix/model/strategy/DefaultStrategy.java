package ua.utilix.model.strategy;


import ua.utilix.model.Sigfox;
import ua.utilix.model.SigfoxData;

public class DefaultStrategy implements Sigfox {

    SigfoxData sigfoxData;

    public DefaultStrategy(SigfoxData sigfoxData) {
        this.sigfoxData = sigfoxData;
        sigfoxData.setErrorMagnet(TypeError.NOERROR);
        sigfoxData.setErrorBatteryAlarm(TypeError.NOERROR);
        sigfoxData.setErrorDamage(TypeError.NOERROR);
        sigfoxData.setErrorUnknown(TypeError.NOERROR);
        sigfoxData.setStartUp(false);
        sigfoxData.setIntgCmpl(false);
        sigfoxData.setCmdOk(false);
        sigfoxData.setBattery(0);
        sigfoxData.setTemperature(0);
        sigfoxData.setCarCount(0);
    }

    public static byte[] asBytes(String s) {
        String tmp;
        byte[] b = new byte[s.length() / 2];
        int i;
        for (i = 0; i < s.length() / 2; i++) {
            tmp = s.substring(i * 2, i * 2 + 2);
            b[i] = (byte) (Integer.parseInt(tmp, 16) & 0xff);
        }
        return b;                                            //return bytes
    }

    public static String asString(byte[] b) {
        char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[b.length * 2];
        for (int j = 0; j < b.length; j++) {
            int v = b[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static boolean isBitSet(byte num, int position) {
        return (((num >> (position - 1)) & 1) == 1);
    }
}
