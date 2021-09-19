import React from 'react';
import './catalog-page-header-internal.scss';

const CatalogPageHeaderInternal = () => {
  return (
      <>
        <header className="CatalogPageHeader">
            <div className="CatalogPageHeader-header-top">
            <a href="#root-to-home" className="CatalogPageHeader-logo">
                <img src={`${process.env.REACT_APP_PUBLIC_ASSETS_URL}/Logo.png`} alt="Entando logo" />
            </a>
            </div>
        </header>
      </>
  )
}

export default CatalogPageHeaderInternal;
