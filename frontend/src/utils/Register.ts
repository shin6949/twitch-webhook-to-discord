export const getTwitchIDSearchResult = async (twitchID: string): Promise<boolean> => {
  const result = await fetch(`/api/twitch/id-search?name=${twitchID}`);
  const data: TwitchIDSearchResponse = await result.json();

  return data.result;
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
  return await response.json();
};

export interface NotificationType {
  value: string;
  name: string;
}

export interface TwitchIDSearchResponse {
  result: boolean;
}
