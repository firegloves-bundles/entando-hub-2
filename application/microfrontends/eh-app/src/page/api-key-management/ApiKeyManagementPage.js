import { useEffect, useState, createRef } from 'react';
import {
  Content,
  DataTable,
  DataTableSkeleton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableRow,
  TableToolbar,
  TableToolbarContent,
  Button,
  InlineNotification,
} from 'carbon-components-react';
import {
  Add16 as AddIcon,
  Edit16 as EditIcon,
  Renew16 as RenewIcon,
  TrashCan16 as DeleteIcon,
  Copy16 as CopyIcon,
} from '@carbon/icons-react';

import GenerateApiKeyModal from './GenerateApiKeyModal';
import {
  getCatalogApiKeys,
  generateCatalogApiKey,
  updateCatalogApiKey,
  regenerateCatalogApiKey,
  deleteCatalogApiKey,
} from '../../integration/Integration';
import EhBreadcrumb from '../../components/eh-breadcrumb/EhBreadcrumb';
import './api-key-management-page.scss';
import i18n from '../../i18n';
import { SHOW_NAVBAR_ON_MOUNTED_PAGE } from '../../helpers/constants';
import { useApiUrl } from '../../contexts/ConfigContext';
import DeleteApiKeyModal from './DeleteApiKeyModal';
import EditApiKeyModal from './EditApiKeyModal';
import RegenerateApiKeyModal from './RegenerateApiKeyModal';
import { copyToClipboard } from '../../helpers/clipboard';

const headers = [
  {
   key: 'label',
   header: 'Label',
  },
  {
    key: 'apiKey',
    header: 'API key',
  },
];

