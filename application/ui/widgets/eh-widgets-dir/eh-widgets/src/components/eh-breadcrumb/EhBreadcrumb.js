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
    const currentUrl = useHistory().location.pathname.split("/")[1]
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
      if (currentUrl === "admin")
        elementList = <span className="navigation-breadcrumb">User</span>;
      else if (currentUrl === "category")
        elementList = <span className="navigation-breadcrumb">Category</span>;
      else if (currentUrl === "organisation")
        elementList = (
          <span className="navigation-breadcrumb">Organisation</span>
        );
    }
    return (
      <Breadcrumb aria-label="Page navigation">
        <BreadcrumbItem>
          <Link to="/">{i18n.t("page.catlogPanel.catlogHomePage.home")}</Link>
        </BreadcrumbItem>
        {elementList}
        {getHigherRole() === ADMIN && (
          <div className="navigation-bar">
            <Link style={linkStyle} to="/admin">
              User Management
            </Link>
            <Link style={linkStyle} to="/category">
              Category Management
            </Link>
            <Link style={linkStyle} to="/organisation">
              Organisation Management
            </Link>
          </div>
        )}
      </Breadcrumb>
    );
}

export default EhBreadcrumb
