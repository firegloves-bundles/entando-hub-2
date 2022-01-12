import React from 'react'
import {Breadcrumb, BreadcrumbItem} from "carbon-components-react"
import {Link} from "react-router-dom"
import './eh-breadcrumb.scss'
import i18n from '../../i18n'
import { ADMIN, SHOW_NAVBAR_ON_MOUNTED_PAGE } from '../../helpers/constants'
import { getHigherRole } from '../../helpers/helpers'
/*
{
path,
href
}
 */
const EhBreadcrumb = ({pathElements = []}) => {
  const currentPath = (pathElements && pathElements.length && pathElements[0].page) ? pathElements[0].page : '';
  const showNavBar = Object.values(SHOW_NAVBAR_ON_MOUNTED_PAGE).includes(currentPath) && getHigherRole() === ADMIN;

  const elementList = pathElements.map((pathElement, index) => {
    if (index === pathElements.length - 1) {
      return (
        <span key={index.toString()}>{pathElement.path}</span>
      )
    }
    return (<BreadcrumbItem key={index.toString()}>
      <Link to={pathElement.href}>{pathElement.path}</Link>
    </BreadcrumbItem>)
  })

  return (
    <Breadcrumb aria-label="Page navigation">
      <BreadcrumbItem>
        <Link to="/">{i18n.t("page.catlogPanel.catlogHomePage.home")}</Link>
      </BreadcrumbItem>
      {elementList}
      {showNavBar && (
        <div className="navigation-bar"
          style={currentPath === SHOW_NAVBAR_ON_MOUNTED_PAGE.isCatalogPage ? { "marginRight": "0rem" } : { "marginRight": "1.8rem" }}>
          <Link to="/admin">
            {i18n.t('navLink.userManagement')}
          </Link>
          <div className="vertical-divider"></div>
          <Link to="/category">
            {i18n.t('navLink.categoryManagement')}
          </Link>
          <div className="vertical-divider"></div>
          <Link to="/organisation">
            {i18n.t('navLink.organisationManagement')}
          </Link>
        </div>
      )}
    </Breadcrumb>
  );
}

export default EhBreadcrumb
