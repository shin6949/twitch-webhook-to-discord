import React from "react";
import { Container, Button } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHome } from "@fortawesome/free-solid-svg-icons";
import Link from "next/link";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { useTranslation } from "next-i18next";

export const getStaticProps = async ({ locale }: { locale: string }) => {
  return {
    props: {
      ...(await serverSideTranslations(locale, ["error", "common"])),
    },
  };
};

const ServerError: React.FC = (): JSX.Element => {
  const { t } = useTranslation(["error", "common"]);
  return (
    <Container className="text-center mt-5">
      <h1>{t("500_title", { ns: "error" })}</h1>
      <p>{t("500_description", { ns: "error" })}</p>
      <Link href="/" passHref>
        <Button variant="primary" className="my-3">
          <FontAwesomeIcon icon={faHome} /> {t("go_home_button", { ns: "error" })}
        </Button>
      </Link>
    </Container>
  );
};

export default ServerError;
