import { createContext, useContext } from 'react';

export const ConfigContext = createContext(null);

export const useConfig = () => {
  return useContext(ConfigContext);
};

export const useApiUrl = () => {
  const config = useConfig();
  const { systemParams } = config || {};
  const { api } = systemParams || {};
  return api && api['entando-hub-api'].url;
}
