package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.dao.FormDao;
import me.cocoblue.twitchwebhook.dto.Form;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FormServiceImpl implements FormService {
    private final FormDao formDao;

    @Override
    public List<Form> getStartFormByBroadcasterIdAndType(int broadcasterId, int type) {
        return formDao.getStartFormByBroadcasterIdAndType(broadcasterId, type);
    }

    @Override
    public List<Form> getEndFormByBroadcasterIdAndType(int broadcasterId, int type) {
        return formDao.getEndFormByBroadcasterIdAndType(broadcasterId, type);
    }
}
