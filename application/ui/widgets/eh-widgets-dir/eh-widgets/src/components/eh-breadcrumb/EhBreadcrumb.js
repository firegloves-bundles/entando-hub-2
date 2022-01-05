import React from 'react'
import {Breadcrumb, BreadcrumbItem} from "carbon-components-react"
import {Link, useHistory} from "react-router-dom"
import './eh-breadcrumb.scss'
import i18n from '../../i18n'
import { ADMIN } from '../../helpers/constants'
import { getHigherRole } from '../../helpers/helpers'
/*
{
path,
href
}
 */
const EhBreadcrumb = ({pathElements = []}) => {
    const currentUrl = useHistory().location.pathname
    const showNavBar = currentUrl === '/' || currentUrl === '/admin' || currentUrl === '/category' || currentUrl === '/organisation';
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
      if (currentUrl === "/admin")
        elementList = <span className="navigation-breadcrumb">{i18n.t('navLink.userManagement')}</span>;
      else if (currentUrl === "/category")
        elementList = <span className="navigation-breadcrumb">{i18n.t('navLink.categoryManagement')}</span>;
      else if (currentUrl === "/organisation")
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
        {getHigherRole() === ADMIN && showNavBar && (
          <div className="navigation-bar" style={currentUrl === '/' ? {"marginRight": "3rem"} : {"marginRight": "1.8rem"}}>
            <Link style={linkStyle} to="/admin">
              {i18n.t('navLink.userManagement')}
            </Link>
            <Link style={linkStyle} to="/category">
              {i18n.t('navLink.categoryManagement')}
            </Link>
            <Link style={linkStyle} to="/organisation">
              {i18n.t('navLink.organisationManagement')}
            </Link>
          </div>
        )}
      </Breadcrumb>
    );
}

export default EhBreadcrumb
