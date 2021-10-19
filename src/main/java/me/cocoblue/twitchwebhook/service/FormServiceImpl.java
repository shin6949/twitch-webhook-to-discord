package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.entity.BroadcasterId;
import me.cocoblue.twitchwebhook.entity.StreamNotifyForm;
import me.cocoblue.twitchwebhook.repository.StreamNotifyFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FormServiceImpl implements FormService {
    private final StreamNotifyFormRepository streamNotifyFormRepository;

    @Override
    public List<StreamNotifyForm> getFormByBroadcasterIdAndType(Long broadcasterId, String type) {

        return streamNotifyFormRepository.getStreamNotifyFormsByBroadcasterIdAndType(
                        BroadcasterId.builder().id(broadcasterId).build(), type);
    }

    @Override
    public List<StreamNotifyForm> getFormAll() {
        return streamNotifyFormRepository.findAll();
    }
}
