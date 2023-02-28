import { useEffect, useState } from 'react';
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
   key: 'name',
   header: 'Name',
  }
];

const ApiKeyManagementPage = () => {
  const [apiKeys, setApiKeys] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [modal, setModal] = useState({
    id: null,
    data: null,
  });

  const apiUrl = useApiUrl;

  useEffect(() => {
    (async () => {
      setIsLoading(true)
      const { data } = await getCatalogApiKeys();
      setApiKeys(data);
      setIsLoading(false);
    })();
  }, []);

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
    // const { data, isError } = await generateCatalogApiKey(apiUrl, apiKeyData);
    console.log(apiKeyData);
  };

  const handleDeleteApiKeyModalSubmit = async () => {
    // const { isError } = await deleteCatalogApiKey(apiUrl, modal.data.id);
    console.log('confirmed delete');
  };

  const handleEditApiKeyModalSubmit = async (apiKeyData) => {
    // const { isError } = await updateApiKey(apiUrl, apiKeyData, apiKeyData.id);
    console.log('updated api key');
  }

  const handleEditClick = (rowData) => {
    const apiKeyData = {
      id: rowData.id,
      name: rowData.cells[0].value
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
      name: rowData.cells[0].value
    };
    console.log(apiKeyData);
    setModal({
      id: 'DeleteApiKeyModal',
      data: apiKeyData,
    });
  };

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
              rows={apiKeys} 
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
                  <Table {...getTableProps()}>
                    <TableBody>
                      {rows.map((row) => (
                        <TableRow {...getRowProps({ row })}>
                          {row.cells.map((cell) => (
                            <TableCell key={cell.id}>
                              {cell.value}
                            </TableCell>
                          ))}
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
