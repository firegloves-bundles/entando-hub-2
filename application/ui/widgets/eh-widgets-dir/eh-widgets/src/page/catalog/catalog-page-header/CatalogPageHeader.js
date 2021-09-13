import React from 'react';
import Login from "../../../components/Login/Login";
import './catalog-page-header.scss';

const CatalogPageHeader = () => {
  return (
      <>
        <header className="CatalogPageHeader">
            <div className="CatalogPageHeader-header-top">
            <a href="#root-to-home" className="CatalogPageHeader-logo">
              <img src="/../../Logo.png" alt="Entando logo" />
            </a>
            <div className="CatalogPageHeader-header-right">
              <Login/>
            </div>
            </div>
            <div className="CatalogPageHeader-header-bottom">
              <div className="CatalogPageHeader-header-content">
                <h2>Welcome to Entando Hub</h2>
                Entando exists to help you build better apps faster by providing an
                Application Composition Platform for building enterprise apps on
                Kubernetes. We also have a growing hub of ready-to-use solutions to
                accelerate your development process further.
                You can take these solutions to use as-is, utilize them as a
                reference, or fork them to use as a base for your own custom
                solution.
              </div>
            </div>
        </header>
      </>
  )
}

export default CatalogPageHeader;
