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
  const snakeCaseData = objectKeysToSnakeCase(data);
  console.log(snakeCaseData);

  return await fetch(`/api/register/twitch/notification/submit`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ snakeCaseData }),
  });
};

export const getNotificationTypes = async (): Promise<NotificationType[]> => {
  const response = await fetch(`/api/register/twitch/notification/types`);
  return await response.json();
};

const toSnakeCase = (str: string) => {
  return str.replace(/[A-Z]/g, (letter) => `_${letter.toLowerCase()}`);
};

const objectKeysToSnakeCase = (obj: Record<string, unknown>) => {
  return Object.entries(obj).reduce((acc, [key, value]) => {
    acc[toSnakeCase(key)] = value;
    return acc;
  }, {} as Record<string, unknown>);
};
