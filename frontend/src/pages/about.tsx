import React from "react";
import { Container } from "react-bootstrap";
import Head from "next/head";
import { Nav } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faGithub } from "@fortawesome/free-brands-svg-icons";

import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { useTranslation } from "next-i18next";

export const getStaticProps = async ({ locale }: { locale: string }) => {
  return {
    props: {
      ...(await serverSideTranslations(locale, ["about", "common"])),
    },
  };
};

const About: React.FC = (): JSX.Element => {
  const { t } = useTranslation(["about", "common"]);

  return (
    <>
      <Head>
        <title>About</title>
      </Head>
      <Container>
        <h1>{t("program-name", { ns: "common" })}</h1>
        <h3>{t("sub-program-name")}</h3>
        <p>
          {t("developer")}
          <br />
          {t("description")}
        </p>
        <h2>{t("notification-types-title")}</h2>
        <p>
          - {t("event-change-channel-information")}
          <br />- {t("event-stream-online")}
          <br />- {t("event-stream-offline")}
        </p>
        <h2>{t("program-source-information")}</h2>
        <Nav>
          <Nav.Link href="https://github.com/shin6949/twitch-webhook-to-discord">
            <FontAwesomeIcon icon={faGithub} /> {t("source")}
          </Nav.Link>
        </Nav>
      </Container>
    </>
  );
};

export default About;
