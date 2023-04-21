import React from "react";
import { Navbar, Nav, Container } from "react-bootstrap";
import Link from "next/link";
import { useTranslation } from "next-i18next";
import "bootstrap/dist/css/bootstrap.min.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFlask, faComment, faAddressCard } from "@fortawesome/free-solid-svg-icons";

const Header: React.FC = (): JSX.Element => {
  const { t } = useTranslation(["common"]);

  return (
    <Navbar bg="light" expand="lg" className="mb-3">
      <Container>
        <Link className="navbar-brand" href="/">
          {t("program-name", { ns: "common" })}
        </Link>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Link className="nav-link" href="/register">
              <FontAwesomeIcon icon={faComment} />
              {t("register-menu", { ns: "common" })}
            </Link>
            <Link className="nav-link" href="/fcm-test">
              <FontAwesomeIcon icon={faFlask} />
              {t("test-menu", { ns: "common" })}
            </Link>
            <Link className="nav-link" href="/about">
              <FontAwesomeIcon icon={faAddressCard} />
              {t("about-menu", { ns: "common" })}
            </Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;
