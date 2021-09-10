package me.cocoblue.twitchwebhook.dao;

import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.mapper.FormMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FormDao {
    private final FormMapper formMapper;

    public FormDao(FormMapper formMapper) {
        this.formMapper = formMapper;
    }

    public List<Form> getStartFormByBroadcasterIdAndType(int broadcasterId, int type) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("broadcasterId", broadcasterId);
        param.put("type", type);

        return formMapper.getStartFormByBroadcasterIdAndType(param);
    }

    public List<Form> getEndFormByBroadcasterIdAndType(int broadcasterId, int type) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("broadcasterId", broadcasterId);
        param.put("type", type);

        return formMapper.getEndFormByBroadcasterIdAndType(param);
    }


    public List<Integer> getAllBroadcasterId() {
        return formMapper.getAllBroadcasterId();
    }
}
