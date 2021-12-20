package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.entity.StreamNotifyFormEntity;
import me.cocoblue.twitchwebhook.repository.StreamNotifyFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FormServiceImpl implements FormService {
    private final StreamNotifyFormRepository streamNotifyFormRepository;

    @Override
    public List<StreamNotifyFormEntity> getFormByBroadcasterIdAndType(Long broadcasterId, String type) {

        return streamNotifyFormRepository.getStreamNotifyFormsByBroadcasterIdAndType(
                        BroadcasterIdEntity.builder().id(broadcasterId).build(), type);
    }

    @Override
    public List<StreamNotifyFormEntity> getFormAll() {
        return streamNotifyFormRepository.findAll();
    }
}
