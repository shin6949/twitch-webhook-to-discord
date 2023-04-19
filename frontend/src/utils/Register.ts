export const getTwitchIDSearchResult = async (twitchID: string): Promise<Response> => {
  return await fetch(`/api/twitch/id-search?name=${twitchID}`);
};

export const postTwitchNotificationRegister = async (data: { twitchID: string; notificationType: string; delayTime: number }): Promise<Response> => {
  return await fetch(`/api/twitch/notification/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
};

export const getNotificationTypes = async (): Promise<NotificationType[]> => {
  const response = await fetch(`/api/twitch/notification/types`);
  const data = await response.json();
  return data;
};

export interface NotificationType {
  value: string;
  name: string;
}
