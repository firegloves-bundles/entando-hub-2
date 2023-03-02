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
} from 'carbon-components-react';
import {
  Add16 as AddIcon,
  Edit16 as EditIcon,
  TrashCan16 as DeleteIcon,
  Copy16 as CopyIcon,
} from '@carbon/icons-react';

import GenerateApiKeyModal from './GenerateApiKeyModal';
import { getCatalogApiKeys, generateCatalogApiKey } from '../../integration/Integration';
import EhBreadcrumb from '../../components/eh-breadcrumb/EhBreadcrumb';
import './api-key-management-page.scss';
import i18n from '../../i18n';
import { SHOW_NAVBAR_ON_MOUNTED_PAGE } from '../../helpers/constants';
import { useApiUrl } from '../../contexts/ConfigContext';
import DeleteApiKeyModal from './DeleteApiKeyModal';
import EditApiKeyModal from './EditApiKeyModal';

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

        setApiKeyTextRefs(data.map(() => createRef()));
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
      setApiKeys([...apiKeys, { label: apiKeyData.label, apiKey: data.apiKey }]);
      setApiKeyTextRefs({ ...apiKeyTextRefs, [data.apiKey]: createRef() });
      handleModalClose();
    }
  };

  const handleDeleteApiKeyModalSubmit = async () => {
    // const { isError } = await deleteCatalogApiKey(apiUrl, modal.data.id);
    console.log('confirmed delete');
  };

  const handleEditApiKeyModalSubmit = async (apiKeyData) => {
    // const { isError } = await updateApiKey(apiUrl, apiKeyData, apiKeyData.id);
    console.log('updated api key');
  };

  const handleEditClick = (rowData) => {
    const apiKeyData = {
      id: rowData.id,
      label: rowData.cells[0].value
    };
    console.log(apiKeyData);
    setModal({
      id: 'EditApiKeyModal',
      data: apiKeyData,
    });
  };

  const handleDeleteClick = (rowData) => {
    const apiKeyData = {
      id: rowData.id,
      label: rowData.cells[0].value
    };
    console.log(apiKeyData);
    setModal({
      id: 'DeleteApiKeyModal',
      data: apiKeyData,
    });
  };

  // selects the apiKey text and copies the apiKey to the clipboard
  const handleApiKeyClick = (apiKey) => {
    let range = document.createRange();
    range.selectNodeContents(apiKeyTextRefs[apiKey].current);
    let sel = window.getSelection();
    sel.removeAllRanges();
    sel.addRange(range);
    navigator.clipboard.writeText(apiKey);
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
                                <span className="ApiKeyManagementPage-api-key" onClick={() => handleApiKeyClick(row.cells[1].value)}>
                                  <span ref={apiKeyTextRefs[row.cells[1].value]}>{row.cells[1].value}</span>
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
                                hasIconOnly />
                              <Button
                                renderIcon={DeleteIcon}
                                kind="ghost"
                                iconDescription="Delete"
                                onClick={() => handleDeleteClick(row)}
                                hasIconOnly />
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
      <DeleteApiKeyModal
        open={modal.id === 'DeleteApiKeyModal'}
        onClose={handleModalClose}
        onConfirm={handleDeleteApiKeyModalSubmit}
      />
    </>
  )
};

export default ApiKeyManagementPage;
