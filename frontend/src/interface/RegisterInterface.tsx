import { TwitchUser } from "./common/TwitchUser";

export interface NotificationType {
  value: string;
  name: string;
}

export interface TwitchIDSearchResponse {
  result: boolean;
  is_live: boolean;
  user?: TwitchUser;
}

export interface TwitchNotificationRegisterResponse {
  result: boolean;
}
