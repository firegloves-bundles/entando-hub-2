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
const EhBreadcrumb = ({mountedPage, pathElements = []}) => {
    const showNavBar = Object.values(SHOW_NAVBAR_ON_MOUNTED_PAGE).includes(mountedPage) && getHigherRole() === ADMIN;
    const linkStyle = {
        margin: "4px",
        textDecoration: "none",
      };

    let elementList = pathElements.map((pathElement, index) => {
        if (index === pathElements.length - 1) {
            return (
                <span key={index.toString()}>{pathElement.path}</span>
            )
        }
        return (<BreadcrumbItem key={index.toString()}>
            <Link to={pathElement.href}>{pathElement.path}</Link>
        </BreadcrumbItem>)
    })

    if (!elementList.length) {
      if (SHOW_NAVBAR_ON_MOUNTED_PAGE.isUserManagementPage === mountedPage)
        elementList = <span className="navigation-breadcrumb">{i18n.t('navLink.userManagement')}</span>;
      else if (SHOW_NAVBAR_ON_MOUNTED_PAGE.isCategoryManagementPage === mountedPage)
        elementList = <span className="navigation-breadcrumb">{i18n.t('navLink.categoryManagement')}</span>;
      else if (SHOW_NAVBAR_ON_MOUNTED_PAGE.isOrganisationManagementPage === mountedPage)
        elementList = (
          <span className="navigation-breadcrumb">{i18n.t('navLink.organisationManagement')}</span>
        );
    }
    return (
      <Breadcrumb aria-label="Page navigation">
        <BreadcrumbItem>
          <Link to="/">{i18n.t("page.catlogPanel.catlogHomePage.home")}</Link>
        </BreadcrumbItem>
        {elementList}
        {showNavBar && (
          <div className="navigation-bar" style={mountedPage === SHOW_NAVBAR_ON_MOUNTED_PAGE.isCatalogPage ? {"marginRight": "1rem"} : {"marginRight": "1.8rem"}}>
            <Link style={linkStyle} to="/admin">
              {i18n.t('navLink.userManagement')}
            </Link><span style={linkStyle}>|</span>
            <Link style={linkStyle} to="/category">
              {i18n.t('navLink.categoryManagement')}
            </Link><span style={linkStyle}>|</span>
            <Link style={linkStyle} to="/organisation">
              {i18n.t('navLink.organisationManagement')}
            </Link>
          </div>
        )}
      </Breadcrumb>
    );
}

export default EhBreadcrumb
