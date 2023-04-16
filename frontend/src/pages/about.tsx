import React from "react";
import Header from "../components/Header";
import { Container } from "react-bootstrap";
import Head from "next/head";

const About: React.FC = (): JSX.Element => {
  return (
    <>
      <Head>
        <title>About</title>
      </Head>
      <Header />
      <Container>
        <h1>About 페이지입니다.</h1>
      </Container>
    </>
  );
};

export default About;
