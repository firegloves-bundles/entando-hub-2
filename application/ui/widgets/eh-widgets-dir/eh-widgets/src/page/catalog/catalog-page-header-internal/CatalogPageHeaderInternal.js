import React from 'react';
import Login from "../../../components/Login/Login";
import './catalog-page-header-internal.scss';

const CatalogPageHeaderInternal = () => {
  return (
      <>
        <header className="CatalogPageHeader">
            <div className="CatalogPageHeader-header-top">
            <a href="#root-to-home" className="CatalogPageHeader-logo">
              <img src="/../../Logo.png" alt="Entando logo" />
            </a>
            </div>
        </header>
      </>
  )
}

export default CatalogPageHeaderInternal;
