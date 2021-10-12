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

    public List<Form> getFormByBroadcasterIdAndType(int broadcasterId, String type) {
        Map<String, Object> param = new HashMap<>();
        param.put("broadcasterId", broadcasterId);
        param.put("type", type);

        return formMapper.getFormByBroadcasterIdAndType(param);
    }

    public List<Form> getFormAll() {
        return formMapper.getFormAll();
    }
}
