import React from 'react';
import {Breadcrumb,BreadcrumbItem} from "carbon-components-react";
import Link from "carbon-components-react/lib/components/UIShell/Link";

import './eh-bredcrumb.scss';
/*
{
path,
href
}
 */
const EhBreadcrumb = ({pathElements=[]}) => {

    const elementList = pathElements.map((pathElement,index)=>{
        return (<BreadcrumbItem>
            <Link to={pathElement.href} isCurrentPage={index===pathElements.length-1}>{pathElement.path}</Link>
        </BreadcrumbItem>)
    })

    return (
        <Breadcrumb aria-label="Page navigation">
            <BreadcrumbItem>
                <Link to="/">Home</Link>
            </BreadcrumbItem>
            {elementList}
        </Breadcrumb>
    )
}

export default EhBreadcrumb;
