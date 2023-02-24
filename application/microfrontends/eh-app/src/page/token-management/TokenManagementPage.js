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

import GenerateTokenModal from './GenerateTokenModal';
import { getTokens, generateToken } from '../../integration/Integration';
import EhBreadcrumb from '../../components/eh-breadcrumb/EhBreadcrumb';
import './token-management-page.scss';
import i18n from '../../i18n';
import { SHOW_NAVBAR_ON_MOUNTED_PAGE } from '../../helpers/constants';
import { useApiUrl } from '../../contexts/ConfigContext';
import DeleteTokenModal from './DeleteTokenModal';
import EditTokenModal from './EditTokenModal';

const headers = [
  {
   key: 'name',
   header: 'Name',
  }
];

const TokenManagementPage = () => {
  const [tokens, setTokens] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [modal, setModal] = useState({
   id: null,
   data: null,
  });

  const apiUrl = useApiUrl;

  useEffect(() => {
   (async () => {
    setIsLoading(true)
    const { data } = await getTokens();
    setTokens(data);
    setIsLoading(false);
   })();
  }, []);

  const handleGenerateClick = () => {
   setModal({
    id: 'GenerateTokenModal',
   });
  };

  const handleModalClose = () => {
   setModal({
    id: null,
    data: null,
   });
  };

  const handleGenerateTokenModalSubmit = async (tokenData) => {
   // const { data, isError } = await generateToken(apiUrl, tokenData);
   console.log(tokenData);
  };

  const handleDeleteTokenModalSubmit = async () => {
    // const { isError } = await deleteToken(apiUrl, modal.data.id);
    console.log('confirmed delete');
  };

  const handleEditTokenModalSubmit = async (tokenData) => {
    // const { isError } = await updateToken(apiUrl, tokenData, tokenData.id);
    console.log('updated token');
  }

  const handleEditClick = (tokenData) => {
   console.log(tokenData);
   setModal({
    id: 'EditTokenModal',
    data: tokenData,
   });
  };

  const handleDeleteClick = (tokenData) => {
   console.log(tokenData);
   setModal({
    id: 'DeleteTokenModal',
    data: tokenData,
   });
  };

  return (
   <>
    <Content className="TokenManagmentPage">
      <div className="TokenManagmentPage-wrapper">
       <div className="bx--row">
        <div className="bx--col-lg-16 TokenManagmentPage-breadcrumb">
          <EhBreadcrumb
           pathElements={[{
            path: i18n.t('navLink.tokenManagement'),
            page: SHOW_NAVBAR_ON_MOUNTED_PAGE.tokenManagementPage
           }]}
          />
        </div>
       </div>
       <div className="bx--row">
        <div className="bx--col-lg-16 TokenManagmentPage-section">
          {isLoading ? (
          <DataTableSkeleton columnCount={3} rowCount={10}/>
          ) : (
          <DataTable 
            rows={tokens} 
            headers={headers}
          >
            {({
              rows,
              getTableProps,
              getRowProps,
            }) => (
              <TableContainer title={i18n.t('navLink.tokenManagement')}>
                <TableToolbar>
                <TableToolbarContent>
                  <Button onClick={handleGenerateClick} renderIcon={AddIcon}>{i18n.t('component.button.generateToken')}</Button>
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
    <GenerateTokenModal
      open={modal.id === 'GenerateTokenModal'}
      onClose={handleModalClose}
      onSubmit={handleGenerateTokenModalSubmit}
    />
    <EditTokenModal
      open={modal.id === 'EditTokenModal'}
      onClose={handleModalClose}
      onSubmit={handleEditTokenModalSubmit}
    />
    <DeleteTokenModal
      open={modal.id === 'DeleteTokenModal'}
      onClose={handleModalClose}
      onConfirm={handleDeleteTokenModalSubmit}
    />
   </>
  )
};

export default TokenManagementPage;
