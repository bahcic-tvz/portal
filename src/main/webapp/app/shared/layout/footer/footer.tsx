import './footer.scss';

import React from 'react';

import { Col, Row } from 'reactstrap';

const Footer = props => (
  <div className="footer page-content">
    <Row>
      <Col lg={{size: 8, offset: 2}} md={{size: 10, offset: 1}} sm={{size: 12, offset: 0}}>
        <p>Contact: <b>tbahcic@tvz.hr</b></p>
      </Col>
    </Row>
  </div>
);

export default Footer;
