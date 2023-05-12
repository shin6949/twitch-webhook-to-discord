import {
  NotificationType,
  TwitchIDSearchResponse,
} from "../interface/RegisterInterface";

export const getTwitchIDSearchResult = async (
  twitchID: string
): Promise<TwitchIDSearchResponse> => {
  const result = await fetch(`/api/register/twitch/id-search?name=${twitchID}`);
  return await result.json();
};

export const postTwitchNotificationRegister = async (data: {
  twitchID: string;
  notificationType: string;
  delayTime: number;
  registrationToken: string;
}): Promise<Response> => {
  return await fetch(`/api/register/twitch/notification/submit`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      twitch_id: data.twitchID,
      notification_type: data.notificationType,
      delay_time: data.delayTime,
      registration_token: data.registrationToken,
    }),
  });
};

export const getNotificationTypes = async (): Promise<NotificationType[]> => {
  const response = await fetch(`/api/register/twitch/notification/types`);
  return await response.json();
};
