import React from "react";
import { Container } from "react-bootstrap";
import Head from "next/head";
import { Nav } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { GetStaticPropsContext } from "next";
import { faGithub } from "@fortawesome/free-brands-svg-icons";
import { useTranslation } from "next-i18next";

export const getStaticProps = async ({ locale }: GetStaticPropsContext) => ({
  props: {
    ...(await serverSideTranslations(locale as string, ["about"])),
  },
});

const About: React.FC = (): JSX.Element => {
  const { t } = useTranslation("about");

  return (
    <>
      <Head>
        <title>About</title>
      </Head>
      <Container>
        <h1>{t("main-program-name")}</h1>
        <h3>{t("sub-program-name")}</h3>
        <p>
          {t("developer")}
          <br />
          {t("description")}
        </p>
        <h2>{t("notification-types-title")}</h2>
        <p>
          {t("event-change-channel-information")}
          <br />
          {t("event-stream-online")}
          <br />
          {t("event-stream-offline")}
        </p>
        <h2>{t("program-source-informations")}</h2>
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
