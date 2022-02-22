package ua.utilix.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.utilix.model.Device;
import ua.utilix.repo.DeviceRepository;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional(readOnly = true)
    public Device[] findByChatId(long chatId) {
        return deviceRepository.findByChatId(chatId);
    }

    @Transactional(readOnly = true)
    public Device findById(long id) {
        return deviceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Device[] findBySigfoxId(String sigfoxId) {
        System.out.println(deviceRepository.findBySigfoxId(sigfoxId));
        return deviceRepository.findBySigfoxId(sigfoxId);
    }

    @Transactional(readOnly = true)
    public Device findByChatIdAndSigfoxId(long chatId, String sigfoxId) {
        return deviceRepository.findByChatIdAndSigfoxId(chatId,sigfoxId);
    }

    @Transactional(readOnly = true)
    public Device findByIdAndChatId(long Id, long chatId) {
        return deviceRepository.findByIdAndChatId(Id,chatId);
    }


    @Transactional(readOnly = true)
    public List<Device> findAllDevices() {
        return deviceRepository.findAll();
    }

    @Transactional
    public void addDevice(Device device) {
        deviceRepository.save(device);
    }

    @Transactional
    public void updateDevice(Device device) {
        deviceRepository.save(device);
    }

    @Transactional
    public void delDevice(Device device) {
        deviceRepository.delete(device);
    }
}

