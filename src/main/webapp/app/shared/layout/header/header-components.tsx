import React from 'react';

import { NavItem, NavLink, NavbarBrand } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import appConfig from 'app/config/constants';

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src="content/images/portal-logo.svg" alt="Portal logo" />
  </div>
);

export const Brand = props => (
  <NavbarBrand tag={Link} to="/" className="brand-logo">
    <BrandIcon />
    {/*<span className="brand-title">Portal</span>*/}
    {/*<span className="navbar-version">{appConfig.VERSION}</span>*/}
  </NavbarBrand>
);

export const Home = props => (
  <NavItem>
    <NavLink tag={Link} to="/" className="d-flex align-items-center">
      <FontAwesomeIcon icon="home" />
      <span>Home</span>
    </NavLink>
  </NavItem>
);

export const Category = ({ category, color, path })  => {
  const active = path.includes(`/category/${category}`)
  return (
    <NavItem
      style={{
        listStyleType: 'none',
        textTransform: 'uppercase',
        borderBottom: `4px solid ${color}`,
        backgroundColor: active ? `${color}1A` : 'transparent'
      }}
    >
      <NavLink
        tag={Link}
        to={`/category/${category}`}
        className="d-flex align-items-center"
        style={{
          padding: '11px 16px',
          color: active ? color : 'black',
        }}
      >
        <span>{category}</span>
      </NavLink>
    </NavItem>
  )
}
