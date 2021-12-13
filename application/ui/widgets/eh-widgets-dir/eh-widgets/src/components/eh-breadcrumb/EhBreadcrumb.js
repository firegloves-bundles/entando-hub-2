import React from 'react'
import {Breadcrumb, BreadcrumbItem} from "carbon-components-react"
import {Link} from "react-router-dom"
import './eh-breadcrumb.scss'
import i18n from '../../i18n'
/*
{
path,
href
}
 */
const EhBreadcrumb = ({pathElements = []}) => {

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
                {/* <Link to="/">Home</Link> */}
                <Link to="/">{i18n.t('page.catlogPanel.catlogHomePage.home')}</Link>
            </BreadcrumbItem>
            {elementList}
        </Breadcrumb>
    )
}

export default EhBreadcrumb
