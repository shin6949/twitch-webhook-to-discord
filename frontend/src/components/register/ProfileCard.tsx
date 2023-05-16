import { TwitchUser } from "../../interface/common/TwitchUser";
import { Card } from "react-bootstrap";
import { useTranslation } from "next-i18next";

interface ProfileProps {
  twitchUser: TwitchUser;
  isLive: boolean;
}

const ProfileCard = ({ twitchUser, isLive }: ProfileProps): JSX.Element => {
  const { t } = useTranslation(["register", "common"]);

  return (
    <Card style={{ width: "20rem" }}>
      <div
        style={{
          display: "flex",
          alignItems: "center",
        }}
      >
        <Card.Img
          variant="top"
          src={twitchUser.profile_image_url}
          style={{
            width: 64,
            height: 64,
            borderRadius: "50%",
            objectFit: "cover",
            marginRight: "1rem",
          }}
        />
        <Card.Body>
          <Card.Title>{twitchUser.display_name}</Card.Title>
          <Card.Text>{twitchUser.login}</Card.Text>
          <small>
            {isLive
              ? t("profile-data-status-live", { ns: "register" })
              : t("profile-data-status-not-live", { ns: "register" })}
          </small>
        </Card.Body>
      </div>
    </Card>
  );
};

export default ProfileCard;
