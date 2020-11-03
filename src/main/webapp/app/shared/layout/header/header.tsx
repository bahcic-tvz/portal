import './header.scss';

import React, { useState } from 'react';

import { Navbar, Nav, Button, Collapse, Row, Col, NavbarToggler } from 'reactstrap';
import LoadingBar from 'react-redux-loading-bar';
import {Brand, BrandIcon, Category} from './header-components';
import { AdminMenu, EntitiesMenu, AccountMenu } from '../menus';
import { useLocation } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isSwaggerEnabled: boolean;
}

// todo 0. ne dirati nista za admina dok ne dode vrijeme - DONE
// todo 1. slozit da button lijevo nije plus minus nego prava stvar i da se ne prikazuje kad nije mobile
// todo 2. fixati - zasto stalno imam vodoravni scroll bar
// todo 3. pogledati rute, namjestiti da i logiran i ne logiran user moze ic po kategorijama
// todo 4. namjestiti da homepage redirecta na /category/news
// todo 5. namjestiti da /logout redirecta na /category/news

const Header = (props: IHeaderProps) => {
  const [menuOpen, setMenuOpen] = useState(false);
  const { pathname: path } = useLocation()

  const renderDevRibbon = () =>
    props.isInProduction === false ? (
      <div className="ribbon dev">
        <a href="">Development</a>
      </div>
    ) : null;

  const toggleMenu = () => setMenuOpen(!menuOpen);
  const categories = [
    'news',
    'show',
    'sport',
    'lifestyle',
    'tech',
    'viral',
    'video',
    'sponsored'
  ]

  enum categoryEnum {
    news = '#D22328',
    show = '#F5A528',
    sport = '#40B14D',
    lifestyle = '#EFC20C',
    tech = '#47C0FF',
    viral = '#297AF6',
    video = '#9757F6',
    sponsored = '#47C0FF'
  }

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <Row>
      {renderDevRibbon()}
      <Col lg={{size: 8, offset: 2}} md={{size: 10, offset: 1}} sm={{size: 12, offset: 0}}>
        <div id="app-header">
          <LoadingBar className="loading-bar" />
          <Navbar expand="sm" style={{border: 'none'}}>
            <NavbarToggler onClick={toggleMenu}>
              <FontAwesomeIcon icon="bars" />
            </NavbarToggler>
            <Brand />
            <Collapse isOpen={menuOpen} navbar>
              <Nav id="header-tabs" className="mr-auto" navbar>
                {categories.map(category => (
                  <div onClick={() => setMenuOpen(false)}>
                    <Category
                      category={category}
                      color={categoryEnum[category]}
                      path={path}
                    />
                  </div>
                ))}
                <div style={{
                  paddingTop: '5px',
                  marginLeft: '20px'
                }}>
                  {/*<Home />*/}
                  {props.isAuthenticated && props.isAdmin && <EntitiesMenu />}
                  {props.isAuthenticated && props.isAdmin && <AdminMenu showSwagger={props.isSwaggerEnabled} />}
                  <AccountMenu isAuthenticated={props.isAuthenticated} />
                </div>
              </Nav>
            </Collapse>
          </Navbar>
        </div>
      </Col>
    </Row>
  );
};

export default Header;
