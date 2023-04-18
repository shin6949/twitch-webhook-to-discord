import React, { useEffect } from "react";
import { Navbar, Nav, Container } from "react-bootstrap";
import Link from "next/link";
import { useTranslation } from "next-i18next";

const Header: React.FC = (): JSX.Element => {
  const { t } = useTranslation(["common"]);

  useEffect(() => {
    if (typeof window !== "undefined") {
      require("bootstrap/dist/css/bootstrap.min.css");
      require("bootstrap/dist/js/bootstrap.bundle.min.js");
    }
  }, []);

  return (
    <Navbar bg="light" expand="lg" className="mb-3">
      <Container>
        <Link className="navbar-brand" href="/" passHref>
          {t("program-name", { ns: "common" })}
        </Link>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Link className="nav-link" href="/fcm-test" passHref>
              {t("test-menu", { ns: "common" })}
            </Link>
            <Link className="nav-link" href="/about" passHref>
              {t("about-menu", { ns: "common" })}
            </Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;
