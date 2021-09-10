package me.cocoblue.twitchwebhook.mapper;

import me.cocoblue.twitchwebhook.dto.Form;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FormMapper {
    List<Form> getStartFormByBroadcasterIdAndType(Map<String, Object> param);
    List<Form> getEndFormByBroadcasterIdAndType(Map<String, Object> param);
    List<Integer> getAllBroadcasterId();
}