const ApiKeyManagementPage = () => {
  const [apiKeys, setApiKeys] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [modal, setModal] = useState({
    id: null,
    data: null,
  });
  const [apiKeyToastOpen, setApiKeyToastOpen] = useState(false);

  const apiUrl = useApiUrl();

  const [apiKeyTextRefs, setApiKeyTextRefs] = useState({});

  useEffect(() => {
    (async () => {
      setIsLoading(true)
      const { data, isError } = await getCatalogApiKeys(apiUrl);
      if (!isError) {
        // convert each id to string since DataTable expects each row id to be of that type
        setApiKeys(data.map(d => ({
          ...d,
          id: `${d.id}`,
        })));
      }
      setIsLoading(false);
    })();
  }, [apiUrl]);

  const handleGenerateClick = () => {
    setModal({
      id: 'GenerateApiKeyModal',
    });
  };

  const handleModalClose = () => {
    setModal({
      id: null,
      data: null,
    });
  };

  const handleGenerateApiKeyModalSubmit = async (apiKeyData) => {
    const { data, isError } = await generateCatalogApiKey(apiUrl, apiKeyData);
    if (!isError) {
      setApiKeys([
        ...apiKeys,
        {
          label: apiKeyData.label,
          id: `${data.id}`,
          apiKey: data.apiKey,
        }
      ]);
  
      setApiKeyTextRefs({ ...apiKeyTextRefs, [data.id]: createRef() });
      setApiKeyToastOpen(true);
      handleModalClose();
    }
  };

  const handleDeleteApiKeyModalConfirm = async () => {
    const { isError } = await deleteCatalogApiKey(apiUrl, modal.data.id);
    if (!isError) {
      const updatedApiKeys = apiKeys.filter(({ id }) => id !== modal.data.id);
      setApiKeys(updatedApiKeys);
      handleModalClose();
    }
  };

  const handleRegenerateApiKeyModalConfirm = async () => {
    const { data, isError } = await regenerateCatalogApiKey(apiUrl, modal.data.id);
    if (!isError) {
      const updatedApiKeys = apiKeys.map(apiKey => (
        apiKey.id === modal.data.id ? ({
          ...apiKey,
          apiKey: data.apiKey,
        }) : apiKey
      ));

      if (!apiKeyTextRefs[modal.data.id]) {
        setApiKeyTextRefs({ ...apiKeyTextRefs, [modal.data.id]: createRef() });
      }

      setApiKeys(updatedApiKeys);
      setApiKeyToastOpen(true);
      handleModalClose();
    }
  };

  const handleEditApiKeyModalSubmit = async ({ id, label }) => {
    const { isError } = await updateCatalogApiKey(apiUrl, { label }, id);
    if (!isError) {
      const updatedApiKeys = apiKeys.map(apiKey => (
        apiKey.id === id ? ({
          ...apiKey,
          label,
        }) : apiKey
      ));

      setApiKeys(updatedApiKeys);
      handleModalClose();
    }
  };

  const handleEditClick = (rowData) => {
    const apiKeyData = {
      id: rowData.id,
      label: rowData.cells[0].value
    };

    setModal({
      id: 'EditApiKeyModal',
      data: apiKeyData,
    });
  };
  
  const handleRegenerateClick = (rowData) => {
    const apiKeyData = {
      id: rowData.id,
      label: rowData.cells[0].value
    };

    setModal({
      id: 'RegenerateApiKeyModal',
      data: apiKeyData,
    });
  };

  const handleDeleteClick = (rowData) => {
    const apiKeyData = {
      id: rowData.id,
      label: rowData.cells[0].value
    };
    
    setModal({
      id: 'DeleteApiKeyModal',
      data: apiKeyData,
    });
  };

  // selects the apiKey text and copies the apiKey to the clipboard
  const handleApiKeyClick = (apiKey, id) => {
    copyToClipboard(apiKey);

    let range = document.createRange();
    range.selectNodeContents(apiKeyTextRefs[id].current);
    let sel = window.getSelection();
    sel.removeAllRanges();
    sel.addRange(range);
  };

  const handleApiKeyToastClose = () => {
    setApiKeyToastOpen(false);
  };

  const rows = [...apiKeys].reverse();

  return (
    <>
      <Content className="ApiKeyManagementPage">
        <div className="ApiKeyManagementPage-wrapper">
        <div className="bx--row">
          <div className="bx--col-lg-16 ApiKeyManagementPage-breadcrumb">
            <EhBreadcrumb
            pathElements={[{
              path: i18n.t('navLink.apiKeyManagement'),
              page: SHOW_NAVBAR_ON_MOUNTED_PAGE.apiKeyManagementPage
            }]}
            />
          </div>
        </div>
        <div className="bx--row">
          <div className="bx--col-lg-16 ApiKeyManagementPage-section">
            {isLoading ? (
            <DataTableSkeleton columnCount={3} rowCount={10}/>
            ) : (
            <DataTable
              rows={rows}
              headers={headers}
            >
              {({
                rows,
                getTableProps,
                getRowProps,
              }) => (
                <TableContainer title={i18n.t('navLink.apiKeyManagement')}>
                  <TableToolbar>
                  <TableToolbarContent>
                    <Button onClick={handleGenerateClick} renderIcon={AddIcon}>{i18n.t('component.button.generateApiKey')}</Button>
                  </TableToolbarContent>
                  </TableToolbar>
                  {apiKeys.length === 0 ? (
                    <p className="ApiKeyManagementPage-empty-message">{i18n.t('page.apiKeys.empty')}</p>
                  ) : (
                    <Table {...getTableProps()}>
                      <TableBody>
                        {rows.map((row) => (
                          <TableRow {...getRowProps({ row })}>
                            <TableCell>
                              {row.cells[0].value}
                            </TableCell>
                            <TableCell>
                              {row.cells[1].value && (
                                <span className="ApiKeyManagementPage-api-key" onClick={() => handleApiKeyClick(row.cells[1].value, row.id)}>
                                  <span ref={apiKeyTextRefs[row.id]}>{row.cells[1].value}</span>
                                  <CopyIcon />
                                </span>
                              )}
                            </TableCell>
                            <TableCell align="right">
                              <Button
                                renderIcon={EditIcon}
                                kind="ghost"
                                iconDescription="Edit"
                                onClick={() => handleEditClick(row)}
                                hasIconOnly
                              />
                              <Button
                                renderIcon={RenewIcon}
                                kind="ghost"
                                iconDescription="Regenerate"
                                onClick={() => handleRegenerateClick(row)}
                                hasIconOnly
                              />
                              <Button
                                renderIcon={DeleteIcon}
                                kind="ghost"
                                iconDescription="Delete"
                                onClick={() => handleDeleteClick(row)}
                                hasIconOnly
                              />
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  )}
                </TableContainer>
              )}
            </DataTable>)}
          </div>
        </div>
        </div>
      </Content>
      <GenerateApiKeyModal
        open={modal.id === 'GenerateApiKeyModal'}
        onClose={handleModalClose}
        onSubmit={handleGenerateApiKeyModalSubmit}
      />
      <EditApiKeyModal
        open={modal.id === 'EditApiKeyModal'}
        onClose={handleModalClose}
        onSubmit={handleEditApiKeyModalSubmit}
        apiKeyData={modal.data}
      />
      <RegenerateApiKeyModal
        open={modal.id === 'RegenerateApiKeyModal'}
        onClose={handleModalClose}
        onConfirm={handleRegenerateApiKeyModalConfirm}
        apiKeyData={modal.data}
      />
      <DeleteApiKeyModal
        open={modal.id === 'DeleteApiKeyModal'}
        onClose={handleModalClose}
        onConfirm={handleDeleteApiKeyModalConfirm}
        apiKeyData={modal.data}
      />
      {apiKeyToastOpen && (
        <InlineNotification
          className="ApiKeyManagementPage-info-toast"
          kind="info"
          title={i18n.t('toasterMessage.apiKeyVisibility')}
          iconDescription=""
          lowContrast
          onClose={handleApiKeyToastClose}
        />
      )}
    </>
  )
};

export default ApiKeyManagementPage;
