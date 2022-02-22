package ua.utilix.model;

import ua.utilix.model.strategy.DefaultStrategy;
import ua.utilix.model.strategy.ParkingPillStrategy;
import ua.utilix.model.strategy.ParkingSensorStrategy;

public class SigfoxParser {

    public SigfoxData getData(String id, String input, String dev, int sequence) throws Exception {
        SigfoxData sigfoxData = new SigfoxData();
        try {
            if (dev.equals("ParkingPill")) sigfoxData.setStrategy(new ParkingPillStrategy(sigfoxData));
            else if (dev.equals("ParkingSensor")) sigfoxData.setStrategy(new ParkingSensorStrategy(sigfoxData));
            else sigfoxData.setStrategy(new DefaultStrategy(sigfoxData));
            sigfoxData = sigfoxData.getStrategy().parse(id, input, sequence);
        } catch (Exception ex) {
            throw new Exception("Error input data id: " + id);
        }
        return sigfoxData;
    }
}
