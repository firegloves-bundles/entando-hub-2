import React, {useEffect, useState} from "react"

import {
  Content,
  DataTable,
  DataTableSkeleton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableHeader,
  TableRow,
  TableToolbar,
  TableToolbarContent,
} from "carbon-components-react"
import { Password16 as KeyIcon } from '@carbon/icons-react'
import OrganisationManagementOverflowMenu from "./overflow-menu/OrganisationManagementOverflowMenu"
import {ModalAddNewOrganisation} from "./modal-add-new-organisation/ModalAddNewOrganisation"
import { createPrivateCatalog, getAllOrganisations, getPrivateCatalogs } from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-breadcrumb/EhBreadcrumb"
import "./organisation-managment-page.scss"
import i18n from "../../i18n"
import { SHOW_NAVBAR_ON_MOUNTED_PAGE } from "../../helpers/constants"
import { useApiUrl } from "../../contexts/ConfigContext"
import { useHistory } from "react-router-dom"

/*
[
  {
    "name": "Entando inc.",
    "description": "Entando inc.",
    "bundleGroups": [
      "49",
      "51",
      "163",
      "52",
      "61",
      "141",
      "46",
      "142",
      "63",
      "143",
      "144"
    ],
    "organisationId": "1"
  },
  {
    "name": "Solving Team",
    "description": "Solving Team s.r.l",
    "bundleGroups": [
      "50"
    ],
    "organisationId": "2"
  }
]
 */

const headers = [
  {
    key: "name",
    header: "Name",
  },
  {
    key: "description",
    header: "Description",
  },
  {
    key: "privateCatalog",
    header: "Private Catalog",
  },
  {
    key: "overflow",
    header: "",
  },
]

const OrganisationManagementPage = () => {
    const [reloadToken, setReloadToken] = useState(
    new Date().getTime().toString()
  )
  const [organisations, setOrganisations] = useState([])
  const [isLoading, setIsLoading] = useState(true);

  const apiUrl = useApiUrl();

  const history = useHistory();

  // fetches the users to show
  useEffect(() => {
    (async () => {
      setIsLoading(true)
      const { organisationList } = await getAllOrganisations(apiUrl);

      const { data: privateCatalogs } = await getPrivateCatalogs(apiUrl);
      const orgPrivateCatalogMap = privateCatalogs.reduce((m, { id, organisationId }) => ({
        [organisationId]: id,
        ...m,
      }), {});
      
      setOrganisations(organisationList.map(organisation=>{
        return {
          id: organisation.organisationId,
          privateCatalog: orgPrivateCatalogMap[organisation.organisationId],
          ...organisation
        }
      }))

      setIsLoading(false)
    })()
  }, [apiUrl, reloadToken])

  const onAfterSubmit = () => {
    setReloadToken(new Date().getTime().toString())
  }

  const handleCreatePrivateCatalog = async (organisationId) => {
    setIsLoading(true);
    const { data, isError } = await createPrivateCatalog(apiUrl, organisationId);

    if (!isError) {
      setOrganisations(organisations.map(org => ({
        ...org,
        privateCatalog: +org.organisationId === data.organisationId ? data.id : org.privateCatalog,
      })));
    }
      
    setIsLoading(false);
  };

  const handleNavigatePrivateCatalog = (catalogId) => {
    history.push(`/catalog/${catalogId}/`);
  };

  return (
    <>
      <Content className="OrganizationManagmentPage">
        <div className="OrganizationManagmentPage-wrapper">
          <div className="bx--row">
            <div className="bx--col-lg-16 OrganizationManagmentPage-breadcrumb">
              <EhBreadcrumb
                pathElements={[{
                  path: i18n.t('navLink.organisationManagement'),
                  page: SHOW_NAVBAR_ON_MOUNTED_PAGE.isOrganisationManagementPage
                }]}
              />
            </div>
          </div>
          <div className="bx--row">
            <div className="bx--col-lg-16 OrganizationManagmentPage-section">
              {isLoading && <DataTableSkeleton columnCount={3} rowCount={4}/>}
              {!isLoading && (<DataTable rows={organisations} headers={headers}>
                {({
                  rows,
                  headers,
                  getTableProps,
                  getHeaderProps,
                  getRowProps,
                }) => (
                  <TableContainer title={i18n.t('page.management.organisationsManagement')}>
                    <TableToolbar>
                      <TableToolbarContent>
                        <ModalAddNewOrganisation onAfterSubmit={onAfterSubmit} />
                      </TableToolbarContent>
                    </TableToolbar>
                    <Table {...getTableProps()}>
                      <TableHead>
                        <TableRow>
                          {headers.map((header) => (
                            <TableHeader
                              {...getHeaderProps({ header })}
                              style={header.key === 'privateCatalog' ? { textAlign: 'center' } : {}}
                            >
                              {header.header ? i18n.t(`component.bundleModalFields.${header.key}`) : ''}
                            </TableHeader>
                          ))}
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {rows.map((row) => (
                          <TableRow {...getRowProps({ row })}>
                            {row.cells.map((cell) => {
                              if (cell.info.header === 'overflow') {
                                return (
                                  <TableCell key={cell.id}>
                                    <OrganisationManagementOverflowMenu
                                      organisationObj={{
                                        organisationId: row.id,
                                        name: row.cells[0].value,
                                        description: row.cells[1].value,
                                        privateCatalog: row.cells[2].value,
                                      }}
                                      onAfterSubmit={onAfterSubmit}
                                      setReloadToken={setReloadToken}
                                      onCreatePrivateCatalog={() => handleCreatePrivateCatalog(row.id)}
                                      onNavigatePrivateCatalog={() => handleNavigatePrivateCatalog(row.cells[2].value)}
                                    />
                                  </TableCell>
                                )
                              } else if (cell.info.header === 'privateCatalog') {
                                return (
                                  <TableCell key={cell.id} style={{ textAlign: 'center' }}>
                                    {cell.value && <KeyIcon />}
                                  </TableCell>
                                )
                              } else
                                return (
                                  <TableCell key={cell.id}>
                                    {cell.value}
                                  </TableCell>
                                )
                            })}
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
    </>
  )
}

export default OrganisationManagementPage
