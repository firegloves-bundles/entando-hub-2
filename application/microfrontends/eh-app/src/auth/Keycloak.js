import { useState, useEffect, createContext, useContext } from 'react';

const KeycloakContext = createContext(null);

const getKeycloakInstance = () =>
  (window &&
    window.entando &&
    window.entando.keycloak && {
      ...window.entando.keycloak,
      initialized: true,
    }) || { initialized: false };

export function KeycloakProvider({ children }) {
  const [instance, saveInstance] = useState(getKeycloakInstance());

  useEffect(() => {
    const refresh = () =>
      saveInstance({ ...getKeycloakInstance(), initialized: true });

    window.addEventListener('keycloak', refresh);
    
    return () => window.removeEventListener('keycloak', refresh);
  }, []);

  return (
    <KeycloakContext.Provider value={instance}>
      {instance.initialized && children}
    </KeycloakContext.Provider>
  );
}

export function useKeycloak() {
  return useContext(KeycloakContext);
}
