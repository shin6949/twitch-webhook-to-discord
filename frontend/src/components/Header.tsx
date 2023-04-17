import React, { useEffect } from "react";
import { Navbar, Nav, Container } from "react-bootstrap";
import Link from "next/link";

const Header: React.FC = (): JSX.Element => {
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
          트위치 알리미
        </Link>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Link className="nav-link" href="/fcm-test" passHref>
              기기 알림 테스트
            </Link>

            <Link className="nav-link" href="/about" passHref>
              About
            </Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;
