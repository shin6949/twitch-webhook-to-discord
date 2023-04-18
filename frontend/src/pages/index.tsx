import React, { useEffect } from "react";
import Link from "next/link";
import { Container } from "react-bootstrap";

const App = (): JSX.Element => {
  useEffect(() => {
    if (typeof window !== "undefined") {
      require("bootstrap/dist/css/bootstrap.min.css");
      require("bootstrap/dist/js/bootstrap.bundle.min.js");
    }
  }, []);

  return (
    <>
      <Container className="my-4">
        <nav>
          <ul>
            <li>
              <Link href="/fcm-test" passHref>
                <div>FCMTest</div>
              </Link>
            </li>
            <li>
              <Link href="/about" passHref>
                <div>About</div>
              </Link>
            </li>
          </ul>
        </nav>
      </Container>
    </>
  );
};

export default App;
