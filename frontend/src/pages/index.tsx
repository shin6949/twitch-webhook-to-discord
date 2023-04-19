import React from "react";
import Link from "next/link";
import { Container } from "react-bootstrap";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import { useTranslation } from "next-i18next";
import "bootstrap/dist/css/bootstrap.min.css";

export const getStaticProps = async ({ locale }: { locale: string }) => {
  return {
    props: {
      ...(await serverSideTranslations(locale, ["common"])),
    },
  };
};

const App = (): JSX.Element => {
  const { t } = useTranslation(["common"]);

  return (
    <>
      <Container className="my-4">
        <nav>
          <ul>
            <li>
              <Link href="/fcm-test">
                <div>{t("test-menu", { ns: "common" })}</div>
              </Link>
            </li>
            <li>
              <Link href="/about">
                <div>{t("about-menu", { ns: "common" })}</div>
              </Link>
            </li>
          </ul>
        </nav>
      </Container>
    </>
  );
};

export default App;
