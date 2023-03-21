import React, { createContext, useState, useContext, useCallback, useMemo } from 'react';

import { getPrivateCatalogs } from '../integration/Integration';
import { useApiUrl } from './ConfigContext';

const CatalogContext = createContext();

export const CatalogProvider = ({ children }) => {
  const [catalogs, setCatalogs] = useState([]);

  const apiUrl = useApiUrl();

  const fetchCatalogs = useCallback(async () => {
    const { data: fetchedCatalogs, isError } = await getPrivateCatalogs(apiUrl);
    if (!isError) {
      setCatalogs(fetchedCatalogs);
    }
  }, [apiUrl]);

  const contextValue = useMemo(() => ({
    catalogs, fetchCatalogs, setCatalogs
  }), [catalogs, fetchCatalogs]);

  return (
    <CatalogContext.Provider value={contextValue}>
      {children}
    </CatalogContext.Provider>
  );
};

export const useCatalogs = () => {
  return useContext(CatalogContext);
};
