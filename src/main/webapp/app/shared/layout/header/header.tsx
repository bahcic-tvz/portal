import './header.scss';

import React, { useState } from 'react';

import { Navbar, Nav, NavbarToggler, NavbarBrand, Collapse, Row, Col } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { NavLink as Link } from 'react-router-dom';
import LoadingBar from 'react-redux-loading-bar';

import { Home, Brand } from './header-components';
import { AdminMenu, EntitiesMenu, AccountMenu } from '../menus';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isSwaggerEnabled: boolean;
}

const Header = (props: IHeaderProps) => {
  const [menuOpen, setMenuOpen] = useState(false);

  const renderDevRibbon = () =>
    props.isInProduction === false ? (
      <div className="ribbon dev">
        <a href="">Development</a>
      </div>
    ) : null;

  const toggleMenu = () => setMenuOpen(!menuOpen);

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <Row>
      <Col lg={{size: 8, offset: 2}} md={{size: 10, offset: 1}} sm={{size: 12, offset: 0}}>
        <div id="app-header">
          {/*{renderDevRibbon()}*/}
          <LoadingBar className="loading-bar" />
          <Navbar expand="sm" style={{border: 'none'}}>
            <NavbarToggler aria-label="Menu" onClick={toggleMenu} />
            <Brand />
            <Collapse isOpen={menuOpen} navbar>
              <Nav id="header-tabs" className="ml-auto" navbar>
                {/*<Home />*/}
                {props.isAuthenticated && props.isAdmin && <EntitiesMenu />}
                {props.isAuthenticated && props.isAdmin && <AdminMenu showSwagger={props.isSwaggerEnabled} />}
                <AccountMenu isAuthenticated={props.isAuthenticated} />
              </Nav>
            </Collapse>
          </Navbar>
        </div>
      </Col>
    </Row>
  );
};

export default Header;
