import './header.scss';

import React, { useState } from 'react';

import { Navbar, Nav, Button, Collapse, Row, Col } from 'reactstrap';
import LoadingBar from 'react-redux-loading-bar';
import { Brand, Category } from './header-components';
import { AdminMenu, EntitiesMenu, AccountMenu } from '../menus';
import { useLocation } from 'react-router-dom';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isSwaggerEnabled: boolean;
}

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
            <Button onClick={toggleMenu} color={'white'}>{menuOpen ? '-' : '+'}</Button>
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
