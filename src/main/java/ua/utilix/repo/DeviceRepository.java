package ua.utilix.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.utilix.model.Device;

import java.util.List;


public interface DeviceRepository extends JpaRepository<Device, Long> {

    Device findById(long id);

    Device[] findByChatId(long chatId);

    Device findByChatIdAndSigfoxId(long chatId, String sigfoxId);

    Device findByIdAndChatId(long Id, long chatId);

    Device[] findBySigfoxId(String sigfoxId);

}
