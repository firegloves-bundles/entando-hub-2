import React, {useEffect, useState} from "react"

import {
  Content,
  DataTable,
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
import OrganisationManagementOverflowMenu from "./overflow-menu/OrganisationManagementOverflowMenu"
import {ModalAddNewOrganisation} from "./modal-add-new-user/ModalAddNewOrganisation"
import {getAllOrganisations,} from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb"
import "./organisation-managment-page.scss"

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
    key: "overflow",
    header: "",
  },
]

const OrganisationManagementPage = () => {
  const [reloadToken, setReloadToken] = useState(
    new Date().getTime().toString()
  )
  const [organisations, setOrganisations] = useState([])

  // fetches the users to show
  useEffect(() => {
    (async () => {
      const organisationList = (await getAllOrganisations()).organisationList;
      setOrganisations(organisationList.map(organisation=>{
        return {
          id: organisation.organisationId,
          ...organisation
        }
      }))
    })()
  }, [reloadToken])

  const onAfterSubmit = () => {
    setReloadToken(new Date().getTime().toString())
  }
  return (
    <>
      <Content className="UserManagementPage">
        <div className="UserManagementPage-wrapper">
          <div className="bx--row">
            <div className="bx--col-lg-16 UserManagementPage-breadcrumb">
              <EhBreadcrumb />
            </div>
          </div>
          <div className="bx--row">
            <div className="bx--col-lg-16 UserManagementPage-section">
              <DataTable rows={organisations} headers={headers}>
                {({
                  rows,
                  headers,
                  getTableProps,
                  getHeaderProps,
                  getRowProps,
                }) => (
                  <TableContainer title="Organisations Management">
                    <TableToolbar>
                      <TableToolbarContent>
                        <ModalAddNewOrganisation onAfterSubmit={onAfterSubmit} />
                      </TableToolbarContent>
                    </TableToolbar>
                    <Table {...getTableProps()}>
                      <TableHead>
                        <TableRow>
                          {headers.map((header) => (
                            <TableHeader {...getHeaderProps({ header })}>
                              {header.header}
                            </TableHeader>
                          ))}
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {rows.map((row) => (
                          <TableRow {...getRowProps({ row })}>
                            {row.cells.map((cell, index) => {
                              if (cell.id !== row.id + ":overflow") {
                                return (
                                  <TableCell key={cell.id}>
                                    {cell.value}
                                  </TableCell>
                                )
                              }
                              return (
                                <TableCell key={cell.id}>
                                  {console.log(row)}
                                  <OrganisationManagementOverflowMenu
                                    organisationObj={{
                                      organisationId: row.id,
                                      name: row.cells[0].value,
                                      description: row.cells[1].value
                                    }}
                                    onAfterSubmit={onAfterSubmit}
                                  />
                                </TableCell>
                              )
                            })}
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                )}
              </DataTable>
            </div>
          </div>
        </div>
      </Content>
    </>
  )
}

export default OrganisationManagementPage
